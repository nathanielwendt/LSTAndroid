package com.ut.mpc.lstrtree;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;

/**
 * Created by nathanielwendt on 12/2/15.
 */
public class LSTIndexReceiver extends BroadcastReceiver {


    public LSTIndexReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle results = getResultExtras(true);

        Bundle extras = intent.getExtras();
        String action = extras.getString("action");

        SQLiteRTree helper = new SQLiteRTree(context, "RTreeMain");
        LSTFilter lstFilter = new LSTFilter(helper);

        if(("insert").equals(action)){
            float latitude = extras.getFloat("latitude");
            float longitude = extras.getFloat("longitude");
            float timestamp;
            if(extras.get("timestamp") != null){
                timestamp = extras.getFloat("timestamp");
            } else {
                timestamp = (float) (System.currentTimeMillis() / 1000);
            }
            lstFilter.insert(new STPoint(longitude, latitude, timestamp));
            results.putString("result", "success");
        } else if(("windowPoK").equals(action)){
            float minLongitude = extras.getFloat("minLongitude");
            float maxLongitude = extras.getFloat("maxLongitude");
            float minLatitude = extras.getFloat("minLatitude");
            float maxLatitude = extras.getFloat("maxLatitude");
            float minTimestamp = extras.getFloat("minTimestamp");
            float maxTimestamp = extras.getFloat("maxTimestamp");
            String responseReceiver = extras.getString("responseReceiver");

            STPoint min = new STPoint(minLongitude, minLatitude, minTimestamp);
            STPoint max = new STPoint(maxLongitude, maxLatitude, maxTimestamp);
            double pok = lstFilter.windowPoK(new STRegion(min, max));
            results.putDouble("pok", pok);
            results.putString("result", "success");
        } else if(("pointPoK").equals(action)){
            float longitude = extras.getFloat("longitude");
            float latitude = extras.getFloat("latitude");
            float timestamp = extras.getFloat("timestamp");
            String responseReceiver = extras.getString("responseReceiver");

            double pok = lstFilter.pointPoK(new STPoint(longitude, latitude, timestamp));
            results.putDouble("pok", pok);
            results.putString("result", "success");
        } else {
            results.putString("result", "unknown command");
        }

    }
}
