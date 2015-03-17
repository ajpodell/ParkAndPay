package com.parkandpay.aaron.parkandpay;

/**
 * Created by DSoff on 3/8/2015.
 */
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SpotsList extends ArrayAdapter<String> {
    private final Context context;
    private final Bundle info;
    private final List<String> values;

    public  SpotsList(Context context, Bundle info, List<String> values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.info = info;
        this.values = values;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        Integer pos = position;
        Bundle temp_spot = (Bundle) info.get(pos.toString());

        //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
        String str = dateFormat.format(((Calendar) temp_spot.get("time")).getTime());


        if (temp_spot.getBoolean("gets_ticket")) {
            imageView.setImageResource(R.drawable.x_icon);
            textView.setText(temp_spot.getString("name") + " is unpaid for!");
            textView.setTextColor(Color.RED);
        }
        else {
            imageView.setImageResource(R.drawable.check_icon);
            textView.setText(temp_spot.getString("name") + " is paid for until " + str);
            textView.setTextColor(Color.YELLOW);
        }

        return rowView;
    }
}