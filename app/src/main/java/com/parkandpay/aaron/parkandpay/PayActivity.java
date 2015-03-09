package com.parkandpay.aaron.parkandpay;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.parse.Parse;
import com.parse.ParseObject;

import java.util.Calendar;
import java.util.Date;


public class PayActivity extends ActionBarActivity {

    private Date selectedTime;
    private Integer spotNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        Parse.initialize(getApplicationContext(), "D3e1FmZef0xReoXZlWFVGgxIosiuUfusQ9jgHT7y", "FFUhtwW99qsT4ExUj2kepUhKVAibc3MsMJYJGf2x");


        Intent intent = getIntent();
        spotNum = intent.getIntExtra("spot_num", -1);
        Log.d("debug", spotNum.toString());

        String spot_text = "Sorry, could not find your spot. Please try again.";
        if(spotNum != -1) {
            spot_text = spotNum.toString();
        }

        // ADD SOME TEXT THATS LIKE, THIS SPOT IS CURRENTLY EXPIRED OR
        //      THIS SPOT WILL EXPIRE AT "time"

        TextView textview = (TextView) findViewById(R.id.spot_num_text);
        textview.setText(spot_text);
        textview.setTextSize(40);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pay, menu);
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

    public void onClickPay(View view) {
        Log.d("test", "pay button clicked");


        final Calendar c = Calendar.getInstance();
        int curHour = c.get(Calendar.HOUR_OF_DAY);
        Integer curMinute = c.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        // this is an async callback
                        Log.d("test", "should be second: " + Integer.toString(hourOfDay));
                        selectedTime = new Date();
                        Calendar tempTime = Calendar.getInstance();
                        tempTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        tempTime.set(Calendar.MINUTE, minute);
                        selectedTime = tempTime.getTime();

                        Log.d("test", selectedTime.toString());

                        // Probably want a confirmation here

                        //Wrap this in a function or something
                        ParseObject dataObject = new ParseObject("ParkingSpot");
                        dataObject.put("SpotName", spotNum);
                        dataObject.put("PaidForUntil", selectedTime);
                        dataObject.saveInBackground();


                    }
                }, curHour, curMinute, false);
        tpd.show();
    }
}
