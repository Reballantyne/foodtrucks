package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class FavoritesActivity extends Activity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        FavoritesAdapter adapter = new FavoritesAdapter(this, generateData());
        ListView listView = (ListView) findViewById(R.id.listFavorites);
        listView.setAdapter(adapter);
        context = this;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
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

    //This method pulls all the relevant reviews for a food truck in sorted
    //descending order by number of reviews
    private ArrayList<String> generateData() {
        ArrayList<String> items = new ArrayList<String>();
        ParseQuery<ParseObject> queryFoodTruck = new ParseQuery<ParseObject>("User");
        //Pull the food truck information
        queryFoodTruck.whereEqualTo("user_name", LoginActivity.userNameSession);
        try {
            List<ParseObject> foodTrucks = queryFoodTruck.find();
            String userID = foodTrucks.get(0).getObjectId();
            ParseQuery<ParseObject> queryFavorites = new ParseQuery<ParseObject>("Favorite");
            //Use q food truck ID to find all reviews
            queryFavorites.whereEqualTo("user_id", userID);
            List<ParseObject> favorites = queryFavorites.find();
            int numFavorites = favorites.size();
            TextView favoritesNumber = (TextView) findViewById(R.id.numFavorites);
            //Display the number of reviews
            favoritesNumber.setText(numFavorites + " favorite food trucks");
            //Create a ReviewItem for each review
            for (ParseObject f : favorites) {
                String name = (String) f.get("foodtruck_name");
                items.add(name);
            }
        } catch (Exception e) {


        }
        return items;
    }

    //Menu bar button click: redirects to the specials page
    public void goSpecials(View v) {
        Intent i = new Intent(getApplicationContext(), SpecialsActivity.class);
        startActivity(i);
    }

    //Menu bar button click: redirects to the map page
    public void goMap(View v) {
        Intent i = new Intent(getApplicationContext(), NearbyActivity.class);
        startActivity(i);
    }

    //Menu bar: Redirects to the search page
    public void goSearch(View v) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

}
