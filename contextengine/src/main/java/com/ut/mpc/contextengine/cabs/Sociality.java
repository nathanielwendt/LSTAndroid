package com.ut.mpc.contextengine.cabs;

import android.util.Log;

import com.example.nathanielwendt.pacolib.Cabs;
import com.example.nathanielwendt.pacolib.samples.CommData;
import com.example.nathanielwendt.pacolib.samples.ContextSample;
import com.example.nathanielwendt.pacolib.samples.Sample;
import com.example.nathanielwendt.pacolib.samples.SensorSample;

import rx.Observable;

/**
 * Created by nathanielwendt on 12/5/15.
 */
public class Sociality extends Cabs {
    public static final String ID = "com.ut.mpc.contextengine.cabs.sociality";

    @Override
    public void init() {
        Log.d("sociality", "starting sociality");

        PacoMediator mediator = new PacoMediator();
        Observable.combineLatest(mediator.cab(Proximity.ID), mediator.communication(), (x, y) -> checkSociality(x, y)).subscribe();
    }

    public Sample checkSociality(Sample proximitySample, Sample commSample){
        ContextSample proximityContextSample = new ContextSample(proximitySample);
        Sample.JsonWrapper proximityData = proximityContextSample.dataJson();

        SensorSample commSensorSample = new SensorSample(commSample);
        CommData commData = (CommData) commSensorSample.data();

        double score = 0;
        if(proximityData.keyEquals("value", Proximity.Data.FAMILY)){
            score += .1;
        } else if(proximityData.keyEquals("value", Proximity.Data.COWORKERS)){
            score += .2;
        } else if(proximityData.keyEquals("value", Proximity.Data.FRIENDS)){
            score += .5;
        }

        score += (commData.callTimestamps.size() * .2);
        score += (commData.messageTimestamps.size() * .05);

        if(score > 1){
            score = 1;
        }

        ContextSample cSample = new ContextSample(1.0, getId(), new Data(score));
        onNext(cSample);
        return cSample;
    }

    class Data {
        protected double value;

        public Data(double value){
            this.value = value;
        }
    }

    @Override
    public String getDisplayName() {
        return "Sociality";
    }

    @Override
    public String getId() {
        return ID;
    }
}
