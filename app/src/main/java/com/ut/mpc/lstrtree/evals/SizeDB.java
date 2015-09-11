package com.ut.mpc.lstrtree.evals;

import android.content.Context;
import android.util.Log;

import com.ut.mpc.etch.Eval;
import com.ut.mpc.lstrtree.SQLiteRTree;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STRegion;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Gets size of current database
 *
 */
public class SizeDB implements Eval {

    @Override
    public void execute(Context ctx, JSONObject options) throws JSONException {
        SQLiteRTree helper = new SQLiteRTree(ctx, "RTreeMain");
        LSTFilter lstFilter = new LSTFilter(helper);

        STRegion bounds = helper.getBoundingBox();
        Log.d("LST", "size of db is" + lstFilter.getSize());
    }
}
