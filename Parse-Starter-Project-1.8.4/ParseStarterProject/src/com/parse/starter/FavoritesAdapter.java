package com.parse.starter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * The FavoritesAdapter class works to display the favorites in a dynamic list form
 * @author  shilpa Kannan, Srinidhi Raghavan
 */
public class FavoritesAdapter extends ArrayAdapter<String> {

    //Context
    private final Context context;
    //All the different possible reviews
    private final ArrayList<String> favoriteItemsList;

    public FavoritesAdapter(Context context, ArrayList<String> favoriteList) {
        super(context, R.layout.review_row, favoriteList);
        this.context = context;
        this.favoriteItemsList = favoriteList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Create inflater
        final int pos = position;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.favorites_row, parent, false);

        // 3. Get the two text view from the rowView
        TextView foodtruckView = (TextView) rowView.findViewById(R.id.FoodTruckName);

        // 4. Set the text for textView
        foodtruckView.setText(favoriteItemsList.get(position));
        foodtruckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, FoodTruckActivity.class);
                //extra information added so the FoodTruckPage can populate with correct data
                i.putExtra("TRUCK_NAME", favoriteItemsList.get(pos));
                context.startActivity(i);
            }

        });
 
        // 5. return rowView
        return rowView;
    }
}
