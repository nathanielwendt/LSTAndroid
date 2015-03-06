package com.example.nathanielwendt.lstrtree.evals;

import android.content.Context;
import android.os.Bundle;

import com.example.nathanielwendt.lstrtree.SQLiteRTree;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;

import java.util.ArrayList;
import java.util.List;

import profiler.MultiProfiler;

/**
 * Basic window query operation
 * <li>numGrid - number of grids along each dimension</li>
 */
public class WindowSuperGrid implements Eval {

    private static final String TAG = SampleEval.class.getSimpleName();

    @Override
    public void execute(Context ctx, Bundle options){
        int numGrid = Integer.valueOf(options.getString("numGrid"));

        String pref = options.getString("pref");
        if (pref == null) {
            pref = "normal.pref";
        }

        SQLiteRTree helper = new SQLiteRTree(ctx, "RTreeMain");
        LSTFilter lstFilter = new LSTFilter(helper);

        STRegion bounds = helper.getBoundingBox();
        STPoint minBounds = bounds.getMins();
        STPoint maxBounds = bounds.getMaxs();

        float xStep = (maxBounds.getX() - minBounds.getX()) / numGrid;
        float yStep = (maxBounds.getY() - minBounds.getY()) / numGrid;
        float tStep = (maxBounds.getT() - minBounds.getT()) / numGrid;

        List<Double> results = new ArrayList<Double>();

        MultiProfiler.init(this, ctx);
        MultiProfiler.loadPrefs("/sdcard/trepn/saved_preferences/"+pref);
        MultiProfiler.startProfiling(TAG);

        MultiProfiler.startMark(TAG);

        for(float x = minBounds.getX(); x < maxBounds.getX(); x+= xStep){
            for(float y = minBounds.getY(); y < maxBounds.getY(); y+= yStep){
                for(float t = minBounds.getT(); t < maxBounds.getT(); t+= tStep) {
                    double result = lstFilter.windowPoK(new STRegion(new STPoint(x,y,t), new STPoint(x + xStep,y + yStep,t + tStep)));
                    results.add(result);
                }
            }
        }

        MultiProfiler.endMark(TAG);

        MultiProfiler.stopProfiling();

        for(Double result : results){
            System.out.println(result);
        }
    }

    @Override
    public void execute(Context ctx){
        Bundle options = new Bundle();
        options.putString("numGrid", "10");
        execute(ctx, options);
    }

}
