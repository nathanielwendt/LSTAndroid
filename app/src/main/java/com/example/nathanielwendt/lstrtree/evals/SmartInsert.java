package com.example.nathanielwendt.lstrtree.evals;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.nathanielwendt.lstrtree.SQLiteNaive;
import com.example.nathanielwendt.lstrtree.SQLiteRTree;
import com.example.nathanielwendt.lstrtree.benchmark.DBPrepare;
import com.ut.mpc.setup.Constants;
import com.ut.mpc.utils.GPSLib;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import profiler.MultiProfiler;
import profiler.Stabilizer;

/**
 * Basic window query operation
 * <li> file - name of file to insert data </li>
 * <li> numPoints - number of data points to insert from beginning of file</li>
 * <li> append - True/False for appending this dataset to db or clearing it first</li>
 * <li> type - SQLiteRTree for rtree, anything else for NaiveTableStore </li>
 */
public class SmartInsert implements Eval {

    private static final String TAG = SuperGridRKd.class.getSimpleName();

    @Override
    public void execute(Context ctx, Bundle options){
        int numPoints = Integer.valueOf(options.getString("numPoints"));
        final String fileName = options.getString("file");
        boolean append = Boolean.valueOf(options.getString("append"));
        final double smartInsVal = Double.valueOf(options.getString("smartInsVal"));

        SQLiteRTree helper = new SQLiteRTree(ctx, "RTreeMain");
        SQLiteNaive other = new SQLiteNaive(ctx, "SpatialTableMain");
        other.clear();

        Constants.SmartInsert.INS_THRESH = smartInsVal;
        final LSTFilter lstFilter = new LSTFilter(helper);
        lstFilter.setSmartInsert(true);

        if(!append){
            lstFilter.clear();
        }

        Stabilizer stabFunc = new Stabilizer(){
            @Override
            public void task(Object data) {
                DBPrepare.populateDB(lstFilter, "/sdcard/Crawdad/" + fileName, 25, smartInsVal);
            }
        };

        MultiProfiler.init(this, ctx);
        MultiProfiler.startProfiling(TAG + fileName + smartInsVal);
        MultiProfiler.startMark(stabFunc, null, "SI");
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            line = br.readLine();
            String[] split = line.split(" ");
            STPoint point = new STPoint(Float.valueOf(split[1]),Float.valueOf(split[0]),Float.valueOf(split[3]));

            int count = 1;
            lstFilter.insert(point);
            while (((line = br.readLine()) != null) && count < numPoints) {
                split = line.split(" ");
                point = new STPoint(Float.valueOf(split[1]),Float.valueOf(split[0]),Float.valueOf(split[3]));
                lstFilter.insert(point);
                count++;
            }
            br.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        MultiProfiler.endMark("SI");


        STRegion bounds = helper.getBoundingBox();
        STPoint minBounds = bounds.getMins();
        STPoint maxBounds = bounds.getMaxs();

        float spaceGrid = 10; // 10 km
        float timeGrid = 60 * 60 * 24 * 7; // one week
        STPoint cube = new STPoint(GPSLib.longOffsetFromDistance(minBounds, spaceGrid), GPSLib.latOffsetFromDistance(minBounds, spaceGrid), timeGrid);
        float xStep = cube.getX();
        float yStep = cube.getY();
        float tStep = cube.getT();

        List<Double> poks = new ArrayList<Double>();
        List<Integer> numCandPoints = new ArrayList<Integer>();
        int count = 15;
        MultiProfiler.startMark(stabFunc, null, "Window");
        outerloop:
        for(float x = minBounds.getX(); x < maxBounds.getX(); x+= xStep){
            for(float y = minBounds.getY(); y < maxBounds.getY(); y+= yStep){
                for(float t = minBounds.getT(); t < maxBounds.getT(); t+= tStep) {
                    poks.add(lstFilter.windowPoK(new STRegion(new STPoint(x,y,t), new STPoint(x + xStep,y + yStep,t + tStep))));
                    numCandPoints.add(helper.range(new STRegion(new STPoint(x,y,t), new STPoint(x + xStep,y + yStep,t + tStep))).size());
                    count--;
                    if(count <= 0){ break outerloop; }
                }
            }
        }
        MultiProfiler.endMark("Window");
        MultiProfiler.stopProfiling();

        Log.d(TAG, "Populated Database table with size: " + helper.getSize());
        Log.d(TAG, "Cleared other table with size: " + other.getSize());
    }

    @Override
    public void execute(Context ctx){
        Bundle options = new Bundle();
        options.putString("file", "new_abboip.txt");
        options.putString("numPoints", "1000");
        options.putString("append", "False");
        options.putString("smartInsVal", String.valueOf(DBPrepare.smartInsOffVal));
        execute(ctx, options);
    }

}
