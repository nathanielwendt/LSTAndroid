package com.ut.mpc.lstrtree;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ut.mpc.R;
import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STStorage;

public class MyActivity extends Activity {
    String TAG = "WALKABOUT";
    Intent mServiceIntent;

    LSTFilter lstFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent("com.ut.mpc.lstindex");
        intent.putExtra("action", "insert");
        intent.putExtra("latitude", (float) 20.0);
        intent.putExtra("longitude", (float) 20.0);
        intent.putExtra("timestamp", (float) 1000.0);

        sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle results = getResultExtras(true);
                System.out.println("result: " + results.getString("result"));
            }
        }, null, Activity.RESULT_OK, null, null);

        intent = new Intent("com.ut.mpc.lstindex");
        intent.putExtra("action", "pointPoK");
        intent.putExtra("longitude", (float) 20.0);
        intent.putExtra("latitude", (float) 20.0);
        intent.putExtra("timestamp", (float) 1000.0);

        sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle results = getResultExtras(true);
                System.out.println("pok: " + results.getDouble("pok"));
            }
        }, null, Activity.RESULT_OK, null, null);

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

}
