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



public class MapTest extends Activity implements OnMapReadyCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_test);

       MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
       mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        final GoogleMap MAP = map;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FoodTruck");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> foodtrucks, ParseException e) {
                if (e == null) {
                    for (ParseObject food: foodtrucks) {
                        ParseGeoPoint location = (ParseGeoPoint) food.get("location");
                        String name = (String) food.get("name");
                        Boolean healthy = (Boolean) food.get("hasHealthyOptions");
                        ArrayList<Integer> opening_times = (ArrayList<Integer>)food.get("opening_times");
                        ArrayList<Integer> closing_times = (ArrayList<Integer>)food.get("closing_times");
                        Boolean open = isOpen(opening_times, closing_times);

                        LatLng store = new LatLng(location.getLatitude(), location.getLongitude());

                       if (healthy) {
                           if (open) {
                               MAP.addMarker(new MarkerOptions()
                                       .title(name)
                                       .snippet("Open")
                                       .position(store)
                                       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                           } else {
                               MAP.addMarker(new MarkerOptions()
                                       .title(name)
                                       .snippet("Closed")
                                       .position(store)
                                       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                           }
                        } else {
                           if (open) {
                               MAP.addMarker(new MarkerOptions()
                                       .title(name)
                                       .snippet("Open")
                                       .position(store));
                           } else {
                               MAP.addMarker(new MarkerOptions()
                                       .title(name)
                                       .snippet("Closed")
                                       .position(store));
                           }
                        }
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });


        map.setMyLocationEnabled(true);

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        Location location = service.getLastKnownLocation(provider);
        LatLng userLocation;
        if (location == null) {
            userLocation = new LatLng(39.950948, -75.195417);
        } else {
            userLocation = new LatLng(location.getLatitude(),location.getLongitude());
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13));

    }

    public void goSearch(View v){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    public boolean isOpen(ArrayList<Integer> open, ArrayList<Integer> close) {
        Calendar current = Calendar.getInstance();
        int dayOfWeek = current.get(Calendar.DAY_OF_WEEK)-1;
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
        String currentTime = sdf.format(current.getTime());
        int militaryTime = Integer.parseInt(currentTime);
        int openTime = open.get(dayOfWeek);
        int closeTime = close.get(dayOfWeek);
        if(openTime == -1 || militaryTime < openTime || militaryTime > closeTime) {
            return false;
        } else {
            return true;
        }
    }

}

