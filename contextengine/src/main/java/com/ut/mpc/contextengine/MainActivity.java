package com.ut.mpc.contextengine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.nathanielwendt.contextengine.R;
import com.example.nathanielwendt.pacolib.PacoConsts;
import com.example.nathanielwendt.pacolib.samples.GpsData;
import com.example.nathanielwendt.pacolib.samples.SensorSample;
import com.ut.mpc.contextengine.cabs.AbstractLocation;
import com.ut.mpc.contextengine.cabs.Safety;

public class MainActivity extends Activity {
    private final String TAG = "PacoMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startPaco(View v){
        startService(new Intent(this, PacoEngine.class));
    }

    public void registerAbstractLoc(View v){
        startService(new Intent(this, AbstractLocation.class));
    }

    public void registerSafety(View v){
        startService(new Intent(this, Safety.class));
    }

    public void stopPaco(View v){
        Log.d(TAG, "stopping paco");
        stopService(new Intent(this, PacoEngine.class));
    }

    public void sendGpsSample(View v){
        Log.d(TAG, "sending gps sample");
        Intent intent = new Intent();
        intent.setAction("demo.temp.test");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        GpsData data = new GpsData();
        data.latitude = 30.342155;
        data.longitude = -97.7291139999999;

        SensorSample sample = new SensorSample(0.8, PacoConsts.Sensors.Gps, data);
        intent.putExtra("sample", sample);
        sendBroadcast(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
