package com.parkandpay.aaron.parkandpay;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//Toast.makeText(getApplicationContext(), "testing123", Toast.LENGTH_SHORT).show();

public class SpotsTakenActivity extends ActionBarActivity {

    private static Bundle spot_bundle = new Bundle();
    private static ParseObject selectedLot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spots_taken);
        Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "D3e1FmZef0xReoXZlWFVGgxIosiuUfusQ9jgHT7y", "FFUhtwW99qsT4ExUj2kepUhKVAibc3MsMJYJGf2x");
    }

    @Override
    protected void onStart(){
        super.onStart();
        populateSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_spots_taken, menu);
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

    public static void setSelected(ParseObject object) {
        selectedLot = object;
    }

    public void refresh(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.lot_spinner);
        getLotObjectFromName(spinner.getSelectedItem().toString());
        getDataFromParse(selectedLot);
        addStatusToBundle();
        showAvailable();
    }


    public void showAvailable() {
        List<String> values = new ArrayList<String>();
        for (int i = 0; i < spot_bundle.size(); i++) {
            values.add(i, "");
        }
        SpotsList adapter = new SpotsList(this.getApplicationContext(), spot_bundle, values);
        adapter.notifyDataSetChanged();
        ListView listView = (ListView) findViewById(R.id.lots_list);
        listView.setAdapter(adapter);
    }


    public void addStatusToBundle() {
        Calendar paid_for_time = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        for (Integer i = 0; i < spot_bundle.size(); i++){
            paid_for_time = ((Calendar) ((Bundle) spot_bundle.get(i.toString())).get("time"));
            now = Calendar.getInstance();
//            now.set(Calendar.HOUR, 10);
//            now.set(Calendar.MINUTE, 0);
//            now.set(Calendar.AM_PM, 0);
            if (paid_for_time.after(now)) {
                ((Bundle) spot_bundle.get(i.toString())).putBoolean("gets_ticket", false);
            }
            else {
                ((Bundle) spot_bundle.get(i.toString())).putBoolean("gets_ticket", true);
            }
        }
    }

    public void getLotObjectFromName(String lot) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParkingLot");
        query.whereEqualTo("Lot_Name", lot);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                selectedLot = parseObjects.get(0);
            }
        });
    }

    public void getDataFromParse(ParseObject lot) {
        // parse query to get spot time info
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParkingSpot");
        query.whereEqualTo("Lot", lot);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    Integer count = -1;
                    for (ParseObject object : parseObjects) {
                        Date spot_date = object.getDate("PaidForUntil");
                        Bundle spot = new Bundle();
                        Calendar spottime = Calendar.getInstance();
                        spottime.setTime(spot_date);
                        spot.putSerializable("time", spottime);
                        spot.putString("name", object.getString("SpotName"));
                        count += 1;
                        spot_bundle.putBundle(count.toString(), spot);
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void populateSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.lot_spinner);
        final List<ParseObject> lotObjects = new ArrayList<ParseObject>();
        // Parse query to get the lot names
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParkingLot");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : parseObjects) {
                        lotObjects.add(object);
                    }
                }
            }
        });
        String[] values = {"Lot_1" , "Lot_2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


/*     custom spinner with objects stored
        final CustomSpinner adapter = new CustomSpinner(this, android.R.layout.simple_spinner_dropdown_item, lotObjects);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpotsTakenActivity.selectedLot = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
*/
    }

}
