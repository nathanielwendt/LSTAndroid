package writable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContentHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "content.db";
    private static final String TABLE_NAME = "content";
    private static final int DATABASE_VERSION = 1;
    Context myContext;

    public ContentHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
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

    public void insert(String bleId, String wifiId){
        Log.d("LST", "inserting entry: " + bleId + " , " + wifiId);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ble_id", bleId);
        values.put("wifi_id", wifiId);
        db.insert("plain", // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
        db.close();
    }

    public IdMapping getInfoFromBLE(String bleId) throws WritableException {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * from " + this.TABLE_NAME + " WHERE bleId=" + bleId + ";";
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
            return IdMapping.entryFromCursor(cursor);
        } else {
            throw new WritableException("No entry exists for that BLE Id");
        }
    }

    public IdMapping getInfoFromWifi(String wifiId) throws WritableException {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * from " + this.TABLE_NAME + " WHERE wifi_id=" + wifiId + ";";
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
            return IdMapping.entryFromCursor(cursor);
        } else {
            throw new WritableException("No entry exists for that BLE Id");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d("LST", "creating table");
        String create_basic = "CREATE TABLE " + this.TABLE_NAME + " ( id INTEGER PRIMARY KEY, ble_id TEXT UNIQUE, wifi_id TEXT UNIQUE );";
        database.execSQL(create_basic);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ContentHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + "demo_index");
        onCreate(db);
    }

}
