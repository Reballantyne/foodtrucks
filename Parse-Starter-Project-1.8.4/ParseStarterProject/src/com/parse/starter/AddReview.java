package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AddReview extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        //UNCOMMENT THIS
   //     TextView foodTruck = (TextView)findViewById(R.id.foodTruck);
     //   foodTruck.setText(FoodTruckPage.foodTruckName);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_review, menu);
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

    public void submitReview(View v){
        //Send to parse
        EditText et = (EditText) findViewById(R.id.reviewBox);
        String review = et.getText().toString();
        ParseQuery<ParseObject> queryFoodTruck = new ParseQuery<ParseObject>("FoodTruck");
        queryFoodTruck.whereEqualTo("name", FoodTruckPage.foodTruckName);
        try {
            Log.v("AR1:", "got here1");
            List<ParseObject> foodTrucks = queryFoodTruck.find();
            //String foodTruckID = (String) foodTrucks.get(0).get("objectId");
            String foodTruckID = foodTrucks.get(0).getObjectId();
            ParseQuery<ParseObject> queryUserName = new ParseQuery<ParseObject>("User");
            queryFoodTruck.whereEqualTo("user_name", LoginActivity.userNameSession);
            List<ParseObject> users = queryUserName.find();
            Log.v("AR1:", "got here2");
            //String userID = (String) users.get(0).get("objectId");
            String userID = users.get(0).getObjectId();
            Calendar current = Calendar.getInstance();
            Log.v("AR1:", "got here3");
            SimpleDateFormat sdf = new SimpleDateFormat("MMM DD, yyyy, HH:mm");
            Log.v("AR1:", "got here4");
            String dateNow = sdf.format(current.getTime());
            Log.v("AR1:", "got here5");
            Date currentTime = sdf.parse(dateNow);
            Log.v("AR1:", "got here6");
            ParseObject newReview = new ParseObject("Review");
            Log.v("AR1:", "got here7");

            newReview.put("user_id", userID);
            Log.v("AR1:", "got here8");
            newReview.put("foodtruck_id", foodTruckID);
            newReview.put("text", review);
            //newReview.put("createdAt", currentTime);
            Log.v("AR1:username", LoginActivity.userNameSession);
            newReview.put("user_name", LoginActivity.userNameSession);
            newReview.saveInBackground();
            Log.v("AR1:", "got here9");
            //CHANGEDs
            Intent i = new Intent(getApplicationContext(), ReviewPage.class);
            startActivity(i);
        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}
