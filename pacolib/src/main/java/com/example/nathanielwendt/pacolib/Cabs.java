package com.example.nathanielwendt.pacolib;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.example.nathanielwendt.pacolib.samples.ContextSample;
import com.example.nathanielwendt.pacolib.samples.Sample;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

import static com.example.nathanielwendt.pacolib.PacoConsts.Actions;
import static com.example.nathanielwendt.pacolib.PacoConsts.CONTEXT_ENGINE;
import static com.example.nathanielwendt.pacolib.PacoConsts.Sensors;

/**
 * Created by nathanielwendt on 11/18/15.
 */
public abstract class Cabs extends Service {
    protected List<String> subscriptions = new ArrayList<>();
    protected BroadcastReceiver mediatorReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return Service.START_STICKY;
    }

    public void onNext(ContextSample contextSample){
        for(String sub: subscriptions){
            Intent intent = new Intent();
            intent.setAction(sub);
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.putExtra("sample", contextSample);
            intent.putExtra("action", Actions.CabUpdate);
            intent.putExtra("cabId", getId());
            sendBroadcast(intent);
        }
    }

    public abstract void init();

    public abstract String getDisplayName();

    public abstract String getId();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mediatorReceiver);
    }

    public class PacoMediator {
        public List<String> sensorSubs = new ArrayList<>();
        public List<String> cabSubs = new ArrayList<>();

        public Observable<Sample> allSamples = Observable.create(
                new Observable.OnSubscribe<Sample>() {

                    @Override
                    public void call(Subscriber<? super Sample> sub) {
                        IntentFilter broadcastFilter = new IntentFilter();
                        broadcastFilter.addAction(getId());
                        mediatorReceiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                String action = intent.getStringExtra("action");
                                if(Actions.CabCabReg.equals(action)) {
                                    Log.d(getId(), "Incoming Cab Reg");
                                    String id = intent.getStringExtra("cabId");
                                    subscriptions.add(id);
                                } else if(Actions.CabUpdate.equals(action)){
                                    Log.d(getId(), "Incoming Cab Update");
                                    Sample contextSample = (Sample) intent.getParcelableExtra("sample");
                                    sub.onNext(contextSample);
                                } else if(Actions.PacoUpdate.equals(action)){
                                    Sample sensorSample = (Sample) intent.getParcelableExtra("sample");
                                    sub.onNext(sensorSample);
                                }
                            }
                        };

                        registerReceiver(mediatorReceiver, broadcastFilter);
                    }
                }
        );

        public Observable<Sample> applicationStates(){
            sensorSubs.add(Sensors.ApplicationStates);
            return allSamples.filter(sensItem -> sensItem.isType(Sensors.ApplicationStates));
        }

        public Observable<Sample> gps(){
            sensorSubs.add(Sensors.Gps);
            return allSamples.filter(sensItem -> sensItem.isType(Sensors.Gps));
        }

        public Observable<Sample> accelerometer(){
            sensorSubs.add(Sensors.Accelerometer);
            return allSamples.filter(sensItem -> sensItem.isType(Sensors.Accelerometer));
        }

        public Observable<Sample> bluetooth(){
            sensorSubs.add(Sensors.Bluetooth);
            return allSamples.filter(sensItem -> sensItem.isType(Sensors.Bluetooth));
        }

        public Observable<Sample> communication(){
            sensorSubs.add(Sensors.Communication);
            return allSamples.filter(sensItem -> sensItem.isType(Sensors.Communication));
        }

        public Observable<Sample> cab(String filterTag){
            cabSubs.add(filterTag);
            return allSamples.filter(sensItem -> sensItem.isType(filterTag));
        }

        public void submit(){
            //Register for sensor streams with Paco Engine
            Intent regIntent = new Intent();
            regIntent.setAction(CONTEXT_ENGINE);
            regIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            regIntent.putExtra("action", Actions.CabPacoReg);
            regIntent.putExtra("cabId", getId());
            regIntent.putExtra("displayName", getDisplayName());
            regIntent.putExtra("numSensors", sensorSubs.size());
            int count = 1;
            for(String sens: sensorSubs){
                regIntent.putExtra("sensor" + String.valueOf(count), sens);
                count++;
            }
            sendBroadcast(regIntent);

            //Register for cab streams with each cab subscription
            for(String cab: cabSubs){
                Intent cabRegIntent = new Intent();
                cabRegIntent.setAction(cab);
                cabRegIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                cabRegIntent.putExtra("action", Actions.CabCabReg);
                cabRegIntent.putExtra("cabId", getId());
                cabRegIntent.putExtra("displayName", getDisplayName());
                sendBroadcast(cabRegIntent);
            }

        }
    }
}
