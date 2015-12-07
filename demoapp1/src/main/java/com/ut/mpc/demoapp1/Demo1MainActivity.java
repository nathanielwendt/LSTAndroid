package com.ut.mpc.demoapp1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import rx.Observable;
import rx.Subscriber;


public class Demo1MainActivity extends Activity {

    int index = 0;
    ImageView imgView;
    TextView textView;
    Observable<String> SOCIALITY;
    Observable<String> ACTIVITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Observable<String> ACTIVITY = Observable.from(new String[]{"walking", "running", "walking", "walking", "running"});
        //Observable<String> SOCIALITY = Observable.from(new String[]{"alone","friends","alone","alone","alone","family"});

        String[] activities = new String[]{"walking","running","walking","walking","running","running","running"};
        String[] socialities = new String[]{"alone","alone","friends","alone","alone","alone","family"};
        int delay = 3000; //ms


        Capa.configureImg(this);
        imgView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        Capa.ImageMap(imgView, "https://iangoudie.files.wordpress.com/2015/10/no-running.jpg");
        Capa.TextMap(textView, "default");

        for(int i = 0; i < socialities.length; i++){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    index = (index + 1) % 7;
                }
            }, delay * (i + 1));
        }


        ACTIVITY = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> sub) {

                for(int i = 0; i < activities.length; i++){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            sub.onNext(activities[index]);
                            System.out.println("activity >> " + activities[index]);
                        }
                    }, delay * i);
                }

                //sub.onCompleted();
            }
        });

        SOCIALITY = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> sub) {

                for(int i = 0; i < socialities.length; i++){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            sub.onNext(socialities[index]);
                            System.out.println("sociality >> " + socialities[index]);
                        }
                    }, delay * i);
                }
            }
        });

//        new Thread(new Runnable() {
//            public void run() {
//                for(int i = 0; i < 30; i++){
//                    index = (index + 1) % 7;
//                    try { Thread.sleep(delay); } catch (InterruptedException e){ e.printStackTrace(); }
//                }
//            }
//        }).start();

        ACTIVITY.filter(x -> x.equals("running"))
                .subscribe(x -> Capa.ImageMap(imgView, "http://mywebsite.com/running.jpg"));
        ACTIVITY.filter(x -> !x.equals("running"))
                .subscribe(x -> Capa.ImageMap(imgView, "https://mywebsite.com/no-running.jpg"));

        SOCIALITY.map(x -> (x.equals("family") | x.equals("friends")) ? "people" : "alone")
                 .subscribe(x -> Capa.TextMap(textView, "people around!"));
        SOCIALITY.filter(x -> !x.equals("family") && !x.equals("friends"))
                 .subscribe(x -> Capa.TextMap(textView, "you're alone!"));



       // Log.d("DemoApp1", "Registering demo app 1");
       // DemoApp1Receiver.setup();
       // DemoApp1Receiver.setPackageId("com.ut.mpc.demoapp1");
       // DemoApp1Receiver.register(getApplicationContext());
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
