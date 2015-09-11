package com.ut.mpc.demoapp1;

import rx.Observable;

/**
 * Created by nathanielwendt on 8/14/15.
 */
public class DemoAppContextEngineReceiver extends ContextEngineReceiver {

    @Override
    public void onStreamUpdate(Observable<String> obs){
        obs.filter(x -> x.equals("10")).subscribe(x -> System.out.println(x + " is a number that is 10"));
        Observable<String> newObs = obs.filter(x -> x.equals("10"));

        newObs.subscribe(val -> sendVal(val));
    }

    public void sendVal(String val){

    }
}
