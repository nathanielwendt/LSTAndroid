package com.example.nathanielwendt.lstrtree.evals;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import profiler.*;
/**
 * Created by Aurelius on 3/5/15.
 */
public class SampleEval implements Eval, MultiProfiler.Stabilizer {

    private static final String TAG = SampleEval.class.getSimpleName();

    @Override
    public void execute(Context ctx, Bundle options) {
        Log.d(TAG, "::execute");
        MultiProfiler.init(this, ctx);
        MultiProfiler.loadPrefs("/sdcard/trepn/saved_preferences/testPref.pref");
        MultiProfiler.startProfiling("sampleTest");

        MultiProfiler.startMark("randomLoop");

        long squareSum = 0;
        for (int i=0; i<1000000000; ++i) {
            squareSum += i*i;
        }
        MultiProfiler.endMark("randomLoop");

        MultiProfiler.startMark("randomLoop2");

        squareSum = 0;
        for (int i=0; i<1000000000; ++i) {
            squareSum += i*i;
        }

        MultiProfiler.endMark("randomLoop2");

        MultiProfiler.stopProfiling();

    }


    @Override
    public void execute(Context ctx) {
        execute(ctx, null);
    }

    @Override
    public void task() {
        // sample test here
        long squareSum = 0;
        for (int i=0 ; i<10000000; ++i) {
            squareSum += i*i;
        }
    }
}
