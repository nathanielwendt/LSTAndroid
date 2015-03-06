package com.example.nathanielwendt.lstrtree.evals;

import android.content.Context;
import android.os.Bundle;

import com.example.nathanielwendt.lstrtree.SQLiteRTree;
import com.ut.mpc.utils.GPSLib;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;

import java.util.ArrayList;
import java.util.List;

import profiler.MultiProfiler;
import profiler.Stabilizer;

/**
 * Basic window query operation
 * <li>numGrid - number of grids along each dimension</li>
 */
public class WindowSuperGrid implements Eval, Stabilizer {

    private static final String TAG = SampleEval.class.getSimpleName();
    private static STRegion firstRun;
    private static LSTFilter lstFilter;

    @Override
    public void execute(Context ctx, Bundle options){
        int numGrid = Integer.valueOf(options.getString("numGrid"));

        String pref = options.getString("pref");
        if (pref == null) {
            pref = "normal.pref";
        }

        SQLiteRTree helper = new SQLiteRTree(ctx, "RTreeMain");
        lstFilter = new LSTFilter(helper);

        STRegion bounds = helper.getBoundingBox();
        STPoint minBounds = bounds.getMins();
        STPoint maxBounds = bounds.getMaxs();

        firstRun = new STRegion(new STPoint(minBounds.getX(),minBounds.getY(),minBounds.getT()), new STPoint(minBounds.getX(),minBounds.getY(),minBounds.getT()));

        float spaceGrid = 10;
        float timeGrid = 1000 * 60 * 60 * 6;
        STPoint cube = new STPoint(GPSLib.longOffsetFromDistance(minBounds, spaceGrid), GPSLib.latOffsetFromDistance(minBounds, spaceGrid), timeGrid);
        float xStep = cube.getX();
        float yStep = cube.getY();
        float tStep = cube.getT();

        MultiProfiler.init(this, ctx);
        MultiProfiler.startProfiling(TAG);
        for(int i = 0; i < 10; i++){
            MultiProfiler.startMark(TAG);
            for(float x = minBounds.getX(); x < maxBounds.getX(); x+= xStep){
                for(float y = minBounds.getY(); y < maxBounds.getY(); y+= yStep){
                    for(float t = minBounds.getT(); t < maxBounds.getT(); t+= tStep) {
                        lstFilter.windowPoK(new STRegion(new STPoint(x,y,t), new STPoint(x + xStep,y + yStep,t + tStep)));
                    }
                }
            }
            MultiProfiler.endMark(TAG);
        }
        MultiProfiler.stopProfiling();

        List<Double> poks = new ArrayList<Double>();
        List<Integer> numCandPoints = new ArrayList<Integer>();
        double result;
        for(float x = minBounds.getX(); x < maxBounds.getX(); x+= xStep){
            for(float y = minBounds.getY(); y < maxBounds.getY(); y+= yStep){
                for(float t = minBounds.getT(); t < maxBounds.getT(); t+= tStep) {
                    numCandPoints.add(helper.range(new STRegion(new STPoint(x,y,t), new STPoint(x + xStep,y + yStep,t + tStep))).size());
                    poks.add(lstFilter.windowPoK(new STRegion(new STPoint(x,y,t), new STPoint(x + xStep,y + yStep,t + tStep))));
                }
            }
        }

        System.out.println(poks);
        System.out.println(numCandPoints);
    }

    @Override
    public void execute(Context ctx){
        Bundle options = new Bundle();
        options.putString("numGrid", "10");
        execute(ctx, options);
    }

    @Override
    public void task() {
        lstFilter.windowPoK(firstRun);
    }
}
