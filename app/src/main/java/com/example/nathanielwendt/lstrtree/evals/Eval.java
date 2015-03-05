package com.example.nathanielwendt.lstrtree.evals;

import android.content.Context;

/**
 * Created by nathanielwendt on 3/4/15.
 */
public interface Eval {
    public void execute(Context ctx, String options);
    public void execute(Context ctx);
}
