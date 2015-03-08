package com.example.nathanielwendt.lstrtree.evals;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.nathanielwendt.lstrtree.SQLiteNaive;
import com.example.nathanielwendt.lstrtree.SQLiteRTree;
import com.example.nathanielwendt.lstrtree.benchmark.DBPrepare;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STStorage;

/**
 * Basic window query operation
 * <li> file - name of file to insert data </li>
 * <li> numPoints - number of data points to insert from beginning of file</li>
 * <li> append - True/False for appending this dataset to db or clearing it first</li>
 * <li> type - SQLiteRTree for rtree, anything else for NaiveTableStore </li>
 */
public class InsertData implements Eval {

    @Override
    public void execute(Context ctx, Bundle options){
        int numPoints = Integer.valueOf(options.getString("numPoints"));
        String fileName = options.getString("file");
        boolean append = Boolean.valueOf(options.getString("append"));
        String type = options.getString("type");

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
        DBPrepare.populateDB(lstFilter, "/sdcard/Crawdad/" + fileName, numPoints, DBPrepare.smartInsOffVal);

        Log.d(TAG, "Is R Tree?: " + isRTree);
        Log.d(TAG, "Populated Database table with size: " + helper.getSize());
        Log.d(TAG, "Cleared other table with size: " + other.getSize());
    }

    @Override
    public void execute(Context ctx){
        Bundle options = new Bundle();
        options.putString("file", "new_abboip.txt");
        options.putString("numPoints", "1000");
        options.putString("append", "False");
        options.putString("type", "SQLiteRTree");
        execute(ctx, options);
    }

}
