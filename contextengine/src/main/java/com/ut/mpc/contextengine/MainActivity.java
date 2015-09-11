package com.ut.mpc.contextengine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.nathanielwendt.contextengine.R;

import rx.Observable;
import rx.Subscriber;

public class MainActivity extends Activity {
    private final String TAG = "PacoMainActivity";

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        sub.onNext("Hello, world!");
                        sub.onNext("Hi");
                        sub.onCompleted();
                    }
                }
        );

        Observable<String> tempObs = Observable.just("Hi");

       // Observable.merge(tempObs, myObservable);
        tempObs = tempObs.mergeWith(myObservable);
        tempObs = tempObs.filter(x -> x.equals("Hi"));

        tempObs.subscribe(x -> System.out.println("woo: > " + x));

        //Observable.merge(tempObs, myObservable).subscribe(str -> System.out.println("woo: " + str));
        //tempObs.mergeWith(myObservable).subscribe(x -> System.out.println(x));




        //broadcastIntent("10");
        //broadcastIntent("20");
        //broadcastIntent("10");
        //broadcastIntent("10");
    }

    public void startPaco(View v){
        startService(new Intent(this, PacoEngine.class));
    }

    public void register(View v){
        MyReceiver.setup();
        MyReceiver.setPackageId("com.ut.mpc.contextengine");
        MyReceiver.register(getApplicationContext());
    }

    public void stopPaco(View v){
        Log.d(TAG, "stopping paco");
        stopService(new Intent(this, PacoEngine.class));
    }

    // broadcast a custom intent.
    public void broadcastIntent(String val){
        Log.d(TAG, "broadcasting intent");
        Intent intent = new Intent();
        intent.setAction("com.ut.mpc.CONTEXT_ENGINE");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("test", val);
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
