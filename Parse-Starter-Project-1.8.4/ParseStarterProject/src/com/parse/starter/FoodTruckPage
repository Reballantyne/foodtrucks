package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by Rebecca_2 on 3/29/2015.
 */
public class FoodTruckPage extends Activity {
    static String foodTruckName;
    static String address;
    static String phoneNum;
    static String categories;
    static String url;

    //Method: Rebecca
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_truck_page);
        //Intent intent = getIntent();
        //foodTruckName = intent.getStringExtra(MainActivity.TRUCK_ID);
        //TextView name = (TextView) findViewById(R.id.name);
        //name.setText(foodTruckName);
        //new GetFoodTruckInfo().execute();
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
    public void callTruck(View v){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:123456789"));
        EndCallListener callListener = new EndCallListener();
        TelephonyManager mTM = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
        startActivity(callIntent);
    }

    //Method: Rebecca
    public void viewWebsite(View v){
        Intent intent = new Intent("android.intent.action.VIEW",
                    Uri.parse("http://www.google.com/"));
        startActivity(intent);
    }

    //Method: Rebecca
    public void goSearch(View v){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    //Method: Rebecca
    private class EndCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if(TelephonyManager.CALL_STATE_RINGING == state) {
                Log.i("phone error", "RINGING, number: " + incomingNumber);
            }
            if(TelephonyManager.CALL_STATE_OFFHOOK == state) {
                //wait for phone to go offhook (probably set a boolean flag) so you know your app
                // initiated the call.
                Log.i("phone error", "OFFHOOK");
            }
            if(TelephonyManager.CALL_STATE_IDLE == state) {
                //when this state occurs, and your flag is set, restart your app
                Log.i("phone error", "IDLE");
            }
        }
    }


    //Method: Rebecca
    private class GetFoodTruckInfo extends AsyncTask<Void, Void, String[]> {
        protected String[] doInBackground(Void... params) {
            //Get the current list of foodtrucks
            final String[] result = new String[4];
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FoodTruck");
            query.whereEqualTo("name", foodTruckName);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> trucks, ParseException e) {
                if (e == null) {
                    for (ParseObject u: trucks) {
                        result[0] = (String) u.get("address");
                        result[1] = "tel:" + (String)u.get("phoneNum");
                        result[2] = (String) u.get(categories);
                        result[3] = (String)u.get("url");
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
                }
           });
           Log.v("parse", "x" + result[0]);
           return result;
        }

        /* Rebecca: Method is still not correct, callback function cannot set global variables
        @Override
        protected void onPostExecute(String[] result) {
            //Put the new information into the view
            TextView name = (TextView) findViewById(R.id.name);
            name.setText(foodTruckName);
            TextView addresser = (TextView) findViewById(R.id.address);
           // Log.v("parse", "x" + result[0]);
            addresser.setText(result[0]);
            TextView truckGenre = (TextView) findViewById(R.id.genre);
            truckGenre.setText(result[2]);
        }
        */
    }

}
