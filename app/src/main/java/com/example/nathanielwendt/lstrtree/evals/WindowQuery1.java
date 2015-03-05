package com.example.nathanielwendt.lstrtree.evals;

import android.content.Context;

import com.example.nathanielwendt.lstrtree.SQLiteRTree;
import com.example.nathanielwendt.lstrtree.benchmark.DBPrepare;
import com.ut.mpc.utils.LSTFilter;

public class WindowQuery1 implements Eval {

    @Override
    public void execute(Context ctx, String options){
        SQLiteRTree helper = new SQLiteRTree(ctx, "RTreeMain");
        LSTFilter lstFilter = new LSTFilter(helper);

        DBPrepare.populateDB(lstFilter, "/sdcard/Crawdad/" + options, 1000);
    }

    @Override
    public void execute(Context ctx){
        execute(ctx, "new_abboip.txt");
    }

}
