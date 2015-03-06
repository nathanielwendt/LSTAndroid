package com.example.nathanielwendt.lstrtree.evals;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import profiler.*;
/**
 * Created by Aurelius on 3/5/15.
 */
public class SampleEval implements Eval {

    private static final String TAG = SampleEval.class.getSimpleName();

    @Override
    public void execute(Context ctx, Bundle options) {
        Log.d(TAG, "::execute");
        MultiProfiler.init(this, ctx);
        //MultiProfiler.loadPrefs("/sdcard/trepn/saved_preferences/normal.pref");
        MultiProfiler.startProfiling("sampleTest");

        MultiProfiler.startMark(new Stabilizer() {
            @Override
            public void task(Object data) {
                // sample test here
                long squareSum = 0;
                for (int i=0 ; i<10000000; ++i) {
                    squareSum += i*i;
                }
            }
        }, null, TAG);

        long squareSum = 0;
        for (int i=0 ; i<10000000; ++i) {
            squareSum += i*i;
        }

        MultiProfiler.endMark(TAG);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MultiProfiler.startMark(new Stabilizer() {
            @Override
            public void task(Object data) {
                // sample test here
                long squareSum = 0;
                for (int i=0 ; i<10000000; ++i) {
                    squareSum += i*i;
                }
            }
        }, null, TAG);

        squareSum = 0;
        for (int i=0 ; i<10000000; ++i) {
            squareSum += i*i;
        }

        MultiProfiler.endMark(TAG);

        MultiProfiler.stopProfiling();

    }


    @Override
    public void execute(Context ctx) {
        execute(ctx, null);
    }

}
