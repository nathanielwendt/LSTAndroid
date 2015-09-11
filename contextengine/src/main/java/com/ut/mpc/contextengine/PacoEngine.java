package com.ut.mpc.contextengine;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.example.nathanielwendt.pacolib.ContextItem;
import com.example.nathanielwendt.pacolib.PacoObservable;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by nathanielwendt on 8/18/15.
 */
public class PacoEngine extends Service {

    private final String TAG = "PacoEngine";
    private String ENGINE_POSTFIX = ".CONTEXT_ENGINE";
    RegisterReceiver regReceiver;

    Observable<ContextItem> sensorObservable = Observable.create(
            new Observable.OnSubscribe<ContextItem>() {
                @Override
                public void call(Subscriber<? super ContextItem> sub) {
                    sub.onNext(new ContextItem(10));
                    sub.onNext(new ContextItem(20));
                    sub.onNext(new ContextItem(10));
                    sub.onCompleted();
                }
            }
    );

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Starting paco engine");
        IntentFilter filter = new IntentFilter(RegisterReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        regReceiver = new RegisterReceiver();
        registerReceiver(regReceiver, filter);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(regReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Incoming registration requests come through this regReceiver
    public class RegisterReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.ut.mpc.CONTEXT_ENGINE";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Registering new application");
            String action = intent.getStringExtra("action");
            if(("register").equals(action)){
                String packageId = intent.getStringExtra("packageId");
                List<PacoObservable> observables = (ArrayList <PacoObservable>) intent.getSerializableExtra("observables");

                int index = 0;
                for(PacoObservable obs : observables){
                    Observable<ContextItem> temp = Observable.empty();
                    temp = temp.mergeWith(sensorObservable);

                    List<Func1> filterFuncs = obs.getFilterFuncs();
                    List<Func1> mapFuncs = obs.getMapFuncs();

                    for(int i =0; i < filterFuncs.size(); i++){
                        temp = temp.filter(filterFuncs.get(i));
                    }

                    for(int i =0; i < mapFuncs.size(); i++){
                        temp = temp.map(mapFuncs.get(i));
                    }

                    final int tempIndex = index;
                    temp.subscribe(contextItem -> broadcastIntent(packageId,tempIndex,contextItem));
                    //Observable.merge(temp, sensorObservable).subscribe(contextItem -> broadcastIntent(packageId,tempIndex,contextItem));
                    index++;
                }
            }
        }
    }

    // broadcast a custom intent.
    public void broadcastIntent(String appId, int obsId, ContextItem contextItem){
        Log.d(TAG, "broadcasting intent to: " + appId + " and obsId: " + obsId);
        Intent intent = new Intent();
        intent.setAction(appId + ENGINE_POSTFIX);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("action", "update");
        intent.putExtra("context", contextItem);
        intent.putExtra("obsIndex", obsId);
        sendBroadcast(intent);
    }
}
