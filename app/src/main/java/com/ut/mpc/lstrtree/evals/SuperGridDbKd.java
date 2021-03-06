package com.ut.mpc.lstrtree.evals;

import android.content.Context;

import com.ut.mpc.etch.Eval;
import com.ut.mpc.etch.MultiProfiler;
import com.ut.mpc.etch.Stabilizer;
import com.ut.mpc.lstrtree.SQLiteNaive;
import com.ut.mpc.setup.Constants;
import com.ut.mpc.utils.GPSLib;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic window query operation
 * <li>dbTag - tag to give in name of database for this run</li>
 */
public class SuperGridDbKd implements Eval {

    private static final String TAG = SuperGridDbKd.class.getSimpleName();

    @Override
    public void execute(Context ctx, JSONObject options) throws JSONException {
        String dbTag = options.getString("dbTag");
        String dataType = options.getString("dataType");

        SQLiteNaive helper = new SQLiteNaive(ctx, "SpatialTableMain");
        final LSTFilter lstFilter = new LSTFilter(helper);
        lstFilter.setKDCache(true);

        STPoint minBounds, maxBounds;
        float xStep, yStep, tStep;
        if("cabs".equals(dataType)){
            System.out.println("setting up data type: Cabs");
            Constants.setCabDefaults();
            float spaceGrid = 10; // 10 km
            float timeGrid = 60 * 60 * 24 * 7; // one week (in seconds)
            STRegion bounds = helper.getBoundingBox();
            minBounds = bounds.getMins();
            maxBounds = bounds.getMaxs();
            STPoint cube = new STPoint(GPSLib.longOffsetFromDistance(minBounds, spaceGrid), GPSLib.latOffsetFromDistance(minBounds, spaceGrid), timeGrid);
            xStep = cube.getX();
            yStep = cube.getY();
            tStep = cube.getT();
        } else {
            System.out.println("setting up data type: Mobility");
            Constants.setMobilityDefaults();
            float spaceGrid = 500; // 500m
            float timeGrid = 60 * 60 * 3; // 10 hours (in seconds)
            STRegion bounds = helper.getBoundingBox();
            minBounds = bounds.getMins();
            maxBounds = bounds.getMaxs();
            STPoint cube = new STPoint(spaceGrid, spaceGrid, timeGrid);
            xStep = cube.getX();
            yStep = cube.getY();
            tStep = cube.getT();
        }

        Stabilizer stabFunc = new Stabilizer(){
            @Override
            public void task(Object data) {
                lstFilter.windowPoK((STRegion) data);
            }
        };

        MultiProfiler.init(this, ctx);
        MultiProfiler.startProfiling(TAG + dbTag);
        int nonZeroCount = 0;
        int totalCount = 0;
        double val = 0.0;
        outerloop:
        for(float x = minBounds.getX(); x < maxBounds.getX(); x+= xStep){
            for(float y = minBounds.getY(); y < maxBounds.getY(); y+= yStep){
                for(float t = minBounds.getT(); t < maxBounds.getT(); t+= tStep) {
                    STRegion region = new STRegion(new STPoint(x,y,t), new STPoint(x + xStep,y + yStep,t + tStep));
                    MultiProfiler.startMark(stabFunc, region, x + "," + y + "," + t);
                    for(int i = 0; i < 3; i++){
                        val = lstFilter.windowPoK(region);
                    }
                    MultiProfiler.endMark(x + "," + y + "," + t);
                    if(val > 0.001){
                        nonZeroCount++;
                        System.out.println("incremented count");
                    }
                    totalCount++;
                    if(nonZeroCount >= 20){ break outerloop; }
                }
            }
        }
        MultiProfiler.stopProfiling();

        System.out.println("Loop count is : " + totalCount);
        List<Double> poks = new ArrayList<Double>();
        List<Integer> numCandPoints = new ArrayList<Integer>();
        secondOuterLoop:
        for(float x = minBounds.getX(); x < maxBounds.getX(); x+= xStep){
            for(float y = minBounds.getY(); y < maxBounds.getY(); y+= yStep){
                for(float t = minBounds.getT(); t < maxBounds.getT(); t+= tStep) {
                    poks.add(lstFilter.windowPoK(new STRegion(new STPoint(x,y,t), new STPoint(x + xStep,y + yStep,t + tStep))));
                    numCandPoints.add(helper.range(new STRegion(new STPoint(x,y,t), new STPoint(x + xStep,y + yStep,t + tStep))).size());
                    totalCount--;
                    if(totalCount <= 0){ break secondOuterLoop; }
                }
            }
        }
        System.out.println("DONE PROFILING >>>>>>>");
        for (int start = 0; start < poks.size(); start += 400) {
            int end = Math.min(start + 400, poks.size());
            List<Double> sublist = poks.subList(start, end);
            System.out.println(sublist);
        }
        System.out.println(numCandPoints);
    }
}
