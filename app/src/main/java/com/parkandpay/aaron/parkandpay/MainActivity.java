package com.parkandpay.aaron.parkandpay;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.Parse;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Parse.enableLocalDatastore(getApplicationContext());
       // Parse.initialize(getApplicationContext(), getString(R.string.PARSE_APPLICATION_ID), getString(R.string.PARSE_CLIENT_KEY));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    // ------ Buttons to Activity Handlers -------//
    public void onClickSpotsTaken(View view) {
        // Load up spots taken activity
        Intent intent = new Intent(view.getContext(), SpotsTakenActivity.class);
        //intent.putExtra("spot_num", item);
        startActivity(intent);
    }

    public void onClickPayForSpot(View view) {
        Intent intent = new Intent(view.getContext(), SelectSpotActivity.class);
        startActivity(intent);
    }
}
