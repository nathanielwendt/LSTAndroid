package com.example.nathanielwendt.lstrtree;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        SQLiteRTree helper = new SQLiteRTree(this, "RTreeMain");
//
//        LSTFilter lstFilter = new LSTFilter(helper);
//        //lstFilter.clear();
//        lstFilter.setSmartInsert(false);
//
//
//        for(int i = 0; i < 100; i++){
//            float val = ((float) i) / 10;
//            lstFilter.insert(new STPoint(val,val,(float) i));
//        }
//        lstFilter.insert(new STPoint(299f, 299f, 299f));
//        Log.d("LST", "Size of structure " + lstFilter.getSize());

//        STPoint min = new STPoint(0.0f,0.0f,0.0f);
//        STPoint max = new STPoint(30f,30f,30f);
//        Log.d("LST", "Before window PoK2");
//        STRegion region = new STRegion(min,max);
//        double val = lstFilter.windowPoK(region);
//        Log.d("LST", "window PoK >> " + val);

        setContentView(R.layout.activity_my);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
