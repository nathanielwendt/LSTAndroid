package com.ut.mpc.demoapp1;

import com.example.nathanielwendt.pacolib.samples.ContextSample;
import com.example.nathanielwendt.pacolib.CustomObject;
import com.example.nathanielwendt.pacolib.PacoReceiver;

import java.io.Serializable;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by nathanielwendt on 8/21/15.
 */
public class DemoApp1Receiver extends PacoReceiver {

    public static void setup(){
        PacoReceiver.addSubscriber(
                PacoReceiver.getContextStream().filter((Func1<? super ContextSample, Boolean> & Serializable) (x) -> x.getTestValue() > 10)
                        .subscribe((Action1<ContextSample> & Serializable) (x) -> System.out.println("demo app value is " + x.getTestValue()))
        );

        CustomObject obj = new CustomObject(){
            @Override
            public void doit(int x) {
                System.out.println("xlkfjef ix : " + x);
            }
        };

        PacoReceiver.doit(obj);
    }
}
