package com.example.nathanielwendt.pacolib;

/**
 * Created by nathanielwendt on 11/18/15.
 */
public class PacoConsts {
    public final static String CONTEXT_ENGINE = "com.ut.mpc.contextengine.CONTEXT_ENGINE";
    public final static String EMPTY_VAL = "---------";

    public static class Actions {
        public static final String CabPacoReg = "CabPacoReg";
        public static final String CabCabReg = "CabCabReg";
        public static final String AppPacoReg = "AppPacoReg";
        public static final String PacoUpdate = "PacoUpdate";
        public static final String CabUpdate = "CabUpdate";
    }

    public static class Sensors {
        public static final String Gps = "gps";
        public static final String Accelerometer = "accelerometer";
        public static final String Bluetooth = "bluetooth";
        public static final String Communication = "communication";
        public static final String ApplicationStates = "applicationStates";
    }
}
