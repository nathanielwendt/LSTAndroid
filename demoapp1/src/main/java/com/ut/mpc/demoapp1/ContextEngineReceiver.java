package com.ut.mpc.demoapp1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;

import rx.Observable;

/**
 * Created by nathanielwendt on 8/13/15.
 */
public abstract class ContextEngineReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, android.content.Intent intent) {
        Log.d("BroadcastSubscriber", "received message");
        onStreamUpdate(Observable.just(intent.getStringExtra("test")));
    }

    public abstract void onStreamUpdate(Observable<java.lang.String> obs);
}
