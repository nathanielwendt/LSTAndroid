package com.example.nathanielwendt.lstrtree.evals;

import android.content.Context;
import android.os.Bundle;

import com.example.nathanielwendt.lstrtree.SQLiteRTree;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic window query operation
 * <li>numGrid - number of grids along each dimension</li>
 */
public class WindowSuperGrid implements Eval {

    @Override
    public void execute(Context ctx, Bundle options){
        int numGrid = Integer.valueOf(options.getString("numGrid"));

        SQLiteRTree helper = new SQLiteRTree(ctx, "RTreeMain");
        LSTFilter lstFilter = new LSTFilter(helper);

        STRegion bounds = helper.getBoundingBox();
        System.out.println(bounds);
        STPoint minBounds = bounds.getMins();
        STPoint maxBounds = bounds.getMaxs();

        List<STPoint> points = helper.range(bounds);
        System.out.println("points back is " + points.size());

        float xStep = (maxBounds.getX() - minBounds.getX()) / numGrid;
        float yStep = (maxBounds.getY() - minBounds.getY()) / numGrid;
        float tStep = (maxBounds.getT() - minBounds.getT()) / numGrid;

        List<Double> results = new ArrayList<Double>();
        for(float x = minBounds.getX(); x < maxBounds.getX(); x+= xStep){
            for(float y = minBounds.getY(); y < maxBounds.getY(); y+= yStep){
                for(float t = minBounds.getT(); t < maxBounds.getT(); t+= tStep) {
                    double result = lstFilter.windowPoK(new STRegion(new STPoint(x,y,t), new STPoint(x + xStep,y + yStep,t + tStep)));
                    results.add(result);
                }
            }
        }

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
