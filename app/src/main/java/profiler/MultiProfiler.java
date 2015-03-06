package profiler;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.nathanielwendt.lstrtree.evals.Eval;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Aurelius on 3/4/15.
 */
public class MultiProfiler {

    private static final int stabilizeThresh = 10; // in ms
    private static final int minCount = 10; // in iterations
    private static Context appContext;
    //private static Stabilizer mStabilizer;

    private static final String TAG = MultiProfiler.class.getSimpleName();

    private static String deviceLabel;

    public MultiProfiler() {

    }

    public static void init(Eval testClass, Context testContext) {
        //mStabilizer = (Stabilizer) testClass;
        appContext =  testContext;
        Intent trepn = new Intent();
        String model= Build.MODEL;
        String serial = Build.SERIAL;
        String modelConcat = model.replaceAll("\\s","");
        String serialStrip = serial.substring(serial.length()-3);
        Log.d(TAG, "Model: "+modelConcat);
        Log.d(TAG, "Serial: "+serialStrip);
        deviceLabel = modelConcat+"_"+serialStrip;


        // Start Trepn
        trepn.setClassName("com.quicinc.trepn", "com.quicinc.trepn.TrepnService");
        appContext.startService(trepn);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void startProfiling(String dbName) {

        Intent createDatabase = new Intent("com.quicinc.trepn.start_profiling");
        String timeAndDate = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        String dbFullName = dbName+"_"+deviceLabel+"_"+timeAndDate;
        Log.d(TAG, "dB full name: "+dbFullName);
        createDatabase.putExtra("com.quicinc.trepn.database_file", dbFullName);
        appContext.sendBroadcast(createDatabase);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void stopProfiling() {
        Intent stopProfiling = new Intent("com.quicinc.trepn.stop_profiling");
        appContext.sendBroadcast(stopProfiling);
    }

    public static int startMark(Stabilizer mStabilizer, Object data, String desc) {

        stabilizeTask(mStabilizer, data);
        Intent trepn = new Intent("com.quicinc.Trepn.UpdateAppState");
        int trepnStartCode = Math.abs(desc.hashCode() % 1000);
        trepn.putExtra("com.quicinc.Trepn.UpdateAppState.Value", trepnStartCode);
        trepn.putExtra("com.quicinc.Trepn.UpdateAppState.Value.Desc", desc
                + "::Start");
        appContext.sendBroadcast(trepn);
        return trepnStartCode;
    }

    public static int endMark(String desc) {
        Intent trepn = new Intent("com.quicinc.Trepn.UpdateAppState");
        //int trepnEndCode = Math.abs(desc.hashCode() % 1000) + 1;
        int trepnEndCode = 0;
        trepn.putExtra("com.quicinc.Trepn.UpdateAppState.Value", trepnEndCode);
        trepn.putExtra("com.quicinc.Trepn.UpdateAppState.Value.Desc", desc
                + "::End");
        appContext.sendBroadcast(trepn);
        return trepnEndCode;
    }

    public static void loadPrefs(String prefName) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent loadPreferences = new Intent("com.quicinc.trepn.load_preferences");
        loadPreferences.putExtra("com.quicinc.trepn.load_preferences_file", prefName);
        appContext.sendBroadcast(loadPreferences);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void stabilizeTask(Stabilizer mStabilizer, Object data) {
        long startTime = 0;
        long endTime = 1000000;
        long execTime = 0;
        long runningSum = 0;
        long count = 0;
        long avg = 0;
        do {
            startTime = System.currentTimeMillis();
            mStabilizer.task(data);
            endTime = System.currentTimeMillis();
            execTime = endTime - startTime;
            runningSum += execTime;
            count++;
            avg = runningSum / count;
            Log.d(TAG, "avg: "+ avg);
            Log.d(TAG, "execTime: " + execTime);

        } while((count < minCount) || (Math.abs(avg - execTime) > stabilizeThresh));

    }

}

