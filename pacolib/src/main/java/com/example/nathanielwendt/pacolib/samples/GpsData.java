package com.example.nathanielwendt.pacolib.samples;

import android.os.Bundle;

/**
 * Created by nathanielwendt on 11/18/15.
 */
public class GpsData {
    public long timestamp;
    public double latitude;
    public double longitude;
    public double bearing;
    public double speed;
    public double altitude;
    public String provider;

    public static GpsData fromExtras(Bundle extras){
        GpsData sample = new GpsData();
        sample.timestamp = extras.getLong("timestamp");
        sample.latitude = extras.getDouble("latitude");
        sample.longitude = extras.getDouble("longitude");
        sample.bearing = extras.getDouble("bearing");
        sample.speed = extras.getDouble("speed");
        sample.altitude = extras.getDouble("altitude");
        sample.provider = extras.getString("provider");
        return sample;
    }
}
