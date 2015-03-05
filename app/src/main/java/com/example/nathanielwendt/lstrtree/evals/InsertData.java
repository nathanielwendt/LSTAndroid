package com.example.nathanielwendt.lstrtree.evals;

import android.content.Context;
import android.os.Bundle;

import com.example.nathanielwendt.lstrtree.SQLiteRTree;
import com.example.nathanielwendt.lstrtree.benchmark.DBPrepare;
import com.ut.mpc.utils.LSTFilter;

/**
 * Basic window query operation
 * <li> file - name of file to insert data </li>
 * <li> numPoints - number of data points to insert from beginning of file</li>
 */
public class InsertData implements Eval {

    @Override
    public void execute(Context ctx, Bundle options){
        int numPoints = Integer.valueOf(options.getString("numPoints"));
        String fileName = options.getString("file");

        SQLiteRTree helper = new SQLiteRTree(ctx, "RTreeMain");
        LSTFilter lstFilter = new LSTFilter(helper);

        DBPrepare.populateDB(lstFilter, "/sdcard/Crawdad/" + fileName, numPoints);
    }

    @Override
    public void execute(Context ctx){
        Bundle options = new Bundle();
        options.putString("file", "new_abboip.txt");
        options.putString("numPoints", "1000");
        execute(ctx, options);
    }

}
