package com.parse.starter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.widget.RadioButton;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class MainActivity extends Activity {
    private List<ParseObject> foodTrucks;
    public static String TRUCK_ID;
    private int sorted = -1;
    private int latitude;
    private int longitude;

    @TargetApi(11)
    @Override
    //Method: Rebecca
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Search for a truck.");
        new RemoteDataTask().execute();
                Bundle extraBundles = getIntent().getExtras();
        if(extraBundles != null) {
            sorted = extraBundles.getInt("filter");
            RadioButton selected = (RadioButton) findViewById(sorted);
        }
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

    //Method:Rebecca
    //redirects to food truck page
    public void foodTruckRedirect(View v){
        Intent i = new Intent(getApplicationContext(), FoodTruckPage.class);
        startActivity(i);
    }

    //Method: Rebecca
    //displays the on screen keyboard when a user clicks on the searchView
    public void searchClick(View v){
        SearchView search = (SearchView) findViewById(R.id.searchView);
        search.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
    }

    //Method: Rebecca
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            //Get the current list of foodtrucks
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("FoodTruck");
                        if(sorted == -1) {
                query.orderByDescending("name");
                try {
                    foodTrucks = query.find();
                } catch (ParseException e) {
                }
            } else if(sorted == 0){
                query.orderByDescending("location");
                try {
                    foodTrucks = query.find();
                } catch (ParseException e) {
                }

            } else if (sorted == 1){
                query.orderByDescending("categories");
                try {
                    foodTrucks = query.find();
                } catch (ParseException e) {
                }
            } else if (sorted == 2){
                Calendar current = Calendar.getInstance();
                int dayOfWeek = current.get(Calendar.DAY_OF_WEEK)-1;
                SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
                String currentTime = sdf.format(current.getTime());
                int militaryTime = Integer.parseInt(currentTime);
                query.orderByAscending("opening_times");
                try {
                    foodTrucks = query.find();
                    List<ParseObject> foodTrucksCopy = query.find();
                    for(ParseObject f: foodTrucksCopy){
                        int[] open = (int[])f.get("opening_times");
                        int[] close = (int[])f.get("closing_times");
                        int openTime = open[dayOfWeek];
                        int closeTime = close[dayOfWeek];
                        if(openTime == -1 || militaryTime < openTime || militaryTime > closeTime)
                            foodTrucks.remove(f);
                    }
                } catch (ParseException e) {
                }
            } else if (sorted == 3){
                query.whereEqualTo("hasHealthyOptions", true);
                try {
                    foodTrucks = query.find();
                } catch (ParseException e) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Put the list of todos into the list viewer
            ListView foodList = (ListView) findViewById(R.id.listView);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
            if (foodTrucks != null) {
                for (ParseObject truck : foodTrucks) {
                    adapter.add((String) truck.get("name"));
                }
            }
            foodList.setAdapter(adapter);
        }
    }
    public void onFilterClicked(View view){
            Intent i = new Intent(this, FilterActivity.class);
            startActivity(i);
    }
    //On-Click methods that re-direct to the relevant FoodTruckPage - Rebecca

}
