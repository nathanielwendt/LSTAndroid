package com.example.nathanielwendt.lstrtree;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.nathanielwendt.lstrtree.evals.Eval;

public class BaseRunActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_run);

        Bundle extras = this.getIntent().getExtras();

        if(extras != null){
            String evalClassName = extras.getString("eval");
            try {
                Object newObject = Class.forName("com.example.nathanielwendt.lstrtree.evals." + evalClassName).newInstance();
                if(extras.containsKey("options")){
                    ((Eval) newObject).execute(this, extras.getString("options"));
                } else {
                    ((Eval) newObject).execute(this);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    protected void execute(){
        throw new RuntimeException("executing base activity for which no logic is implemented");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base_run, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
