package com.example.nathanielwendt.lstrtree.benchmark;

import com.ut.mpc.utils.LSTFilter;
import com.ut.mpc.utils.STPoint;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by nathanielwendt on 3/4/15.
 */
public class DBPrepare {
    private static final double smartInsOffVal = -1;

    public static void populateDB(LSTFilter struct, String fileName, int num, double smartInsThresh) {
        populateDB(new LSTFilter[]{struct}, fileName, num, smartInsThresh);
    }

    public static void populateDB(LSTFilter struct, String fileName, int num) {
        populateDB(new LSTFilter[]{struct}, fileName, num, smartInsOffVal);
    }

    public static void populateDB(LSTFilter[] structs, String fileName, int num, double smartInsThresh) {

        boolean smartInsert;
        if(smartInsThresh == smartInsOffVal){
            smartInsert = false;
        } else {
            smartInsert = true;
        }

        for(int i = 0; i < structs.length; i++){
            structs[i].clear();
            structs[i].setSmartInsert(smartInsert);
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            line = br.readLine();
            String[] split = line.split(" ");
            STPoint point = new STPoint(Float.valueOf(split[0]),Float.valueOf(split[1]),Float.valueOf(split[3]));

            int count = 1;
            insertPoint(structs, point);
            while (((line = br.readLine()) != null) && count < num) {
                split = line.split(" ");
                point = new STPoint(Float.valueOf(split[0]),Float.valueOf(split[1]),Float.valueOf(split[3]));
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
