package com.example.nathanielwendt.lstrtree.evals;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by nathanielwendt on 3/4/15.
 */
public interface Eval {
    public final String TAG = "EVAL";

    public void execute(Context ctx, Bundle options);
    public void execute(Context ctx);
}
