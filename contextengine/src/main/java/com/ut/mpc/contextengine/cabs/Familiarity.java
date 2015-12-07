package com.ut.mpc.contextengine.cabs;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.nathanielwendt.pacolib.Cabs;
import com.example.nathanielwendt.pacolib.samples.ApplicationStateData;
import com.example.nathanielwendt.pacolib.samples.ContextSample;
import com.example.nathanielwendt.pacolib.samples.GpsData;
import com.example.nathanielwendt.pacolib.samples.Sample;
import com.example.nathanielwendt.pacolib.samples.SensorSample;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by nathanielwendt on 12/2/15.
 */
public class Familiarity extends Cabs {
    public static final String ID = "com.ut.mpc.contextengine.cabs.familiarity";

    private static float LONG_KM_EST = .009f;
    private static float LAT_KM_EST = .001f;
    private static float time_border = 3600; //in seconds
    private static float km_border = 1;
    private int count = 0;
    private List<String> knownApplications = new ArrayList<String>();

    @Override
    public void init() {
        knownApplications.add("com.snapchat");
        knownApplications.add("com.instagram");

        Log.d("Familiarity", "starting familiarity");
        PacoMediator mediator = new PacoMediator();
        Observable.combineLatest(mediator.gps(), mediator.applicationStates(), (x, y) -> checkFamiliarity(x, y)).subscribe();
        mediator.submit();
    }

    public Sample checkFamiliarity(Sample gpsSample, Sample applicationStateSample){
        SensorSample gpsSensorSample = new SensorSample(gpsSample);
        GpsData gpsData = (GpsData) gpsSensorSample.data();

        SensorSample applicationStateSensorSample = new SensorSample(applicationStateSample);
        ApplicationStateData appData = (ApplicationStateData) applicationStateSensorSample.data();

        count = 0;
        for(String appForeground: appData.appForegrounds){
            if(knownApplications.contains(appForeground)){
                count++;
            }
        }

        count = count / 5; //normalize
        if(count > 1){
            count = 1;
        }

        Intent intent = new Intent("com.ut.mpc.lstindex");
        intent.putExtra("action", "windowPoK");
        intent.putExtra("minLongitude", (float) gpsData.longitude - (LONG_KM_EST * km_border));
        intent.putExtra("minLatitude", (float) gpsData.latitude - (LAT_KM_EST * km_border));
        intent.putExtra("maxLongitude", (float) gpsData.longitude + (LONG_KM_EST * km_border));
        intent.putExtra("maxLatitude", (float) gpsData.latitude + (LONG_KM_EST * km_border));
        intent.putExtra("minTimestamp", (float) System.currentTimeMillis() - time_border);
        intent.putExtra("maxTimestamp", (float) System.currentTimeMillis() + time_border);

        sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle results = getResultExtras(true);
                double pok = results.getDouble("pok");

                double score = (pok + count) / 2;

                double accuracy;
                if(pok >= count){
                    accuracy = 1.0;
                } else {
                    accuracy = pok / count;
                }
                ContextSample cSample = new ContextSample(accuracy, getId(), new Data(score));
                onNext(cSample);
            }
        }, null, Activity.RESULT_OK, null, null);

        return null;
    }

    public class Data {
        public double familiarity;

        public Data(double familiarity){
            this.familiarity = familiarity;
        }
    }

    @Override
    public String getDisplayName() {
        return "Familiarity";
    }

    @Override
    public String getId() {
        return ID;
    }
}
