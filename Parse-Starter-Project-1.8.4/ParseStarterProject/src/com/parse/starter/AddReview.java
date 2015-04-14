package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
            List<ParseObject> foodTrucks = queryFoodTruck.find();
            String foodTruckID = (String) foodTrucks.get(0).get("objectId");
            ParseQuery<ParseObject> queryUserName = new ParseQuery<ParseObject>("User");
            queryFoodTruck.whereEqualTo("user_name", LoginActivity.userNameSession);
            List<ParseObject> users = queryUserName.find();
            String userID = (String) users.get(0).get("objectId");
            Calendar current = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM DD, YYYY, HH:mm");
            String dateNow = sdf.format(current.getTime());
            Date currentTime = sdf.parse(dateNow);

            ParseObject newReview = new ParseObject("Review");
            newReview.put("user_id", userID);
            newReview.put("foodtruck_id", foodTruckID);
            newReview.put("text", review);
            newReview.put("createdAt", currentTime);
            newReview.put("user_name", LoginActivity.userNameSession);

            Intent i = new Intent(getApplicationContext(), ReviewPage.class);
            startActivity(i);
        } catch (Exception e) {


        }

    }
}