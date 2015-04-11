package com.parkandpay.aaron.parkandpay;

import android.app.Application;
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
        startActivity(intent);
    }

    public void onClickPayForSpot(View view) {
        Intent intent;

        if(ApplicationConfig.hasSpot()) {
            intent = new Intent(view.getContext(), PayActivity.class);
            intent.putExtra("spot_num", ApplicationConfig.getSpotTaken());
            intent.putExtra("lot_name", ApplicationConfig.getLotName());
        } else {
            intent = new Intent(view.getContext(), PayActivity.class);
        }

        startActivity(intent);
    }
}
