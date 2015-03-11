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
import java.util.Collections;
import java.util.List;
import java.lang.Integer;

import com.parse.ParseQuery;
import com.parse.ParseObject;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;


public class SelectSpotActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_spot);

        createSpotList();

    }

    private static String lot_name_c = "l8W9nV5ami";

    public void createSpotList(){
        //create a list of strings
        final ListView listview = (ListView) findViewById(R.id.availSpotsListView);
        final ArrayList<String> list = new ArrayList<String>();
        String currentLot = lot_name_c; // TODO - fix this hardcode

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ParkingSpot").whereEqualTo("Lot_Name", currentLot);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); ++i) {
                        list.add(objects.get(i).getString("SpotName"));
                    }
                    // Sort spot numbers
                    Collections.sort(list);

                    final ArrayAdapter adapter = new ArrayAdapter(SelectSpotActivity.this,
                            android.R.layout.simple_list_item_1, list);
                    listview.setAdapter(adapter);

                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view,
                                                int position, long id) {
                            final String item = parent.getItemAtPosition(position).toString();
                            Log.d("debug", item);

                            Intent intent = new Intent(view.getContext(), PayActivity.class);
                            intent.putExtra("spot_num", item);
                            intent.putExtra("lot_name", lot_name_c);

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
                } else {
                    // throw new RuntimeException();
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
