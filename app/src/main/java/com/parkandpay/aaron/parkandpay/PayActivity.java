package com.parkandpay.aaron.parkandpay;

import android.app.Application;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Button;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class PayActivity extends ActionBarActivity {

    private Date selectedTime;
    private String spotNum;
    private String lotName;
    private static ParseObject selectedLotObj;
    private static Button resetSpotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        Intent intent = getIntent();
        spotNum = intent.getStringExtra("spot_num");
        String selectedLot = intent.getStringExtra("lot_name");
        lotName = selectedLot;
        resetSpotButton = (Button) findViewById(R.id.resetSpotButton);

        if(ApplicationConfig.hasSpot()) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("ParkingSpot");
            query.whereEqualTo("Lot_Name", selectedLot);
            query.whereEqualTo("SpotName", spotNum);

            query.findInBackground(new FindCallback<ParseObject>() {
                //@Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if(e == null && parseObjects.size() > 0) {
                        selectedLotObj = parseObjects.get(0);
                        String expiration_time = selectedLotObj.get("PaidForUntil").toString();

                        //SimpleDateFormat format = new SimpleDateFormat("MMM dd, hh:mm a");
                        String time = "Expires at: " + expiration_time;

                        final TextView time_remaining_text = (TextView) findViewById(R.id.time_remaining_text);
                        time_remaining_text.setText(time);
                        time_remaining_text.setTextSize(32);
                    } else {
                        System.out.println(e.getMessage());
                        throw new RuntimeException();
                    }
                }
            });


        } else {
            resetSpotButton.setVisibility(View.INVISIBLE);
        }

        Log.d("debug", spotNum);

        String spot_text = "Sorry, could not find your spot. Please try again.";
        if(spotNum != null) {
            spot_text = spotNum.toString();
        }

        // ADD SOME TEXT THATS LIKE, THIS SPOT IS CURRENTLY EXPIRED OR
        //      THIS SPOT WILL EXPIRE AT "time"

        final TextView textview = (TextView) findViewById(R.id.spot_num_text);
        textview.setText("Space #"+ spot_text);
        textview.setTextSize(40);



        //janky get lot id - currently always lot 1

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParkingSpot");
        query.whereEqualTo("Lot_Name", selectedLot);
        query.whereEqualTo("SpotName", spotNum);

        query.findInBackground(new FindCallback<ParseObject>() {
            //@Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null && parseObjects.size() > 0) {
                    selectedLotObj = parseObjects.get(0);


                    resetSpotButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ParseObject dataObject = ParseObject.createWithoutData("ParkingSpot", selectedLotObj.getObjectId());

                            dataObject.put("PaidForUntil", new Date());
                            dataObject.saveInBackground(new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        ApplicationConfig.resetSpot();
                                        Intent intent = new Intent(PayActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        throw new RuntimeException();
                                    }
                                }
                             });
                        }

                    });

                } else {
                    System.out.println(e.getMessage());
                    throw new RuntimeException();
                }
            }
        });


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
                        SimpleDateFormat format = new SimpleDateFormat("MMM dd, hh:mm a");
                        // Probably want a confirmation here
                        ParseObject dataObject = ParseObject.createWithoutData("ParkingSpot", selectedLotObj.getObjectId());

                        dataObject.put("PaidForUntil", selectedTime);
                        dataObject.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    // saved successfully
                                    Log.d("test", "parse object saved");
                                    Context context = getApplicationContext();

                                    //make pretty time
                                    SimpleDateFormat format = new SimpleDateFormat("MMM dd, hh:mm a");
                                    String time = "Expires at: " + format.format(selectedTime);

                                    int duration = Toast.LENGTH_LONG;
                                    Toast toast = Toast.makeText(context, "Payment Successful!\n"+time, duration);
                                    toast.show();

                                    final TextView time_remaining_text = (TextView) findViewById(R.id.time_remaining_text);
                                    time_remaining_text.setText(time);
                                    time_remaining_text.setTextSize(32);

                                    resetSpotButton.setVisibility(View.VISIBLE);

                                    ApplicationConfig.setSpotTaken(spotNum);
                                    ApplicationConfig.setLotName(lotName);

                                } else {
                                    System.out.println(e.getMessage());
                                    System.out.println("Object: " + selectedLotObj.getObjectId());
                                    //did not save
                                    Toast toast = Toast.makeText(getApplicationContext(), "Error: Unable to Buy Spot!", Toast.LENGTH_LONG);
                                    toast.show();
                                    Log.d("error", e.toString());
                                }
                            }
                        });


                    }
                }, curHour, curMinute, false);
        tpd.show();
    }
}
