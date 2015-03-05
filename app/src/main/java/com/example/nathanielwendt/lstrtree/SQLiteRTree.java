package com.example.nathanielwendt.lstrtree;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.ut.mpc.setup.Constants;
import com.ut.mpc.utils.GPSLib;
import com.ut.mpc.utils.LSTFilterException;
import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STRegion;
import com.ut.mpc.utils.STStorage;

import org.sqlite.database.ExtraUtils;
import org.sqlite.database.sqlite.SQLiteDatabase;
import org.sqlite.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static com.example.nathanielwendt.lstrtree.DBConstants.DATABASE_NAME;
import static com.example.nathanielwendt.lstrtree.DBConstants.DATABASE_VERSION;
import static com.ut.mpc.setup.Constants.SPATIAL_TYPE;

//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteRTree extends SQLiteOpenHelper implements STStorage {

    static {
        System.loadLibrary("sqliteX");
    }

    private Context myContext;
    private String table_identifier;

    public SQLiteRTree(Context context, String identifier) {
        super(context, context.getDatabasePath(DATABASE_NAME).getPath(), null, DATABASE_VERSION);
        Log.d("LST", context.getDatabasePath(DATABASE_NAME).getPath());
        this.myContext = context;
        this.table_identifier = identifier;

        DBInit dbInit = new DBInit(context);

        if(!dbInit.dbExists()){
            Log.d("LST", "DB did not exist, using dbInit to create now.");
            dbInit.init();
            this.onCreate(this.getWritableDatabase());
        }
    }

    @Override
    public int getSize() {
        SQLiteDatabase db = this.getReadableDatabase();
        int size = ((Long) ExtraUtils.longForQuery(db, "SELECT COUNT(*) FROM " + this.table_identifier, null)).intValue();
        db.close();
        return size;
    }

    @Override
    public void insert(STPoint point) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("minX", point.getX());
        values.put("maxX", point.getX());
        values.put("minY", point.getY());
        values.put("maxY", point.getY());
        values.put("minT", point.getT());
        values.put("maxT", point.getT());
        db.insert(this.table_identifier, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
        db.close();
    }

    @Override
    public List<STPoint> range(STRegion range) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT id,minX,maxX,minY,maxY,minT,maxT from " + this.table_identifier + " ";

        STPoint mins = range.getMins();
        STPoint maxs = range.getMaxs();

        String prefix = " WHERE ";
        if(mins.hasX() && maxs.hasX()){
            query += prefix + " minX >= " + String.valueOf(mins.getX()) +
                    " AND maxX <= " + String.valueOf(maxs.getX());
            prefix = " AND ";
        }

        if(mins.hasY() && maxs.hasY()){
            query += prefix + " minY >= " + String.valueOf(mins.getY()) +
                    " AND maxY <= " + String.valueOf(maxs.getY());
            prefix = " AND ";
        }

        if(mins.hasT() && maxs.hasT()){
            query += prefix + " minT >= " + String.valueOf(mins.getT()) +
                    " AND maxT <= " + String.valueOf(maxs.getT());
            prefix = " AND ";
        }

        Cursor cur = db.rawQuery(query, null);

        List<STPoint> points = cursorToList(cur);
        Log.d("LST", query);
        Log.d("LST", "result size >> " + points.size());
        db.close();
        return points;
    }

    @Override
    public List<STPoint> nearestNeighbor(STPoint needle, STPoint boundValues, int n) {

        //TODO support more than one nearest neighbor
        if(n != 1){
            throw new RuntimeException("Nearest Neighbor does not support N != 1");
        }

        List<STPoint> candPoints = new ArrayList<STPoint>();
        for(int i = 0; i < 20; i++){
            try {
                STRegion miniRegion = GPSLib.getSpaceBoundQuick(needle, boundValues, SPATIAL_TYPE);
                candPoints = range(miniRegion);
                if(candPoints.size() >= n){
                    break;
                }
                int step = (int) Math.pow(i, 3);
                boundValues.setX(step * boundValues.getX());
                boundValues.setY(step * boundValues.getY());
                boundValues.setT(step * boundValues.getT());
            } catch (LSTFilterException e){
                e.printStackTrace();
            }
        }


        STPoint minPoint = null;
        double minVal = Double.MAX_VALUE;
        for(STPoint point: candPoints){
            //ToDo: optimize this since we use the same needle each time, some values should drop out of dist calc.
            //ToDO: optimize by using squared values of distance instead of ones using sqrt (expensive)
            try {
                double distance = GPSLib.distanceBetween(needle, point, SPATIAL_TYPE);
                if(distance < minVal){
                    minVal = distance;
                    minPoint = point;
                }
            } catch (LSTFilterException e){
                e.printStackTrace();
            }
        }

        List<STPoint> minPoints = new ArrayList<STPoint>();
        minPoints.add(minPoint);
        return minPoints;
    }

    @Override
    public List<STPoint> getSequence(STPoint start, STPoint end) {

        STPoint boundValues = new STPoint(Constants.PoK.X_RADIUS, Constants.PoK.Y_RADIUS, Constants.PoK.T_RADIUS);

        STPoint startNN = this.nearestNeighbor(start, boundValues, 1).get(0);
        STPoint endNN = this.nearestNeighbor(end, boundValues, 1).get(0);

        float minT = startNN.getT() > endNN.getT() ? startNN.getT() : endNN.getT();
        float maxT = startNN.getT() > endNN.getT() ? startNN.getT() : endNN.getT();
//
//        String query = "SELECT id,minX,maxX,minY,maxY,minT,maxT from " + this.table_identifier;
//        query += "WHERE minT<=" + maxT + " AND minT>=" + minT;
//        query += "ORDER BY minT ASC";
//
//        SQLiteDatabase db = this.getReadableDatabase();
        STPoint minPoint = new STPoint();
        STPoint maxPoint = new STPoint();

        minPoint.setT(minT);
        maxPoint.setT(maxT);
        return this.range(new STRegion(minPoint, maxPoint));
    }

    @Override
    public STRegion getBoundingBox() {
        //TODO: get bounding box through R-Tree shadow tables

        SQLiteDatabase db = this.getReadableDatabase();


        String query = "SELECT minX FROM " + this.table_identifier + " ORDER BY minX ASC LIMIT 1;";
        float minX = getRowValueHelper(db, query, 0);
        query = "SELECT minY FROM " + this.table_identifier + " ORDER BY minY ASC Limit 1;";
        float minY = getRowValueHelper(db, query, 0);
        query = "SELECT minT FROM " + this.table_identifier + " ORDER BY minT ASC Limit 1;";
        float minT = getRowValueHelper(db, query, 0);
        query = "SELECT maxX FROM " + this.table_identifier + " ORDER BY maxX DESC Limit 1;";
        float maxX = getRowValueHelper(db, query, 0);
        query = "SELECT maxY FROM " + this.table_identifier + " ORDER BY maxY DESC Limit 1;";
        float maxY = getRowValueHelper(db, query, 0);
        query = "SELECT maxT FROM " + this.table_identifier + " ORDER BY maxT DESC Limit 1;";
        float maxT = getRowValueHelper(db, query, 0);

        return new STRegion(new STPoint(minX, minY, minT), new STPoint(maxX, maxY, maxT));
    }

    private float getRowValueHelper(SQLiteDatabase db, String query, int index){
        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();
        float val = cur.getFloat(index);
        cur.close();
        return val;
    }

    private List<STPoint> cursorToList(Cursor cur){
        List<STPoint> list = new ArrayList<STPoint>();

        cur.moveToFirst();
        while (!cur.isAfterLast()) {
            int id = cur.getInt(0);
            float minX = cur.getFloat(1);
            float maxX = cur.getFloat(2);
            float minY = cur.getFloat(3);
            float maxY = cur.getFloat(4);
            float minT = cur.getFloat(5);
            float maxT = cur.getFloat(6);

            //Log.d("LST","minx: " + minX + " , maxx: " + maxX);
            //Log.d("LST","miny: " + minY + " , maxy: " + maxY);
            //Log.d("LST","mint: " + minT + " , maxt: " + maxT);

            list.add(new STPoint(minX, minY, minT));
            cur.moveToNext();
        }
        cur.close();

        return list;
    }

    public void clear(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(this.table_identifier, null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String create_rtree = "CREATE VIRTUAL TABLE " + this.table_identifier + " USING rtree(\n" +
                "   id,              -- Integer primary key\n" +
                "   minX, maxX,      -- Minimum and maximum X coordinate\n" +
                "   minY, maxY,       -- Minimum and maximum Y coordinate\n" +
                "   minT, maxT       -- Minimum and maximum T coordinate\n" +
                ");";
        Log.d("LST","Created table: " + this.table_identifier);
        database.execSQL(create_rtree);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteRTree.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + this.table_identifier);
        onCreate(db);
    }
}
