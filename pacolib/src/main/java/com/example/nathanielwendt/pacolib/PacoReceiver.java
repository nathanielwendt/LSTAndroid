package com.example.nathanielwendt.pacolib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by nathanielwendt on 8/13/15.
 */
public class PacoReceiver extends BroadcastReceiver {
    private static String TAG = "PacoReceiver";

    private static String PACO_PREFS_ID = "PacoPreferences";
    private static String PACO_INIT_TAG = "PacoInitialized";
    protected static String packageId;
    protected static List<PacoObservable> obs = new ArrayList<PacoObservable>();
    protected static List<Action1> actions = new ArrayList<Action1>();
    private static int subscriberCount = 0;

    public static PacoObservable<ContextItem> getContextStream(){
        return new PacoObservable<ContextItem>();
    }

    public static void addSubscriber(PacoObservable<ContextItem> ob){
        obs.add(ob);
        actions.add(ob.getSubscribeFunc());
    }

    public static void setPackageId(String id){
        packageId = id;
    }

    public static void register(Context context){
        Log.d(TAG, "registering");
        SharedPreferences prefs = context.getSharedPreferences(PACO_PREFS_ID, Context.MODE_PRIVATE);

        //check registration with external paco context engine app
        boolean isRegistered = prefs.getBoolean(PACO_INIT_TAG, false);
        //if(isRegistered){
        //    return;
        //}

        //check that user appropriately set up local observers and subscriber function callbacks
        if(!isInit()){
            throw new RuntimeException("must add subscribers and set package id before registering");
        }

        Intent intent = new Intent();
        intent.setAction("com.ut.mpc.CONTEXT_ENGINE");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("action", "register");
        intent.putExtra("packageId", packageId);
        intent.putExtra("observables", (Serializable) obs);
        Log.d(TAG, "sending broadcast");
        context.sendBroadcast(intent);

        SharedPreferences.Editor editor = prefs.edit();

        //TODO: change this, just a temporary false value for testing, should be 'true' here
        editor.putBoolean(PACO_INIT_TAG, false);
        editor.apply();
    }

    public static boolean isInit(){
        return (obs != null && actions != null && packageId != null);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BroadcastSubscriber", "received message");

        if(!isInit()){
            return;
        }

        String action = intent.getStringExtra("action");
        if(action.equals("update")){
            int indexId = intent.getExtras().getInt("obsIndex");
            ContextItem item = (ContextItem) intent.getExtras().getSerializable("context");
            actions.get(indexId).call(item);
        }
    }
}
