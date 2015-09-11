package com.ut.mpc.lstrtree.evals;

import android.content.Context;

import com.ut.mpc.etch.Eval;
import com.ut.mpc.etch.MultiProfiler;
import com.ut.mpc.etch.Stabilizer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nathanielwendt on 3/28/15.
 */
public class EtchTest implements Eval {

    public int doit(){
        int doit = 0;
        for(int i = 1; i < 100; i++){
            doit += i / (i * 9) * (i * i * i) / 9000;
        }
        return doit;
    }

    @Override
    public void execute(Context ctx, JSONObject options) throws JSONException {
        Stabilizer stabFunc = new Stabilizer(){
            @Override
            public void task(Object data) {
                doit();
            }
        };

        String dbName = options.getString("dbName");
        int iterations = Integer.valueOf(options.getString("iterations"));

        MultiProfiler.init(this, ctx);
        MultiProfiler.startProfiling(dbName);
        MultiProfiler.startMark(stabFunc, null, "First");
        for(int i = 0; i < iterations; i++){
            doit();
        }
        MultiProfiler.endMark("First");

        MultiProfiler.startMark(stabFunc, null, "Second");
        for(int i = 0; i < iterations; i++){
            doit();
        }
        MultiProfiler.endMark("Second");

        MultiProfiler.stopProfiling();
    }
}
