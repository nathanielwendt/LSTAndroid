package com.ut.mpc.contextengine.cabs;

import android.util.Log;

import com.example.nathanielwendt.pacolib.Cabs;
import com.example.nathanielwendt.pacolib.samples.ContextSample;
import com.example.nathanielwendt.pacolib.samples.GpsData;
import com.example.nathanielwendt.pacolib.samples.Sample;
import com.example.nathanielwendt.pacolib.samples.SensorSample;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import rx.Observable;

/**
 * Created by nathanielwendt on 11/22/15.
 */
public class Safety extends Cabs {
    public static final String ID = "com.ut.mpc.contextengine.cabs.safety";

    private static final String spotCrimeUrl = "http://api.spotcrime.com/crimes.json";
    private static final String spotCrimeKey = "spotcrime-private-api-key";
    private static final double spotCrimeRadius = 0.02;

    @Override
    public void init() {
        Log.d("Safety", "starting Safety");
        PacoMediator mediator = new PacoMediator();
        Observable.combineLatest(mediator.gps(), mediator.cab(AbstractLocation.ID), (x,y) -> checkSafety(x, y)).subscribe();
        mediator.submit();
    }

    public Sample checkSafety(Sample gpsSample, Sample abstractLocSample){
        ContextSample abstractLocContextSample = new ContextSample(abstractLocSample);
        Sample.JsonWrapper abstractLocData = abstractLocContextSample.dataJson();

        SensorSample gpsSensorSample = new SensorSample(gpsSample);
        GpsData gpsData = (GpsData) gpsSensorSample.data();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("lat", gpsData.latitude);
        params.put("lon", gpsData.longitude);
        params.put("key", spotCrimeKey);
        params.put("radius", spotCrimeRadius);
        client.get(spotCrimeUrl, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                double multiplier;
                if(abstractLocData.keyEquals("name", AbstractLocation.Data.WORK)){
                    multiplier = 0.8;
                } else if(abstractLocData.keyEquals("name", AbstractLocation.Data.HOME)){
                    multiplier = 0.1;
                } else if(abstractLocData.keyEquals("name", AbstractLocation.Data.SCHOOL)){
                    multiplier = 0.6;
                } else {
                    multiplier = 1.0;
                }

                double crimeLength;
                try {
                    crimeLength = response.getJSONArray("crimes").length() / 10;
                } catch(JSONException e){
                    e.printStackTrace();
                    crimeLength = 0;
                }

                if(crimeLength > 1){
                    crimeLength = 1;
                }

                //naive comparison, better option could compare types of crimes
                double score = 1 - (multiplier * crimeLength);
                System.out.println("score is: " + score);
                ContextSample cSample = new ContextSample(0.8, getId(), new Data(score));
                onNext(cSample);
            }

            @Override
            public void onStart() {
                System.out.println("sending http request to spotcrime");
            }

        });

//        ContextSample cSample;
//        if(gpsData.latitude == 20.02 && abstractLocData.keyEquals("name","Work")){
//            cSample = new ContextSample(0.9, getId(), new SafetyData("strong"));
//            Log.d("Safety", "strong safety");
//        } else if(gpsData.latitude == 20.12 && abstractLocData.keyEquals("name","Work")){
//            cSample = new ContextSample(0.8, getId(), new SafetyData("unsafe"));
//            Log.d("Safety", "unsafe safety");
//        } else {
//            cSample = new ContextSample(0.0, getId(), new SafetyData(PacoConsts.EMPTY_VAL));
//            Log.d("Safety", "uknown safety");
//        }
//        onNext(cSample);
        return null;
    }

    public class Data {
        public static final String SAFE = "SAFE";
        public static final String MIDSAFE = "MIDSAFE";
        public static final String UNSAFE = "UNSAFE";
        public static final String UNKNOWN = "UNKNOWN";

        public String safetyLevel;
        public Data(String safetyLevel){
            this.safetyLevel = safetyLevel;
        }
        public Data(double score){
            this.safetyLevel = scoreToSafety(score);
        }

        public String scoreToSafety(double score){
            if(score >= 0.8){
                return Data.SAFE;
            } else if(score >= 0.6){
                return Data.MIDSAFE;
            } else {
                return Data.UNSAFE;
            }
        }
    }

    @Override
    public String getDisplayName() {
        return "Safety";
    }

    @Override
    public String getId() {
        return ID;
    }
}
