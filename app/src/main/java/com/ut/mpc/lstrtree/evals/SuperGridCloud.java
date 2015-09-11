package com.ut.mpc.lstrtree.evals;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.ut.mpc.etch.Eval;
import com.ut.mpc.etch.MultiProfiler;
import com.ut.mpc.etch.Stabilizer;
import com.ut.mpc.lstrtree.CloudFilter;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class SuperGridCloud implements Eval {

    private static final String TAG = SuperGridCloud.class.getSimpleName();
    private int numWindows = 0;
    private int count = 0;

    @Override
    public void execute(Context ctx, JSONObject options) throws JSONException {
        //Number of nonzero windows to take values for before exiting out of loop
        //This is an upper limit, not lower limit as the loop may terminate before this value is reached
        int numWindows = Integer.valueOf(options.getString("numWindows"));
        this.numWindows = numWindows;

        final LSTFilter lstFilter = new CloudFilter();

        Stabilizer stabFunc = new Stabilizer(){
            @Override
            public void task(Object data) {
                int x = 2;
            }
        };

        String dbNameFull = "CloudOffload" + "_" +
                numWindows + "_";

        MultiProfiler.init(this, ctx);
        MultiProfiler.startProfiling(dbNameFull);


        double val;
        STRegion region = new STRegion(new STPoint(0,0,0), new STPoint(1,1,1));
        MultiProfiler.startMark(stabFunc, region, "cloudoffload");
        makeRequest();
    }

    private void makeRequest(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://pacobackend.appspot.com/", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                count++;
                Log.d(TAG, "SUCCESS " + statusCode);
                if(count < numWindows){
                    makeRequest();
                } else {
                    MultiProfiler.endMark("cloudoffload");
                    MultiProfiler.stopProfiling();
                    Log.d(TAG, "Done Profiling >>>>>>>>>");
                    Log.d(TAG, "ran " + numWindows + " times");
                    Log.d(TAG, "---------------------------- end of eval --------------------");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.d(TAG, "SUCCESS " + statusCode);
            }
        });
    }
}
