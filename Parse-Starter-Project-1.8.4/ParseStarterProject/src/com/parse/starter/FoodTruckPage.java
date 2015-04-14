package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Rebecca_2 on 3/29/2015.
 */
public class FoodTruckPage extends Activity {
    private List<ParseObject> foodTrucks;
    static String foodTruckName;
    private static String telNumber;
    private static String website;
    private static String hoursOpen;
    private static String openClosed;
    private static boolean hasHealthy;

    //Method: Rebecca
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_truck_page);
        //get information about what food truck was called on
        Intent intent = getIntent();
        foodTruckName = intent.getStringExtra("TRUCK_NAME");
        Log.v("truckName", foodTruckName);
        TextView name = (TextView) findViewById(R.id.name);
        //set foodtruck name
        name.setText(foodTruckName);
        //get the other information about the foodtruck from the database
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
        callIntent.setData(Uri.parse(telNumber));
        EndCallListener callListener = new EndCallListener();
        TelephonyManager mTM = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
        startActivity(callIntent);
    }

    //Method: Rebecca
    public void viewWebsite(View v){
        Intent intent = new Intent("android.intent.action.VIEW",
                    Uri.parse(website));
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

        // Rebecca: Method pulls all foodtruck info from database to populate foodtruck page
        @Override
        protected void onPostExecute(Void v) {
            //Put the new information into the view
            String[] result = new String[4];
            //pulls the data from the database
            for (ParseObject truck: foodTrucks) {
                result[0] = (String) truck.get("address");
                result[1] = "tel:" + (String) truck.get("phoneNum");
                telNumber = result[1];
                ArrayList<String> genre = (ArrayList<String>) truck.get("categories");
                if (genre != null) {
                    result[2] = genre.get(0);
                }
                result[3] = (String) truck.get("url");
                website = result[3];
                //get if it has healthy options
                hasHealthy = (Boolean) truck.get("hasHealthyOptions");
                //get when its open and closed
                Calendar current = Calendar.getInstance();
                int dayOfWeek = current.get(Calendar.DAY_OF_WEEK) - 1;
                ArrayList<Integer> open = (ArrayList<Integer>)truck.get("opening_times");
                ArrayList<Integer> close = (ArrayList<Integer>)truck.get("closing_times");
                int openTime = open.get(dayOfWeek);
                int closeTime = close.get(dayOfWeek);
                formatOpenClosedText(openTime, closeTime);
                //check whether open now or not
                SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
                String currentTime = sdf.format(current.getTime());
                int militaryTime = Integer.parseInt(currentTime);
                if (militaryTime < openTime || militaryTime > closeTime) {
                    { openClosed = "Closed"; }
                }
                else { openClosed = "Open"; }

            }
            //places the data in the UI
            TextView name = (TextView) findViewById(R.id.name);
            name.setText(foodTruckName);
            TextView addresser = (TextView) findViewById(R.id.address);
            addresser.setText(result[0]);
            TextView truckGenre = (TextView) findViewById(R.id.genre);
            truckGenre.setText(result[2]);
            TextView truckTimes = (TextView) findViewById(R.id.hoursToday);
            truckTimes.setText("Hours Today: " + hoursOpen);
            TextView oC = (TextView) findViewById(R.id.oC);
            if (openClosed.equals("Open")) {
                oC.setTextColor(Color.parseColor("#ff318e21"));
            }
            oC.setText(openClosed);
            TextView healthy = (TextView) findViewById(R.id.isHealthy);
            if (hasHealthy) {
                healthy.setText("Healthy");
                healthy.setTextColor(Color.parseColor("#ff318e21"));
            }

        }
    }

    //Method: Rebecca
    private void formatOpenClosedText(int openTime, int closeTime){
        if (openTime == -1) { hoursOpen = "Closed today"; }
        else {
            //change the int for openTime to a properly formatted string
            String openString = "" + openTime;
            String closeString = "" + closeTime;
            Log.v("closeString", closeString);
            String openEnd = openString.substring(openString.length() - 2, openString.length());
            String openBeg = openString.substring(0, openString.length() - 2);
            String closeEnd = closeString.substring(closeString.length() - 2, closeString.length());
            String closeBeg = closeString.substring(0, closeString.length() - 2);
            String startOpenString = openBeg + ":" + openEnd;
            String startCloseString = closeBeg + ":" + closeEnd;
            hoursOpen = startOpenString + " - " + startCloseString;
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
