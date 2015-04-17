package com.parse.starter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.Calendar;
import java.text.SimpleDateFormat;

import android.widget.RadioButton;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.ArrayList;


public class MainActivity extends Activity {
    private List<ParseObject> foodTrucks; //all parse data for all foodTrucks
    private int sorted = -1;
    //private int latitude;
    // private int longitude;

    @TargetApi(11)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Sets query hint for search bar at top of activity
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Search for a truck.");

        //starts thread that populates the listView with data
        new RemoteDataTask().execute();

        //gets extra bundles from filtering activity to perform filtering on search results
        Bundle extraBundles = getIntent().getExtras();
        if (extraBundles != null) {
            sorted = extraBundles.getInt("filter");
            Log.v("bundles", "x" + sorted);
            RadioButton selected = (RadioButton) findViewById(sorted);
        }
        //Redirects a user to the relevant food truck's page
        redirectFoodTruckPage();

        /*
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener listener = new LocationListener() {
            public void onLocationChanged(Location location) {
                point = new GeoPoint(location.getLatitude(), location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);*/
    }

    //Method: Rebecca. Redirects a user to the food truck page who's name they clicked on.
    private void redirectFoodTruckPage() {
        ListView foodList = (ListView) findViewById(R.id.listView);

        //listener for items in the ListView
        foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String truckName = ((TextView) view).getText().toString();
                Intent i = new Intent(getApplicationContext(), FoodTruckActivity.class);
                //extra information added so the FoodTruckPage can populate with correct data
                i.putExtra("TRUCK_NAME", truckName);
                startActivity(i);
            }
        });
    }

    //This class pulls all the relevant information about each food truck name from the database
    //and, if the user has chosen to filter, only displays relevant options.
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {

        //Method: Shilpa. Enables user to filter food trucks
        protected Void doInBackground(Void... params) {
            //Get the current list of foodtrucks
            ParseQuery<ParseObject> query = new ParseQuery<>("FoodTruck");
            if (sorted == -1) {//If no option is selected, sort by name
                query.orderByDescending("name");
                try {
                    foodTrucks = query.find();
                } catch (ParseException e) {
                }
            } else if (sorted == R.id.radio_distance) {
                query.orderByDescending("location");
                try {
                    foodTrucks = query.find();
                } catch (ParseException e) {
                }

            } else if (sorted == R.id.radio_food) {//Sort by food categories
                query.orderByDescending("categories");
                try {
                    foodTrucks = query.find();
                } catch (ParseException e) {
                }
            } else if (sorted == R.id.radio_open) {//Find the food trucks currently open
                Calendar current = Calendar.getInstance();
                int dayOfWeek = current.get(Calendar.DAY_OF_WEEK) - 1;
                //Change format to military time
                SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
                String currentTime = sdf.format(current.getTime());
                int militaryTime = Integer.parseInt(currentTime);
                query.orderByAscending("opening_times");
                try {
                    foodTrucks = query.find();
                    List<ParseObject> foodTrucksCopy = query.find();
                    for (ParseObject f : foodTrucksCopy) {
                        ArrayList<Integer> open = (ArrayList<Integer>) f.get("opening_times");
                        ArrayList<Integer> close = (ArrayList<Integer>) f.get("closing_times");
                        int openTime = open.get(dayOfWeek);
                        int closeTime = close.get(dayOfWeek);
                        //If the food truck is outside open and close time (or closed entirely)
                        if (openTime == -1 || militaryTime < openTime || militaryTime > closeTime)
                            foodTrucks.remove(f);
                    }
                } catch (ParseException e) {
                }
            } else if (sorted == R.id.radio_healthy) {//Find the food trucks with healthy options
                query.whereEqualTo("hasHealthyOptions", true);
                try {
                    foodTrucks = query.find();
                } catch (ParseException e) {
                }
            }
            return null;
        }

        //Method: Rebecca: This method populates the ListView with the names of each FoodTruck
        @Override
        protected void onPostExecute(Void result) {
            ListView foodList = (ListView) findViewById(R.id.listView);
            //creates an ArrayAdapter to populate the list
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_list_item_1);
            if (foodTrucks != null) {
                for (ParseObject truck : foodTrucks) {
                    //add to the adapter each truck's name
                    adapter.add((String) truck.get("name"));
                }
            }
            //set the ListView's adapter to the recently populated adapter
            foodList.setAdapter(adapter);
        }
    }

    //Filter button click: redirects the user to the Filter page
    public void onFilterClicked(View view) {
        Intent i = new Intent(this, FilterActivity.class);
        startActivity(i);
        new RemoteDataTask().execute();
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

}
