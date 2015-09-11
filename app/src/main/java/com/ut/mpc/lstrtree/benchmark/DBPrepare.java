package com.ut.mpc.lstrtree.benchmark;

import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by nathanielwendt on 3/4/15.
 */
public class DBPrepare {
    public static final double smartInsOffVal = -1;

    public static void populateDB(LSTFilter struct, String fileName, int num, double smartInsThresh, boolean isCabs) {
        populateDB(new LSTFilter[]{struct}, fileName, num, smartInsThresh, isCabs);
    }

    public static void populateDB(LSTFilter struct, String fileName, int num) {
        populateDB(new LSTFilter[]{struct}, fileName, num, smartInsOffVal, true);
    }

    public static void populateDB(LSTFilter[] structs, String fileName, int num, double smartInsThresh, boolean isCabs) {

        boolean smartInsert;
        if(smartInsThresh == smartInsOffVal){
            smartInsert = false;
        } else {
            smartInsert = true;
        }

        for(int i = 0; i < structs.length; i++){
            //structs[i].clear();
            structs[i].setSmartInsert(smartInsert);
        }

        int xIndex, yIndex, tIndex;
        String delimiter;
        if(isCabs) {
            xIndex = 1;
            yIndex = 0;
            tIndex = 3;
            delimiter = " ";
        } else {
            xIndex = 1;
            yIndex = 2;
            tIndex = 0;
            delimiter = "\\s+";
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            line = br.readLine();
            String[] split = line.split(delimiter);
            STPoint point = new STPoint(Float.valueOf(split[xIndex]),Float.valueOf(split[yIndex]),Float.valueOf(split[tIndex]));

            int count = 1;
            insertPoint(structs, point);
            while (((line = br.readLine()) != null) && count < num) {
                split = line.split(delimiter);
                point = new STPoint(Float.valueOf(split[xIndex]),Float.valueOf(split[yIndex]),Float.valueOf(split[tIndex]));
                insertPoint(structs, point);
                count++;
            }
            br.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void insertPoint(LSTFilter[] trees, STPoint temp){
        for(int i = 0; i < trees.length; i++){
            trees[i].insert(temp);
        }
    }
}
