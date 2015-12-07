package com.example.nathanielwendt.pacolib.samples;

import com.example.nathanielwendt.pacolib.PacoConsts;
import com.google.gson.Gson;

/**
 * Created by nathanielwendt on 11/18/15.
 */
public class SensorSample extends Sample {
    protected Object data;
    protected boolean isLoaded = false;

    public SensorSample(){}

    public SensorSample(Sample sample){
        super(sample);
    }

    public SensorSample(double accuracy, String type, Object data){
        super(accuracy, type);
        final Gson gson = new Gson();
        this.jsonData = gson.toJson(data);
    }

    protected void loadData() {
        final Gson gson = new Gson();
        if(type.equals(PacoConsts.Sensors.Gps)){
            data = gson.fromJson(this.jsonData, GpsData.class);
        } else {
            throw new RuntimeException("unknown sensor sample type to load");
        }
        isLoaded = true;
    }

    public Object data(){
        if(!isLoaded){
            loadData();
        }
        return data;
    }
}
