package com.parkandpay.aaron.parkandpay;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

import com.parse.ParseObject;

/**
 * Created by DSoff on 3/8/2015.
 */
public class SpotsSpinner extends ArrayAdapter<ParseObject> {
    private Context context;

    private List<ParseObject> data;

    public SpotsSpinner(Context context, int textViewResourceID, List<ParseObject> data) {
        super(context, textViewResourceID, data);
        this.context = context;
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.size();
    }
    @Override
    public ParseObject getItem(int position) {
        return data.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        textView.setTextColor(Color.BLACK);
        textView.setText(data.get(position).getString("Lot_Name"));
        return textView;
    }

    @Override
    public  View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        textView.setTextColor(Color.BLACK);
        textView.setText(data.get(position).getString("Lot_Name"));
        return textView;
    }
}
