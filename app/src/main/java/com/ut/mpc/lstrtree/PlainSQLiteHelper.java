package com.ut.mpc.lstrtree;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class PlainSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydata.db";
    private static final int DATABASE_VERSION = 1;
    Context myContext;

    public PlainSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
    }

    public void init(){
        SQLiteDatabase db = this.getReadableDatabase();
    }

    public void getEntry(){

        SQLiteDatabase db = this.getReadableDatabase();
        db.close();
    }

    public boolean checkDataBase(){
        boolean checkdb = false;
        try{
            String myPath = myContext.getApplicationInfo().dataDir + "/databases/";
            //String myPath = myContext.getFilesDir().getAbsolutePath().replace("files", "databases")+ File.separator + DATABASE_NAME;
            Log.d("LST", myPath);
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
        }
        catch(SQLiteException e){
            System.out.println("Database doesn't exist");
        }

        return checkdb;
    }

    public void createEntry(){
        Log.d("LST", "");
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("val", "hello, world");

        // 3. insert
        db.insert("plain", // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        Log.d("LST", "creating table");

        String create_basic = "CREATE TABLE plain ( id INTEGER PRIMARY KEY, val TEXT );";
        database.execSQL(create_basic);
        //database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(PlainSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + "demo_index");
        onCreate(db);
    }

}
