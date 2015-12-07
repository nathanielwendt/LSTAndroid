package com.example.nathanielwendt.pacolib.samples;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import org.json.simple.JSONObject;

/**
 * Created by nathanielwendt on 11/19/15.
 */
public class Sample implements Parcelable {
    protected double accuracy;
    protected String type;
    protected String jsonData;

    public Sample(){}

    public Sample(Sample sample){
        this.accuracy = sample.accuracy;
        this.type = sample.type;
        this.jsonData = sample.jsonData;
    }

    public Sample(double accuracy, String type){
        this.accuracy = accuracy;
        this.type = type;
    }

    public Sample(double accuracy, String type, String jsonData){
        this(accuracy, type);
        this.jsonData = jsonData;
    }

    public Sample(double accuracy, String type, Object obj){
        this(accuracy, type);
        Gson gson = new Gson();
        this.jsonData = gson.toJson(obj);
    }

    public Sample(Parcel in){
        String[] data = new String[3];
        in.readStringArray(data);

        this.accuracy = Double.valueOf(data[0]);
        this.type = data[1];
        this.jsonData = data[2];
    }

    public Object data(){
        throw new RuntimeException("sample data method not implemented");
    }

    public JsonWrapper dataJson(){
        throw new RuntimeException("sample data method not implemented");
    }

    public boolean isType(String type) {
        return this.type.equals(type);
    }

    public double getAccuracy() {
        return this.accuracy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {String.valueOf(accuracy), type, jsonData});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Sample createFromParcel(Parcel in){
            return new Sample(in);
        }

        public Sample[] newArray(int size){
            return new Sample[size];
        }
    };

    //Class to catch exceptions and provide convenience for client
    public class JsonWrapper {
        protected JSONObject data;

        public JsonWrapper(JSONObject data){
            this.data = data;
        }

        public String getString(String key){
            return (String) getWrap(key);
        }

        public double getDouble(String key){
            return (double) getWrap(key);
        }

        public boolean keyEquals(String key, String value){
            return getWrap(key).equals(value);
        }

        public boolean keyEquals(String key, Double value){
            return getWrap(key) == value;
        }

        protected Object getWrap(String key){
            try {
                return data.get(key);
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }
}
