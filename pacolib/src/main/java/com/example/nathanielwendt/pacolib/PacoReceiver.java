package com.example.nathanielwendt.pacolib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.nathanielwendt.pacolib.samples.ContextSample;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    protected static List<PacoObservable<ContextSample>> obs = new ArrayList<PacoObservable<ContextSample>>();
    protected static List<Action1> actions = new ArrayList<Action1>();
    private static int subscriberCount = 0;
    protected static CustomObject obj;

    public static PacoObservable<ContextSample> getContextStream(){
        return new PacoObservable<ContextSample>();
    }

    public static void addSubscriber(PacoObservable<ContextSample> ob){
        obs.add(ob);
        //actions.add(ob.getSubscribeFunc());
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

//        List<CustomObject> custom = new ArrayList<CustomObject>();
//        custom.add(new CustomObject() {
//
//            @Override
//            public void doit(int x) {
//                System.out.println("from original class this value is: " + x);
//            }
//        });

//
//        List<PacoObservable<ContextItem>> obsOut = copyObs(obs);
//
//        Class c1 = obs.get(0).getFilterFuncs().get(0).getClass();
//        Log.d(TAG, c1.getPackage().toString());
//        Class c2 = obsOut.get(0).getFilterFuncs().get(0).getClass();
//        Log.d(TAG, c2.getPackage().toString());



        intent.putExtra("obj", obj);
        Log.d(TAG, "sending broadcast");
        context.sendBroadcast(intent);

        SharedPreferences.Editor editor = prefs.edit();

        //TODO: change this, just a temporary false value for testing, should be 'true' here
        editor.putBoolean(PACO_INIT_TAG, false);
        editor.apply();
    }

    public static void doit(CustomObject obj){
        PacoReceiver.obj = obj;
    }

    private static Object copyObs(Object obj){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            bos.close();
            byte[] byteData = bos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
            List<PacoObservable<ContextSample>> obsOut = (List<PacoObservable<ContextSample>>) new ObjectInputStream(bais).readObject();
            return obsOut;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
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
            ContextSample item = (ContextSample) intent.getExtras().getSerializable("context");
            actions.get(indexId).call(item);
        }
    }
}
