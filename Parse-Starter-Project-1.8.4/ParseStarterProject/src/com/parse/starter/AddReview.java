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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * The add review activity. Enables users to write and submit new reviews
 * @author Srinidhi Raghavan, Shilpa Kannan
 */


public class AddReview extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        TextView foodTruck = (TextView) findViewById(R.id.foodTruckAddRev);
        //Display the food truck name
        foodTruck.setText(FoodTruckActivity.foodTruckName);
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


    //Saves the Review to the database when the submit button is hit
    public void submitReview(View v) {
        //Send to parse
        EditText et = (EditText) findViewById(R.id.reviewBox);
        String review = et.getText().toString();
        //Get the parseObject related to specific food truck
        ParseQuery<ParseObject> queryFoodTruck = new ParseQuery<ParseObject>("FoodTruck");
        queryFoodTruck.whereEqualTo("name", FoodTruckActivity.foodTruckName);
        try {

            List<ParseObject> foodTrucks = queryFoodTruck.find();
            String foodTruckID = foodTrucks.get(0).getObjectId();
            ParseQuery<ParseObject> queryUserName = new ParseQuery<ParseObject>("User");
            //gets username associated with the login session
            queryFoodTruck.whereEqualTo("user_name", LoginActivity.userNameSession);
            List<ParseObject> users = queryUserName.find();
            String userID = users.get(0).getObjectId();
            //creates a new Parse Object to store and added information
            ParseObject newReview = new ParseObject("Review");
            newReview.put("user_id", userID);
            newReview.put("foodtruck_id", foodTruckID);
            newReview.put("text", review);
            newReview.put("user_name", LoginActivity.userNameSession);
            newReview.saveInBackground();
            Intent i = new Intent(getApplicationContext(), ReviewPage.class);
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
