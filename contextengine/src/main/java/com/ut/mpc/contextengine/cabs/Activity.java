package com.ut.mpc.contextengine.cabs;

import android.util.Log;

import com.example.nathanielwendt.pacolib.Cabs;
import com.example.nathanielwendt.pacolib.samples.AccelerometerData;
import com.example.nathanielwendt.pacolib.samples.ContextSample;
import com.example.nathanielwendt.pacolib.samples.GpsData;
import com.example.nathanielwendt.pacolib.samples.Sample;
import com.example.nathanielwendt.pacolib.samples.SensorSample;

import rx.Observable;

/**
 * Created by nathanielwendt on 12/4/15.
 */
public class Activity extends Cabs {
    public static final String ID = "com.ut.mpc.contextengine.cabs.activity";

    private double lastLat = 0.0;
    private double lastLong = 0.0;


    @Override
    public void init() {
        Log.d("activity", "starting activity");
        PacoMediator mediator = new PacoMediator();

//        mediator.cab(AbstractLocation.ID).filter(loc -> isStillLocation(loc))
//                .subscribe(_x -> onNext(new ContextSample(0.8, getId(), new Data(Data.STILL))));
//        Observable<Sample> notStillLocation = mediator.cab(AbstractLocation.ID).filter(loc -> !isStillLocation(loc));
        Observable.combineLatest(mediator.cab(AbstractLocation.ID), mediator.gps(), mediator.accelerometer(), (x, y, z) -> checkActivity(x, y, z)).subscribe();
        mediator.submit();
    }

    public boolean isStillLocation(Sample abstractLocSample){
        ContextSample abstractLocContextSample = new ContextSample(abstractLocSample);
        Sample.JsonWrapper abstractLocData = abstractLocContextSample.dataJson();
        return ((AbstractLocation.Data.HOME).equals(abstractLocData.getString("name")));
    }

    public Sample checkActivity(Sample abstractLocSample, Sample gpsSample, Sample accelSample){
        ContextSample abstractLocContextSample = new ContextSample(abstractLocSample);
        Sample.JsonWrapper abstractLocData = abstractLocContextSample.dataJson();

        SensorSample gpsSensorSample = new SensorSample(gpsSample);
        GpsData gpsData = (GpsData) gpsSensorSample.data();

        SensorSample accelSensorSample = new SensorSample(accelSample);
        AccelerometerData acceldata = (AccelerometerData) accelSensorSample.data();

        String location = abstractLocData.getString("name");
        ContextSample cSample;
        if((AbstractLocation.Data.HOME).equals(location)){
            cSample = new ContextSample(0.8, getId(), new Data(Data.STILL));
        } else if(lastLat == 0.0) {
            cSample = new ContextSample(0.0, getId(), new Data(Data.UNKNOWN));
        } else {
            double distance = gps2m(gpsData.latitude, gpsData.longitude, lastLat, lastLong);
            //need to know polling frequency to make this work
            if(distance > 100){
                cSample = new ContextSample(0.9, getId(), new Data(Data.WALKING));
            } else {
                cSample = new ContextSample(0.8, getId(), new Data(Data.DRIVING));
            }
        }
        onNext(cSample);
        return cSample;
    }

    public class Data {
        public static final String STILL = "STILL";
        public static final String WALKING = "WALKING";
        public static final String DRIVING = "DRIVING";
        public static final String UNKNOWN = "UNKNOWN";
        protected String value;

        public Data(String value){
            this.value = value;
        }
    }


    private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
        double pk = (double) (180/3.14169);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2);
        double t2 = Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2);
        double t3 = Math.sin(a1)*Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000*tt;
    }

    @Override
    public String getDisplayName() {
        return "Activity";
    }

    @Override
    public String getId() {
        return ID;
    }
}
