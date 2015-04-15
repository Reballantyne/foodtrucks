package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
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
        ImageView picBox = (ImageView) findViewById(R.id.PictureBox);
        Bitmap screenPic = getFirstPicture();
        if (picBox != null){
            picBox.setImageBitmap(screenPic);
        }
        reviewScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), ReviewPage.class);
                startActivity(i);
            }
        });

    }


    private Bitmap getFirstPicture() {

        try {
            ParseQuery<ParseObject> queryFoodTruck = new ParseQuery<ParseObject>("FoodTruck");
            queryFoodTruck.whereEqualTo("name", FoodTruckPage.foodTruckName);
            List<ParseObject> foodTrucks = queryFoodTruck.find();
            ParseObject foodTruckO = foodTrucks.get(0);
            String foodTruckID = (String) foodTruckO.getObjectId();
            ParseQuery<ParseObject> queryPhotos = new ParseQuery<ParseObject>("Photo");
            queryPhotos.whereEqualTo("foodtruck_id", foodTruckID);
            List<ParseObject> dataPhotos = queryPhotos.find();
            for (ParseObject photoObject : dataPhotos) {
                ParseFile photo = (ParseFile) photoObject.get("photo_file");
                byte[] file = photo.getData();
                Bitmap image = BitmapFactory.decodeByteArray(file, 0, file.length);
                return image;
            }
        } catch (Exception e ){

        }
        return null;
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
    public void callTruck(View v) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(telNumber));
        EndCallListener callListener = new EndCallListener();
        TelephonyManager mTM = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
        startActivity(callIntent);
    }

    //Method: Rebecca
    public void viewWebsite(View v) {
        Intent intent = new Intent("android.intent.action.VIEW",
                Uri.parse(website));
        startActivity(intent);
    }

    //Method: Rebecca
    public void goSearch(View v) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    //Method: Nikila & Paarth
    public void goMap(View v) {
        Intent i = new Intent(getApplicationContext(), NearbyActivity.class);
        startActivity(i);
    }

//Method: Rebecca
private class EndCallListener extends PhoneStateListener {
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        if (TelephonyManager.CALL_STATE_RINGING == state) {
            Log.i("phone error", "RINGING, number: " + incomingNumber);
        }
        if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
            //wait for phone to go offhook (probably set a boolean flag) so you know your app
            // initiated the call.
            Log.i("phone error", "OFFHOOK");
        }
        if (TelephonyManager.CALL_STATE_IDLE == state) {
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
        for (ParseObject truck : foodTrucks) {
            result[0] = (String) truck.get("address");
            result[1] = "tel:" + (String) truck.get("phoneNum");
            telNumber = result[1];
            ArrayList<String> genre = (ArrayList<String>) truck.get("categories");
            if (genre != null) {
                result[2] = genre.get(0);
            }
            result[3] = (String) truck.get("url");
            website = result[3];
            //get when its open and closed
            Calendar current = Calendar.getInstance();
            int dayOfWeek = current.get(Calendar.DAY_OF_WEEK) - 1;
            ArrayList<Integer> open = (ArrayList<Integer>) truck.get("opening_times");
            ArrayList<Integer> close = (ArrayList<Integer>) truck.get("closing_times");
            int openTime = open.get(dayOfWeek);
            int closeTime = close.get(dayOfWeek);
            formatOpenClosedText(openTime, closeTime);
            //check whether open now or not
            SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
            String currentTime = sdf.format(current.getTime());
            int militaryTime = Integer.parseInt(currentTime);
            if (militaryTime < openTime || militaryTime > closeTime) {
                {
                    openClosed = "Closed";
                }
            } else {
                openClosed = "Open";
            }

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
        oC.setText(openClosed);
    }

}

    private void formatOpenClosedText(int openTime, int closeTime) {
        if (openTime == -1) {
            hoursOpen = "Closed today";
        } else {
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


    public void photoClick(View v) {
        Intent i = new Intent(this, PictureSwiper.class);
        startActivity(i);
    }

    public void seeMoreReviews(View v) {
        Intent i = new Intent(this, ReviewPage.class);
        startActivity(i);
    }

}
