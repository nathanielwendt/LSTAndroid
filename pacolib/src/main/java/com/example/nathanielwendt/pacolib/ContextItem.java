package com.example.nathanielwendt.pacolib;

import java.io.Serializable;

/**
 * Created by nathanielwendt on 8/18/15.
 */
public class ContextItem implements Serializable {
    private int testValue;

    public ContextItem(int testValue){
        this.testValue = testValue;
    }

    public int getTestValue(){
        return this.testValue;
    }

    public void setTestValue(int value){
        this.testValue = value;
    }
}
