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
    private List<ParseObject> foodTrucks;
    static String foodTruckName = "Cucina Zapata";
    static String address;
    static String phoneNum;
    static String categories;
    static String url;

    //Method: Rebecca
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_truck_page);
       // Intent intent = getIntent();
        //foodTruckName = intent.getStringExtra(MainActivity.TRUCK_ID);
        //TextView name = (TextView) findViewById(R.id.name);
        //name.setText(foodTruckName);
        new GetFoodTruckInfo().execute();

        TextView reviewScreen = (TextView) findViewById(R.id.ReviewClick);
        reviewScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), ReviewPage.class);
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

    //Method: Nikila & Paarth
    public void goMap(View v) {
        Intent i = new Intent(getApplicationContext(), MapTest.class);
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

    //method: rebecca
    private class GetFoodTruckInfo extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            //Get the current list of foodtrucks
            final String[] result = new String[4];
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FoodTruck");
            query.whereEqualTo("name", foodTruckName);
            try {
                foodTrucks = query.find();
            } catch (ParseException e) {
            }
            return null;
        }

        // Rebecca: Method is still not correct, callback function cannot set global variables
        @Override
        protected void onPostExecute(Void v) {
            //Put the new information into the view
            String[] result = new String[4];
            //pulls the data from the database
            for (ParseObject truck: foodTrucks) {
                result[0] = (String) truck.get("address");
                Log.v("result", "x" + result[0]);
                result[1] = "tel:" + (String) truck.get("phoneNum");
                Log.v("result", "x" + result[1]);
                result[2] = (String) truck.get(categories);
                Log.v("result", "x" + result[2]);
                result[3] = (String) truck.get("url");
                Log.v("result", "x" + result[3]);
            }
            //places the data in the UI
            TextView name = (TextView) findViewById(R.id.name);
            name.setText(foodTruckName);
            TextView addresser = (TextView) findViewById(R.id.address);
            Log.v("onPost", "x" + result[0]);
            addresser.setText(result[0]);
            TextView truckGenre = (TextView) findViewById(R.id.genre);
            Log.v("onPost", "x" + result[1]);
            Log.v("onPost", "x" + result[2]);
            truckGenre.setText(result[2]);
        }
    }

    public void photoClick(View v){
        Intent i = new Intent(this, PictureSwiper.class);
        startActivity(i);
    }

    public void seeMoreReviews(View v){
        Log.v("food", "got here");
        Intent i = new Intent(this, ReviewPage.class);
        startActivity(i);
    }

}
