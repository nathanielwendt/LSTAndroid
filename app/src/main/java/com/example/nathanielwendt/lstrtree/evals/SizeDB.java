package com.example.nathanielwendt.lstrtree.evals;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.nathanielwendt.lstrtree.SQLiteRTree;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STRegion;

/**
 * Gets size of current database
 */
public class SizeDB implements Eval {

    @Override
    public void execute(Context ctx, Bundle options){
        SQLiteRTree helper = new SQLiteRTree(ctx, "RTreeMain");
        LSTFilter lstFilter = new LSTFilter(helper);

        STRegion bounds = helper.getBoundingBox();
        Log.d("LST", "size of db is" + lstFilter.getSize());
    }

    @Override
    public void execute(Context ctx){
        execute(ctx,  null);
    }

}
