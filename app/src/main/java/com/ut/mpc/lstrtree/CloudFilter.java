package com.ut.mpc.lstrtree;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;

import org.apache.http.Header;

import java.nio.charset.StandardCharsets;

/**
 * Created by nathanielwendt on 7/26/15.
 */
public class CloudFilter extends LSTFilter {
    public static String TAG = "CloudBackend";

    private void makeRequest(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://pacobackend.appspot.com/", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Log.d(TAG, "SUCCESS" + new String(response, StandardCharsets.UTF_8));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.d(TAG, "SUCCESS" + new String(errorResponse, StandardCharsets.UTF_8));
            }
        });
    }

    public CloudFilter(){
        super(null);
    }


    @Override
    public void insert(STPoint item) {
        makeRequest();
    }

    @Override
    public double windowPoK(STRegion region) {
        makeRequest();
        return 9.99;
    }
}
