package com.ut.mpc.contextengine;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.example.nathanielwendt.pacolib.PacoConsts;
import com.example.nathanielwendt.pacolib.samples.Sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nathanielwendt on 8/18/15.
 */
public class PacoEngine extends Service {
    private final String TAG = "PacoEngine";
    private BroadcastReceiver pacoReceiver;
    private BroadcastReceiver gpsReceiver;
    private BroadcastReceiver appStatesReceiver;
    private BroadcastReceiver accelReceiver;
    private BroadcastReceiver bluetoothReceiver;
    private BroadcastReceiver commReceiver;
    private CabSubscriptions cabSubs = new CabSubscriptions();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Starting paco engine");
        IntentFilter filter = new IntentFilter(PacoConsts.CONTEXT_ENGINE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        pacoReceiver = new PacoReceiver(cabSubs);
        registerReceiver(pacoReceiver, filter);

        IntentFilter gpsFilter = new IntentFilter("sensors.gps");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        gpsReceiver = new PacoEngineSensorReceiver(this, cabSubs, PacoConsts.Sensors.Gps);
        registerReceiver(gpsReceiver, gpsFilter);

        IntentFilter appStatesFilter = new IntentFilter("sensors.appstate");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        appStatesReceiver = new PacoEngineSensorReceiver(this, cabSubs, PacoConsts.Sensors.ApplicationStates);
        registerReceiver(appStatesReceiver, appStatesFilter);

        IntentFilter accelFilter = new IntentFilter("sensors.accel");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        accelReceiver = new PacoEngineSensorReceiver(this, cabSubs, PacoConsts.Sensors.Accelerometer);
        registerReceiver(accelReceiver, accelFilter);

        IntentFilter bluetoothFilter = new IntentFilter("sensors.bluetooth");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        bluetoothReceiver = new PacoEngineSensorReceiver(this, cabSubs, PacoConsts.Sensors.Bluetooth);
        registerReceiver(bluetoothReceiver, bluetoothFilter);

        IntentFilter commFilter = new IntentFilter("sensors.comm");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        commReceiver = new PacoEngineSensorReceiver(this, cabSubs, PacoConsts.Sensors.Communication);
        registerReceiver(commReceiver, commFilter);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(pacoReceiver);
        unregisterReceiver(gpsReceiver);
        unregisterReceiver(appStatesReceiver);
        unregisterReceiver(accelReceiver);
        unregisterReceiver(bluetoothReceiver);
        unregisterReceiver(commReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class PacoEngineSensorReceiver extends BroadcastReceiver {
        private Context ctx;
        private CabSubscriptions cabSubs;
        private String identifier;

        public PacoEngineSensorReceiver(Context ctx, CabSubscriptions cabSubs, String identifier){
            this.ctx = ctx;
            this.cabSubs = cabSubs;
            this.identifier = identifier;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(identifier,"received sample");
            Intent outIntent = new Intent();

            for(CabIdentifier cabIdentifier : cabSubs.get(identifier)){
                outIntent.setAction(cabIdentifier.cabId);
                outIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                outIntent.putExtra("action", PacoConsts.Actions.PacoUpdate);
                Sample sample = intent.getParcelableExtra("sample");
                outIntent.putExtra("sample", sample);
                ctx.sendBroadcast(outIntent);
            }
        }
    }

    //Accelerometer receiver, buffer the observable?

    //Incoming registration requests come through this regReceiver
    public static class PacoReceiver extends BroadcastReceiver {
        CabSubscriptions cabSubs;

        public PacoReceiver(CabSubscriptions cabSubs){
            this.cabSubs = cabSubs;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("PacoReceiver", "received incoming message to Paco Engine");
            String action = intent.getStringExtra("action");
            if(action.equals(PacoConsts.Actions.CabPacoReg)){
                String cabId = (String) intent.getStringExtra("cabId");
                String displayName = (String) intent.getStringExtra("displayName");
                int numSensors = (int) intent.getIntExtra("numSensors", 0);
                for(int i = 1; i <= numSensors; i++){
                    String sensor = intent.getStringExtra("sensor" + String.valueOf(i));
                    cabSubs.add(cabId, displayName, sensor);
                }
            }

        }
    }

    //Manages cab subscriptions
    public class CabSubscriptions {
        private Map<String, List<CabIdentifier>> cabSubs = new HashMap<>();

        public void add(String cabId, String displayName, String sensorName){
            CabIdentifier identifier = new CabIdentifier(cabId, displayName);
            List<CabIdentifier> cabs = cabSubs.get(sensorName);
            if(cabs == null){
                cabs = new ArrayList<CabIdentifier>();
                cabs.add(identifier);
                cabSubs.put(sensorName, cabs);
            } else {
                if(!cabs.contains(identifier)){
                    cabs.add(identifier);
                }
            }
        }

        public List<CabIdentifier> get(String sensorName){
            List<CabIdentifier> cabs;
            cabs = cabSubs.get(sensorName);
            if(cabs == null){
                return new ArrayList<CabIdentifier>();
            } else {
                return cabs;
            }
        }
    }

    public class CabIdentifier {
        public String cabId;
        public String displayName;

        public CabIdentifier(String cabId, String displayName){
            this.cabId = cabId;
            this.displayName = displayName;
        }

        @Override
        public boolean equals(Object other){
            if(other == null) { return false; }
            if(other == this) { return true; }
            if (!(other instanceof CabIdentifier)) return false;
            CabIdentifier otherCabIdentifier = (CabIdentifier) other;
            return otherCabIdentifier.cabId.equals(this.cabId) && otherCabIdentifier.displayName.equals(this.displayName);
        }
    }
}
