package com.parkandpay.aaron.parkandpay;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PayActivity extends ActionBarActivity {

    private Date selectedTime;
    private String spotNum;
    private String lotName;
    private static ParseObject selectedLotObj;
    private static TextView resetSpotButton;
    private static TextView costView;
    private static TextView selectTimeButton;
    private static double cost_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        // initialize all clickable buttons and text views that are used in multiple functions
        Intent intent = getIntent();
        spotNum = intent.getStringExtra("spot_num");
        lotName = intent.getStringExtra("lot_name");
        resetSpotButton = (TextView) findViewById(R.id.resetSpotButton);
        costView = (TextView) findViewById(R.id.cost);
        selectTimeButton = (TextView) findViewById(R.id.time_remaining_text);

        // if the user has already paid for a spot, use the information from that to populate text fields
        if(ApplicationConfig.hasSpot()) {
            costView.setVisibility(View.INVISIBLE);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("ParkingSpot");
            query.whereEqualTo("Lot_Name", lotName);
            query.whereEqualTo("SpotName", spotNum);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if(e == null && parseObjects.size() > 0) {
                        selectedTime = (Date) selectedLotObj.get("PaidForUntil");
                        selectTimeButton.setText(new SimpleDateFormat("hh:mm a", Locale.US).format(selectedTime));
                    } else {
                        System.out.println(e.getMessage());
                        throw new RuntimeException();
                    }
                }
            });

        // if the user has not paid for a spot, populate text fields with default information
        } else {
            costView.setVisibility(View.VISIBLE);
            costView.setText("$0.00");
            resetSpotButton.setVisibility(View.INVISIBLE);
            selectedTime = Calendar.getInstance().getTime();
            selectTimeButton.setText(new SimpleDateFormat("hh:mm a", Locale.US).format(selectedTime));
        }

        Log.d("debug", spotNum);
        String spot_text = "Sorry, could not find your spot. Please try again.";
        if(spotNum != null) {
            spot_text = spotNum;
        }

        // set the date and the space number
        ((TextView) findViewById(R.id.date_text)).setText(new SimpleDateFormat("MMMM dd", Locale.US).format(Calendar.getInstance().getTime()));
        ((TextView) findViewById(R.id.spot_num_text)).setText(lotName + " Parking Space #" + spot_text);

        //janky get lot id - currently always lot 1
        // TODO: Someone add comments to explain what's going on here!
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParkingSpot");
        query.whereEqualTo("Lot_Name", lotName);
        query.whereEqualTo("SpotName", spotNum);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null && parseObjects.size() > 0) {
                    selectedLotObj = parseObjects.get(0);
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

    // TODO: add different functionality to allow a user to add time to an already purchased space don't charge them more for it.
    public void onClickAddTime(View view) {
        Log.d("test", "add time button clicked");

        // Creates Calendar dialog
        final Calendar c = Calendar.getInstance();
        Integer curHour = c.get(Calendar.HOUR_OF_DAY);
        Integer curMinute = c.get(Calendar.MINUTE);
        TimePickerDialog tpd = new TimePickerDialog(this,
            new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    // this is an async callback
                    Log.d("test", "should be second: " + Integer.toString(hourOfDay));
                    Log.d("test", selectedTime.toString());

                    Calendar tempTime = Calendar.getInstance();
                    tempTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    tempTime.set(Calendar.MINUTE, minute);
                    selectedTime = tempTime.getTime();
                    selectTimeButton.setText(new SimpleDateFormat("hh:mm a", Locale.US).format(selectedTime));

                    // calculates cost based on the difference of the selected time and the current time.
                    cost_value = 0.70*Math.ceil(((double) ((selectedTime.getTime() - Calendar.getInstance().getTimeInMillis())/(1000 * 60))) / 30);
                    if(cost_value < 0) {
                        cost_value = 0;
                    }
                    costView.setText("$" + String.format("%.2f", cost_value));
                }

            }, curHour, curMinute, false);
        tpd.show();
    }

    public void onClickPay(View view) {
        Log.d("test", "pay button clicked");

        // Fields for the confirmation dialog
        AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(this);
        confirmationDialog.setTitle("Payment Details");
        confirmationDialog.setMessage("lot name: " + lotName +
                "\nspace: " + spotNum +
                "\ndate: " + new SimpleDateFormat("MMMM dd", Locale.US).format(selectedTime) +
                "\nexpiration time: " + new SimpleDateFormat("hh:mm a", Locale.US).format(selectedTime) +
                "\n\ncost: $" + String.format("%.2f", cost_value));
        confirmationDialog.setCancelable(true);

        // The confirm button: will send data to parse if payment is confirmed
        confirmationDialog.setPositiveButton("Confirm",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ParseObject dataObject = ParseObject.createWithoutData("ParkingSpot", selectedLotObj.getObjectId());

                    dataObject.put("PaidForUntil", selectedTime);
                    dataObject.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // saved successfully
                                Log.d("test", "parse object saved");

                                resetSpotButton.setVisibility(View.VISIBLE);

//                                Context context = getApplicationContext();
//
//                                int duration = Toast.LENGTH_LONG;
//                                Toast toast = Toast.makeText(context, "Payment Successful!\n" +
//                                        "Expires at: " + new SimpleDateFormat("MMMM dd, hh:mm a",
//                                        Locale.US).format(selectedTime), duration);
//                                toast.show();


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
                    });                    }
            });

        // cancel button: will NOT send payment data to parse
        confirmationDialog.setNegativeButton("Cancel",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        confirmationDialog.create().show();
    }

    public void onClickReset(View view) {

        // fields for the reset dialog
        AlertDialog.Builder resetDialog = new AlertDialog.Builder(this);
        resetDialog.setMessage("Are you sure you want to cancel your reservation?");
        resetDialog.setCancelable(true);

        // if yes is selected, will cancel payment and update parse information
        resetDialog.setPositiveButton("Yes",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    ParseObject dataObject = ParseObject.createWithoutData("ParkingSpot", selectedLotObj.getObjectId());
                    dataObject.put("PaidForUntil", new Date());
                    dataObject.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                ApplicationConfig.resetSpot();
                                startActivity(new Intent(PayActivity.this, MainActivity.class));
                            } else {
                                throw new RuntimeException();
                            }
                        }
                    });

                }
            });

        // if no is selected, not update parse information
        resetDialog.setNegativeButton("No",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        resetDialog.create().show();
    }
}
