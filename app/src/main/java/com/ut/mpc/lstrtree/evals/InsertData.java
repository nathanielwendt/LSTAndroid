package com.ut.mpc.lstrtree.evals;

import android.content.Context;
import android.util.Log;

import com.ut.mpc.etch.Eval;
import com.ut.mpc.lstrtree.SQLiteNaive;
import com.ut.mpc.lstrtree.SQLiteRTree;
import com.ut.mpc.lstrtree.benchmark.DBPrepare;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STStorage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Basic window query operation
 * <li> file - name of file to insert data </li>
 * <li> numPoints - number of data points to insert from beginning of file</li>
 * <li> append - True/False for appending this dataset to db or clearing it first</li>
 * <li> type - SQLiteRTree for rtree, anything else for NaiveTableStore </li>
 */
public class InsertData implements Eval {

    @Override
    public void execute(Context ctx, JSONObject options) throws JSONException {
        int numPoints = Integer.valueOf(options.getString("numPoints"));
        String fileName = options.getString("file");
        boolean append = Boolean.valueOf(options.getString("append"));
        String type = options.getString("type");
        String dataType = options.getString("dataType");

        STStorage helper, other;
        boolean isRTree = ("SQLiteRTree").equals(type);
        if(isRTree){
            helper = new SQLiteRTree(ctx, "RTreeMain");
            other = new SQLiteNaive(ctx, "SpatialTableMain");
            other.clear();
        } else {
            helper = new SQLiteNaive(ctx, "SpatialTableMain");
            other = new SQLiteNaive(ctx, "RTreeMain");
            other.clear();
        }
        LSTFilter lstFilter = new LSTFilter(helper);

        if(!append){
            lstFilter.clear();
        }

        boolean isCabs = ("cabs").equals(dataType);
        DBPrepare.populateDB(lstFilter, "/sdcard/Crawdad/" + fileName, numPoints, DBPrepare.smartInsOffVal, isCabs);

        if("cabs".equals(dataType)){
            System.out.println("setting up data type: Cabs");

        } else {
            System.out.println("setting up data type: Mobility");
        }


        Log.d(TAG, "Is R Tree?: " + isRTree);
        Log.d(TAG, "Populated Database table with size: " + helper.getSize());
        Log.d(TAG, "Cleared other table with size: " + other.getSize());
    }
}
