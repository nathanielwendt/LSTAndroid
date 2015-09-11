package com.ut.mpc.lstrtree.evals;

import android.content.Context;
import android.util.Log;

import com.ut.mpc.etch.Eval;
import com.ut.mpc.etch.MultiProfiler;
import com.ut.mpc.etch.Stabilizer;
import com.ut.mpc.lstrtree.SQLiteNaive;
import com.ut.mpc.lstrtree.SQLiteRTree;
import com.ut.mpc.setup.Constants;
import com.ut.mpc.utils.GPSLib;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;
import com.ut.mpc.utils.STStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SuperGrid implements Eval {

    private static final String TAG = SuperGrid.class.getSimpleName();

    @Override
    public void execute(Context ctx, JSONObject options) throws JSONException {
        String dbName = options.getString("dbName");
        String dataType = options.getString("dataType");

        //Number of nonzero windows to take values for before exiting out of loop
        //This is an upper limit, not lower limit as the loop may terminate before this value is reached
        int numWindows = Integer.valueOf(options.getString("numWindows"));

        //number of window poks to duplicate in the inner timing loop.  Remember to divide by this number to get
        //actual energy/execution time
        int duplicateCount = Integer.valueOf(options.getString("duplicateCount"));

        //Don't use the file name other than to print to the csv file output
        final String fileName = options.getString("fileName");

        String structType = options.getString("structType");
        boolean kdTree = Boolean.valueOf(options.getString("kdTree"));

        STStorage helper, other;
        boolean isRTree = ("SQLiteRTree").equals(structType);
        if(isRTree){
            Log.d(TAG, "setting up db type: SQLiteRTree");
            helper = new SQLiteRTree(ctx, "RTreeMain");
            other = new SQLiteNaive(ctx, "SpatialTableMain");
            other.clear();
        } else {
            Log.d(TAG, "setting up db type: SpatialTableMain");
            helper = new SQLiteNaive(ctx, "SpatialTableMain");
            other = new SQLiteNaive(ctx, "RTreeMain");
            other.clear();
        }
        final LSTFilter lstFilter = new LSTFilter(helper);

        if(kdTree){
            Log.d(TAG, "setting KDTree Cache ON");
            lstFilter.setKDCache(true);
        } else {
            lstFilter.setKDCache(false);
            Log.d(TAG, "setting KDTree Cache OFF");
        }

        //SQLiteRTree helper = new SQLiteRTree(ctx, "RTreeMain");
        //final LSTFilter lstFilter = new LSTFilter(helper);
        //lstFilter.setKDCache(true);

        STPoint minBounds, maxBounds;
        float xStep, yStep, tStep;
        if("cabs".equals(dataType)){
            Log.d(TAG, "setting up data type: Cabs");
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
            Log.d(TAG, "setting up data type: Mobility");
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

        Log.d(TAG, "database size is: " + lstFilter.getSize());

        Stabilizer stabFunc = new Stabilizer(){
            @Override
            public void task(Object data) {
                lstFilter.windowPoK((STRegion) data);
            }
        };

        String dbNameFull = dbName + "_" +
                fileName + "_" +
                structType + "_" +
                numWindows + "_" +
                duplicateCount + "_" +
                kdTree;

        MultiProfiler.init(this, ctx);
        MultiProfiler.startProfiling(dbNameFull);
        int nonZeroCount = 0;
        int totalCount = 0;
        double val = 0.0;

        List<Double> poks = new ArrayList<Double>();
        List<Integer> numCandPoints = new ArrayList<Integer>();
        List<String> pointIndices = new ArrayList<String>();

        outerloop:
        for(float x = minBounds.getX(); x < maxBounds.getX(); x+= xStep){
            for(float y = minBounds.getY(); y < maxBounds.getY(); y+= yStep){
                for(float t = minBounds.getT(); t < maxBounds.getT(); t+= tStep) {
                    STRegion region = new STRegion(new STPoint(x,y,t), new STPoint(x + xStep,y + yStep,t + tStep));
                    MultiProfiler.startMark(stabFunc, region, "loop:" + totalCount);
                    for(int i = 0; i < duplicateCount; i++){
                        val = lstFilter.windowPoK(region);
                    }
                    MultiProfiler.endMark("loop:" + totalCount);

                    if(val > 0.001){
                        nonZeroCount++;

                        //update stats lists
                        pointIndices.add("loop:" + totalCount);
                        poks.add(val);
                        numCandPoints.add(helper.range(new STRegion(new STPoint(x,y,t), new STPoint(x + xStep,y + yStep,t + tStep))).size());
                    }
                    totalCount++;
                    if(nonZeroCount >= numWindows){ break outerloop; }
                }
            }
        }
        MultiProfiler.stopProfiling();
        Log.d(TAG, "Done Profiling >>>>>>>>>");
        Log.d(TAG, "Loop count is: " + totalCount);
//        List<Double> poks = new ArrayList<Double>();
//        List<Integer> numCandPoints = new ArrayList<Integer>();
//        secondOuterLoop:
//        for(float x = minBounds.getX(); x < maxBounds.getX(); x+= xStep){
//            for(float y = minBounds.getY(); y < maxBounds.getY(); y+= yStep){
//                for(float t = minBounds.getT(); t < maxBounds.getT(); t+= tStep) {
//                    poks.add(lstFilter.windowPoK(new STRegion(new STPoint(x,y,t), new STPoint(x + xStep,y + yStep,t + tStep))));
//                    numCandPoints.add(helper.range(new STRegion(new STPoint(x,y,t), new STPoint(x + xStep,y + yStep,t + tStep))).size());
//                    totalCount--;
//                    if(totalCount <= 0){ break secondOuterLoop; }
//                }
//            }
//        }
        for (int start = 0; start < poks.size(); start += 400) {
            int end = Math.min(start + 400, poks.size());
            List<Double> sublist = poks.subList(start, end);
            Log.d(TAG, sublist.toString());
        }
        Log.d(TAG, pointIndices.toString());
        Log.d(TAG, numCandPoints.toString());
        Log.d(TAG, "---------------------------- end of eval --------------------");
    }
}
