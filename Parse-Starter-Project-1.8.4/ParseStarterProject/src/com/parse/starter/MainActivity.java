package com.parse.starter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.Calendar;
import java.text.SimpleDateFormat;

import android.widget.RadioButton;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import android.widget.SearchView.*;
import android.widget.Toast;

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

        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                ListView foodList = (ListView) findViewById(R.id.listView);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                        android.R.layout.simple_list_item_1);
                if (foodTrucks != null) {
                    for (ParseObject truck : foodTrucks) {
                        SearchView searchView = (SearchView) findViewById(R.id.searchView);
                        String search = searchView.getQuery().toString();
                        String searchLower = search.toLowerCase();
                        String truckName = (String) truck.get("name");
                        String truckNameLower = truckName.toLowerCase();
                        if (truckNameLower.contains(searchLower)) {
                            //add to the adapter each truck's name
                            adapter.insert((String) truck.get("name"), 0);
                        }
                    }
                }
                //set the ListView's adapter to the recently populated adapter
                foodList.setAdapter(adapter);
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });


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
                        if (open == null || close == null) {
                            foodTrucks.remove(f);
                        } else {
                            int openTime = open.get(dayOfWeek);
                            int closeTime = close.get(dayOfWeek);
                            //If the food truck is outside open and close time (or closed entirely)
                            if (openTime == -1 || militaryTime < openTime || militaryTime > closeTime)
                                foodTrucks.remove(f);
                        }
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

                    adapter.insert((String) truck.get("name"), 0);
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

    public void goFavorites(View v){
        if(LoginActivity.userNameSession != null) {
            Intent i = new Intent(getApplicationContext(), FavoritesActivity.class);
            startActivity(i);
        }
        helperDialogLogIn();
    }

    //This is a helper method that contains the dialog functionality to
    //prompt and check a user's password if they are not already logged in
    private void helperDialogLogIn() {
        final Dialog dialog = new Dialog(getApplicationContext());
        dialog.setContentView(R.layout.dialog_login);
        dialog.setTitle("Please log in");

        Button dialogCancel = (Button) dialog.findViewById(R.id.cancel);
        Button dialogLogin = (Button) dialog.findViewById(R.id.SignIn);
        //Cancel makes the dialog go away
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //Log in checks the user name and password
        dialogLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Context appContext = getApplicationContext();
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("User");
                EditText passwordView = (EditText) dialog.findViewById(R.id.passwordDialog);
                final String password = passwordView.getText().toString();
                EditText userNameView = (EditText) dialog.findViewById(R.id.usernameDialog);
                final String userName = userNameView.getText().toString();
                try {
                    query.whereEqualTo("user_name", userName);
                    //Check the users with the same user name
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> users, ParseException e) {
                            if (e == null) {
                                for (ParseObject u : users) {
                                    String passwordMatch = (String) u.get("password");
                                    //If the password is right, go to the add a review
                                    if (passwordMatch.equals(password)) {
                                        LoginActivity.userNameSession = userName;
                                        Intent i = new Intent(getApplicationContext(), FavoritesActivity.class);
                                        startActivity(i);
                                    } else {
                                        //Otherwise show a toast that the log in is incorrect
                                        CharSequence text = "Incorrect Password/UserName";
                                        int duration = Toast.LENGTH_SHORT;

                                        Toast toast = Toast.makeText(appContext, text, duration);
                                        toast.show();
                                    }

                                }
                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });

                } catch (Exception e) {

                }
            }
        });

        dialog.show();
    }

}
