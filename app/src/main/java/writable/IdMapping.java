package writable;

import android.database.Cursor;

public class IdMapping {
    private String bleId;
    private String wifiId;

    public IdMapping(String bleId, String wifiId){
        this.bleId = bleId;
        this.wifiId = wifiId;
    }

    public String getBleId() {
        return bleId;
    }

    public void setBleId(String bleId) {
        this.bleId = bleId;
    }

    public String getWifiId() {
        return wifiId;
    }

    public void setWifiId(String wifiId) {
        this.wifiId = wifiId;
    }

    public static IdMapping entryFromCursor(Cursor cursor){
        return new IdMapping(cursor.getString(1), cursor.getString(2));
    }
}
