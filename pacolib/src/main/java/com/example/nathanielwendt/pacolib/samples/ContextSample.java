package com.example.nathanielwendt.pacolib.samples;


import com.google.gson.Gson;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by nathanielwendt on 8/18/15.
 */
public class ContextSample extends Sample {
    protected JsonWrapper data;
    protected boolean isLoaded = false;

    public ContextSample(Sample sample){
        super(sample);
    }

    public ContextSample(double accuracy, String type, Object obj){
        super(accuracy, type);
        final Gson gson = new Gson();
        this.jsonData = gson.toJson(obj);
    }

    //Method must be called before accessing any data fields other than accuracy or type
    protected void loadData() {
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(this.jsonData);
            data = new JsonWrapper((JSONObject) obj);
            isLoaded = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public JsonWrapper dataJson(){
        if(!isLoaded){
            loadData();
        }
        return data;
    }

}
