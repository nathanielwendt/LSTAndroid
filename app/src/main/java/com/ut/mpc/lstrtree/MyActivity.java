package com.ut.mpc.lstrtree;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.ut.mpc.R;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STStorage;

public class MyActivity extends Activity {
    boolean locationUpdates = true;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    String TAG = "WALKABOUT";
    Intent mServiceIntent;

    LSTFilter lstFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



            Uri allMessages = Uri.parse("content://sms/");
            //Cursor cursor = managedQuery(allMessages, null, null, null, null); Both are same
            Cursor cursor = this.getContentResolver().query(allMessages, null,
                    null, null, null);

            while (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Log.d(cursor.getColumnName(i) + "", cursor.getString(i) + "");
                }
                Log.d("One row finished",
                        "**************************************************");
            }


//        SQLiteRTree helper = new SQLiteRTree(this, "RTreeMain");
//
//        LSTFilter lstFilter = new LSTFilter(helper);
//        //lstFilter.clear();
//        lstFilter.setSmartInsert(false);
//
//
//        for(int i = 0; i < 100; i++){
//            float val = ((float) i) / 10;
//            lstFilter.insert(new STPoint(val,val,(float) i));
//        }
//        lstFilter.insert(new STPoint(299f, 299f, 299f));
//        Log.d("LST", "Size of structure " + lstFilter.getSize());

//        STPoint min = new STPoint(0.0f,0.0f,0.0f);
//        STPoint max = new STPoint(30f,30f,30f);
//        Log.d("LST", "Before window PoK2");
//        STRegion region = new STRegion(min,max);
//        double val = lstFilter.windowPoK(region);
//        Log.d("LST", "window PoK >> " + val);


//        ContentHelper writable = new ContentHelper(this);
//        writable.insert("ble0001","wifi0001");
//        writable.insert("ble0002","wifi0002");
//        writable.insert("ble0003","wifi0003");
//
//        try {
//            IdMapping mapping = writable.getInfoFromBLE("ble0002");
//            System.out.println(mapping);
//        } catch (WritableException e){
//            e.printStackTrace();
//        }

        //buildGoogleApiClient();
        //createLocationRequest();

        STStorage helper, other;
        helper = new SQLiteNaive(this, "SpatialTableMain");
        other = new SQLiteRTree(this, "RTreeMain");
        Log.d(TAG, "stdtable: " + helper.getSize());
        Log.d(TAG, "other: " + other.getSize());

        setContentView(R.layout.activity_my);
    }

    public void startIntent(String message){
        mServiceIntent = new Intent(this, LocationPoll.class);
        mServiceIntent.putExtra("action",message);
        startService(mServiceIntent);
    }

    public void stdtableOnload(View view){
        startIntent("stdtableonload");
    }

    public void rtreeOnload(View view){
        startIntent("rtreeonload");
    }

    public void cloudOffload(View view){
        startIntent("cloudoffload");
    }

    public void clearAll(View view){
        startIntent("clearstop");
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        stopLocationUpdates();
//    }

//    protected void stopLocationUpdates() {
//        LocationServices.FusedLocationApi.removeLocationUpdates(
//                mGoogleApiClient, this);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mGoogleApiClient.isConnected()) {
//            startLocationUpdates();
//        }
//    }
//
//    protected void createLocationRequest() {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(1000);
//        mLocationRequest.setFastestInterval(500);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//    }
//
//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.my, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onConnected(Bundle bundle) {
//        Log.d(TAG, "connected");
//        if(locationUpdates){
//            startLocationUpdates();
//        }
//    }
//
//    protected void startLocationUpdates() {
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient, mLocationRequest, this);
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        throw new RuntimeException("failed to connect to location services");
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        STPoint point = new STPoint((float) location.getLongitude(),
//                                    (float) location.getLatitude(),
//                                    System.currentTimeMillis());
//        if(lstFilter != null) {
//            lstFilter.insert(point);
//        }
//        Log.d(TAG, point.toString());
//    }
}
