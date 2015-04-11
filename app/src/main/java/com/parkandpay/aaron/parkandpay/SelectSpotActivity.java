package com.parkandpay.aaron.parkandpay;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Date;

import com.parse.ParseQuery;
import com.parse.ParseObject;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;


public class SelectSpotActivity extends ActionBarActivity {
    Integer NUM_VALUES = 40;

    private static String first_lot_name_c = "l8W9nV5ami";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_spot);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String lot_name = parentView.getItemAtPosition(position).toString();
                createSpotList(lot_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) { }

        });

        createSpotList(first_lot_name_c);

    }

    public void createSpotList(final String lot_name){
        //create a list of strings
        Bundle bundle = getIntent().getExtras();
        final Long selectedTime = bundle.getLong("time");

        final ListView listview = (ListView) findViewById(R.id.availSpotsListView);
        final ArrayList<String> list = new ArrayList<String>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParkingSpot").whereEqualTo("Lot_Name", lot_name);
        Date currentTime = new Date();
        query.whereLessThan("PaidForUntil", currentTime);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); ++i) {
                        String numStr = objects.get(i).getString("SpotName");
                        if(Integer.parseInt(numStr) < 10) numStr = "0" + numStr;
                        list.add(numStr);
                    }
                    // Sort spot numbers
                    Collections.sort(list);  // should do a custom comparator

                    final ArrayAdapter adapter = new ArrayAdapter(SelectSpotActivity.this,
                            android.R.layout.simple_list_item_1, list);
                    listview.setAdapter(adapter);

                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view,
                                                int position, long id) {
                            String item = parent.getItemAtPosition(position).toString(); // need to resolve string vs Integer
                            Integer i = Integer.parseInt(item);
                            item = i.toString();
                            Log.d("debug", item);

                            Intent intent = new Intent(view.getContext(), PayActivity.class);
                            intent.putExtra("spot_num", item);
                            intent.putExtra("lot_name", lot_name);
                            intent.putExtra("time", selectedTime);
                            startActivity(intent);
            
                                
                            /*
                            view.animate().setDuration(2000).alpha(0)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            list.remove(item);
                                            adapter.notifyDataSetChanged();
                                            view.setAlpha(1);
                                        }
                                    });
                                   */
                        }
                    });
                }
            }
        });
    }


    //-----------------BUILT IN--------------------------//
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
}
