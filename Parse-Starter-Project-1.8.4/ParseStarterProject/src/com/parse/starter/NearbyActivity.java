package com.parse.starter;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.*;

import android.util.*;

import java.text.SimpleDateFormat;
import java.util.*;

import android.view.View;
import android.location.*;

// Creators: Paarth Taneja & Nikila Venkat
// Implements "Nearby" tab. Displays map, adds markers corresponding to food truck locations.
public class NearbyActivity extends Activity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    // Creates markers for food trucks & displays them on map.
    @Override
    public void onMapReady(GoogleMap map) {
        final GoogleMap MAP = map;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FoodTruck");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> foodtrucks, ParseException e) {
                if (e == null) {

                    // Iterate through all food trucks, extract info to be displayed on marker.
                    for (ParseObject currentFoodTruck : foodtrucks) {
                        ParseGeoPoint location = (ParseGeoPoint) currentFoodTruck.get("location");
                        String name = (String) currentFoodTruck.get("name");
                        Boolean isHealthy = (Boolean) currentFoodTruck.get("hasHealthyOptions");
                        ArrayList<Integer> openingTimes =
                                (ArrayList<Integer>) currentFoodTruck.get("opening_times");
                        ArrayList<Integer> closingTimes =
                                (ArrayList<Integer>) currentFoodTruck.get("closing_times");
                        String openStatus = isOpen(openingTimes, closingTimes);

                        LatLng foodTruckCoordinates = new LatLng(location.getLatitude(),
                                location.getLongitude());

                        //This determines the marker color
                        Float markerColor;
                        if (isHealthy) {
                            markerColor = BitmapDescriptorFactory.HUE_GREEN;
                        } else {
                            markerColor = BitmapDescriptorFactory.HUE_RED;
                        }

                        // Add markers to map
                        MAP.addMarker(new MarkerOptions()
                                .title(name)
                                .snippet(openStatus)
                                .position(foodTruckCoordinates)
                                .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
                        MAP.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            public void onInfoWindowClick(Marker marker) {
                                Intent i = new Intent(getApplicationContext(), FoodTruckActivity.class);
                                i.putExtra("TRUCK_NAME", marker.getTitle());
                                startActivity(i);
                            }
                        });
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });


        // Zoom map to user location.
        map.setMyLocationEnabled(true);
        LatLng userLocation = findUserLocation();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));

    }

    // Finds the user's current latitude and longitude.
    public LatLng findUserLocation() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        Location location = service.getLastKnownLocation(provider);
        LatLng userLocation;
        if (location == null) {
            //This is used as a default because sometimes Genymotion does not have a location stored
            userLocation = new LatLng(39.950948, -75.195417);
        } else {
            userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
        return userLocation;
    }

    // Checks if a food truck is currently open. Returns the string "Open" or "Closed"
    public String isOpen(ArrayList<Integer> open, ArrayList<Integer> close) {
        Calendar currentCalendar = Calendar.getInstance();
        int dayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        String currentTime = sdf.format(currentCalendar.getTime());
        int militaryTime = Integer.parseInt(currentTime);
        int openTime = open.get(dayOfWeek);
        int closeTime = close.get(dayOfWeek);
        if (openTime == -1 || militaryTime < openTime || militaryTime > closeTime) {
            return "Closed";
        } else {
            return "Open";
        }
    }

    //Menu bar: Redirects to the search page
    public void goSearch(View v) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    //Menu bar: Redirects to the specials page
    public void goSpecials(View v) {
        Intent i = new Intent(getApplicationContext(), SpecialsActivity.class);
        startActivity(i);
    }

}

