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
    private List<ParseObject> foodTrucks;
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
            Log.v("bundles","x" + sorted);
            RadioButton selected = (RadioButton) findViewById(sorted);
        }
        //handle creating the onClickListener for the arrayList
        arrayListOnClick();

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

    private void arrayListOnClick(){
        ListView foodList = (ListView) findViewById(R.id.listView);

        foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String truckName = ((TextView) view).getText().toString();
                Intent i = new Intent(getApplicationContext(), FoodTruckPage.class);
                i.putExtra("TRUCK_NAME", truckName);
                startActivity(i);
            }
        });
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
            } else if(sorted == R.id.radio_distance){
                query.orderByDescending("location");
                try {
                    foodTrucks = query.find();
                } catch (ParseException e) {
                }

            } else if (sorted == R.id.radio_food){
                query.orderByDescending("categories");
                try {
                    foodTrucks = query.find();
                } catch (ParseException e) {
                }
            } else if (sorted == R.id.radio_open){
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
                        ArrayList<Integer> open = (ArrayList<Integer>)f.get("opening_times");
                        ArrayList<Integer> close = (ArrayList<Integer>)f.get("closing_times");
                        int openTime = open.get(dayOfWeek);
                        int closeTime = close.get(dayOfWeek);
                        if(openTime == -1 || militaryTime < openTime || militaryTime > closeTime)
                            foodTrucks.remove(f);
                    }
                } catch (ParseException e) {
                }
            } else if (sorted == R.id.radio_healthy){
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
        new RemoteDataTask().execute();
    }

    //Method: Rebecca
    public void goSpecials(View v){
        Intent i = new Intent(getApplicationContext(), Specials.class);
        startActivity(i);
    }

    //Method: Nikila & Paarth
    public void goMap(View v) {
        Intent i = new Intent(getApplicationContext(), NearbyActivity.class);
        startActivity(i);
    }

}
