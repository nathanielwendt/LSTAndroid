package com.ut.mpc.contextengine.cabs;

import android.util.Log;

import com.example.nathanielwendt.pacolib.Cabs;
import com.example.nathanielwendt.pacolib.samples.BluetoothData;
import com.example.nathanielwendt.pacolib.samples.ContextSample;
import com.example.nathanielwendt.pacolib.samples.Sample;
import com.example.nathanielwendt.pacolib.samples.SensorSample;

import java.util.List;
import java.util.Map;

/**
 * Created by nathanielwendt on 12/5/15.
 */
public class Proximity extends Cabs {
    public static final String ID = "com.ut.mpc.contextengine.cabs.proximity";

    private Map<String, String> deviceHistory;

    @Override
    public void init() {
        Log.d("proximity", "starting proximity");

        deviceHistory.put("923jlkaj093jglkaj", Data.FRIENDS);
        deviceHistory.put("9355vcv23jl3glaaf", Data.FRIENDS);
        deviceHistory.put("alkjv903jaklj903j", Data.FAMILY);
        deviceHistory.put("a320029fj09j90jff", Data.COWORKERS);

        PacoMediator mediator = new PacoMediator();
        mediator.bluetooth().buffer(5, 0).subscribe(bluetooth -> checkProximity(bluetooth));
    }

    public void checkProximity(List<Sample> bluetoothSamples){
        int friendsCount = 0;
        int familyCount = 0;
        int coworkersCount = 0;

        for(Sample sample: bluetoothSamples){
            SensorSample bluetoothSensorSample = new SensorSample((sample));
            BluetoothData bluetoothData = (BluetoothData) bluetoothSensorSample.data();

            String value = deviceHistory.get(bluetoothData.bt_address);
            if(value.equals(Data.FRIENDS)){ friendsCount++; }
            if(value.equals(Data.FAMILY)){ familyCount++; }
            if(value.equals(Data.COWORKERS)){ coworkersCount++; }
        }

        if(familyCount >= friendsCount && familyCount >= coworkersCount){
            onNext(new ContextSample(1.0, getId(), new Data(Data.FAMILY)));
        } else if(friendsCount >= familyCount && friendsCount >= coworkersCount){
            onNext(new ContextSample(1.0, getId(), new Data(Data.FRIENDS)));
        } else {
            onNext(new ContextSample(1.0, getId(), new Data(Data.COWORKERS)));
        }


    }

    public class Data {
        public static final String FRIENDS = "FRIENDS";
        public static final String FAMILY = "FAMILY";
        public static final String COWORKERS = "COWORKERS"; //weakest strength

        protected String value;

        public Data(String value){
            this.value = value;
        }
    }

    @Override
    public String getDisplayName() {
        return "Proximity";
    }

    @Override
    public String getId() {
        return ID;
    }
}
