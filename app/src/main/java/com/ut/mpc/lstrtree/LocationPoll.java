package com.ut.mpc.lstrtree;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.ut.mpc.setup.Constants;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STStorage;

import org.apache.http.Header;

/**
 * Created by nathanielwendt on 7/27/15.
 */
public class LocationPoll extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    boolean locationUpdates = true;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    public static String TAG = "LocationPoll";
    LSTFilter lstFilter;

    public LocationPoll() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient != null){
            stopLocationUpdates();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        String type = (String) intent.getExtras().get("action");
        Log.d(TAG, type);

        if("stdtableonload".equals(type)){
            stdTableOnload();
            beginLocation();
        } else if(("rtreeonload").equals(type)){
            rtreeOnload();
            beginLocation();
        } else if (("cloudoffload").equals(type)){
            cloudOffload();
            beginLocation();
        } else if (("clearstop").equals(type)) {
            clearStop();
        }
        return 0;
    }

    protected void beginLocation(){
        if(mGoogleApiClient == null){
            buildGoogleApiClient();
            createLocationRequest();
        } else {
            startLocationUpdates();
        }
    }

    protected void stdTableOnload(){
        Log.d(TAG, "std table onload");
        STStorage helper, other;
        helper = new SQLiteNaive(this, "SpatialTableMain");
        other = new SQLiteNaive(this, "RTreeMain");
        other.clear();
        lstFilter = new LSTFilter(helper);
        lstFilter.setKDCache(false);
        lstFilter.setSmartInsert(false);
        Constants.setCabDefaults();
    }

    protected void rtreeOnload(){
        Log.d(TAG, "rtree onload");
        STStorage helper, other;
        helper = new SQLiteRTree(this, "RTreeMain");
        other = new SQLiteNaive(this, "SpatialTableMain");
        other.clear();
        lstFilter = new LSTFilter(helper);
        lstFilter.setKDCache(true);
        lstFilter.setSmartInsert(true);
        Constants.setCabDefaults();
    }

    protected void cloudOffload(){
        Log.d(TAG, "cloud offload");
        lstFilter = new CloudFilter();
    }

    protected void clearStop(){
        STStorage helper, other;
        helper = new SQLiteNaive(this, "SpatialTableMain");
        other = new SQLiteNaive(this, "RTreeMain");
        helper.clear();
        other.clear();

        if(mGoogleApiClient != null) {
            stopLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "connected");
        if(locationUpdates){
            startLocationUpdates();
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        throw new RuntimeException("failed to connect to location services");
    }

    @Override
    public void onLocationChanged(Location location) {
        STPoint point = new STPoint((float) location.getLongitude(),
                (float) location.getLatitude(),
                System.currentTimeMillis());
        if(lstFilter != null) {
            lstFilter.insert(point);
        }
        Log.d(TAG, point.toString());
    }

    private class CloudFilter extends LSTFilter {

        public CloudFilter(){
            super(null);
        }

        @Override
        public void insert(STPoint point) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://pacobackend.appspot.com/", new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    Log.d(TAG, String.valueOf(statusCode));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });
        }
    }
}
