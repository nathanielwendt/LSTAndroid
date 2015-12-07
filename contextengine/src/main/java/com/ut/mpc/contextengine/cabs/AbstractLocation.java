package com.ut.mpc.contextengine.cabs;

import android.util.Log;

import com.example.nathanielwendt.pacolib.Cabs;
import com.example.nathanielwendt.pacolib.samples.ContextSample;
import com.example.nathanielwendt.pacolib.samples.GpsData;
import com.example.nathanielwendt.pacolib.samples.Sample;
import com.example.nathanielwendt.pacolib.samples.SensorSample;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nathanielwendt on 11/17/15.
 */
public class AbstractLocation extends Cabs {
    public static final String ID = "com.ut.mpc.contextengine.cabs.abstractlocation";
    private LocationTable locTable = new LocationTable();
    private String lastLocName = "";

    public void init(){
        PacoMediator mediator = new PacoMediator();
        mediator.gps().subscribe(item -> checkLoc(item));
        mediator.submit();
    }

    public void checkLoc(Sample sample){
        SensorSample sSample = new SensorSample(sample);
        GpsData gps = (GpsData) sSample.data();
        Log.d("AbstractLoc", String.valueOf(gps.latitude));
        String name = locTable.getLoc(gps.longitude, gps.latitude);
        if(name != null && !lastLocName.equals(name)){
            Data data = new Data(name);
            ContextSample cSample = new ContextSample(sSample.getAccuracy(), getId(), data);
            onNext(cSample);
        } else {
            Data data = new Data(Data.WORK);
            ContextSample cSample = new ContextSample(0.9, getId(), data);
            onNext(cSample);
        }
    }

    @Override
    public String getDisplayName(){
        return "Abstract Location";
    }

    @Override
    public String getId(){
        return ID;
    }

    //Class containing relevant information for a data sample
    public class Data {
        public static final String WORK = "WORK";
        public static final String HOME = "HOME";
        public static final String SCHOOL = "SCHOOL";
        public static final String UNKNOWN = "UNKNOWN";
        protected String name;

        public Data(String name){
            this.name = name;
        }
    }

    //Naive location table that stores list of georegions according to fixed present sizes
    //Linear scan of regions to determine intersection
    public class LocationTable {
        private double longTol = 0.108;
        private double latTol = 0.34;
        private Map<String, GeoRegion> geoRegions = new HashMap<String, GeoRegion>();

        public void setLoc(double longitude, double latitude, String name){
            geoRegions.put(name, new GeoRegion(longitude - longTol, longitude + longTol,
                                               latitude - latTol, latitude + longTol));
        }

        public String getLoc(double longitude, double latitude){
            for (Map.Entry<String, GeoRegion> entry : geoRegions.entrySet()) {
                GeoRegion reg = entry.getValue();
                if(longitude > reg.getMinLong() && longitude < reg.getMaxLong()
                        && latitude > reg.getMinLat() && latitude < reg.getMaxLat()){
                    return entry.getKey();
                }
            }
            return null;
        }
    }

    public class GeoRegion {
        private double minLong, maxLong, minLat, maxLat;

        public double getMinLong() {
            return minLong;
        }

        public double getMaxLong() {
            return maxLong;
        }

        public double getMinLat() {
            return minLat;
        }

        public double getMaxLat() {
            return maxLat;
        }

        public GeoRegion(double minLong, double maxLong, double minLat, double maxLat){
            this.minLong = minLong;
            this.maxLong = maxLong;
            this.minLat = minLat;
            this.maxLat = maxLat;
        }
    }
}
