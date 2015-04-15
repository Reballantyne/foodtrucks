package com.parse.starter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ReviewPage extends Activity {
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_page);
        ReviewAdapter adapter = new ReviewAdapter(this, generateData());
        TextView foodTruck = (TextView)findViewById(R.id.foodTruck);
        foodTruck.setText(FoodTruckPage.foodTruckName);
        ListView listView = (ListView) findViewById(R.id.listReview);
        listView.setAdapter(adapter);
        context = this;
        Button buttonCreateReview = (Button) findViewById(R.id.wrButton);
        buttonCreateReview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginActivity.userNameSession != null) {
                    Intent i = new Intent(context, AddReview.class);
                    startActivity(i);
                }
                Log.v("in review", "here");
                helperDialogLogIn();
            }
        });
    }

    private void helperDialogLogIn(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_login);
        dialog.setTitle("Please log in");

        Button dialogCancel= (Button) dialog.findViewById(R.id.cancel);
        Button dialogLogin = (Button) dialog.findViewById(R.id.SignIn);
        Log.v("in review", "here2");
        dialogCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogLogin.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("User");
                EditText passwordView = (EditText) dialog.findViewById(R.id.passwordDialog);
                final String password = passwordView.getText().toString();
                EditText userNameView = (EditText) dialog.findViewById(R.id.usernameDialog);
                final String userName = userNameView.getText().toString();
                try {
                    query.whereEqualTo("user_name", userName);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> users, ParseException e) {
                            if (e == null) {
                                for (ParseObject u : users) {
                                    String passwordMatch = (String) u.get("password");
                                    if (passwordMatch.equals(password)) {
                                        LoginActivity.userNameSession = userName;
                                        Intent i = new Intent(getApplicationContext(), AddReview.class);
                                        startActivity(i);
                                    } else {
                                        CharSequence text = "Incorrect Password/UserName";
                                        int duration = Toast.LENGTH_SHORT;

                                        Toast toast = Toast.makeText(context, text, duration);
                                        toast.show();
                                    }

                                }
                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });

                } catch (Exception e){

                }
            }
        });

        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_review_page, menu);
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

    private ArrayList<ReviewItem> generateData() {
        ArrayList<ReviewItem> items = new ArrayList<ReviewItem>();
        ParseQuery<ParseObject> queryFoodTruck = new ParseQuery<ParseObject>("FoodTruck");
        queryFoodTruck.whereEqualTo("name", FoodTruckPage.foodTruckName);
        try {
            List<ParseObject> foodTrucks = queryFoodTruck.find();
            //String foodTruck = (String) foodTrucks.get(0).get("objectId");
           // String foodTruck = foodTrucks
            String foodTruck = foodTrucks.get(0).getObjectId();
            ParseQuery<ParseObject> queryReview = new ParseQuery<ParseObject>("Review");
            queryReview.whereEqualTo("foodtruck_id", foodTruck);
            List<ParseObject> reviews = queryReview.find();
            int numReviews = reviews.size();
            TextView reviewNumber = (TextView)findViewById(R.id.numReviews);
            reviewNumber.setText(numReviews+" reviews");
            Log.v("AR2", "hi");
            for(ParseObject r: reviews){
                Log.v("AR2", "hiya");
                String username = (String) r.get("user_name");
                String text = (String) r.get("text");
                //String reviewId = (String) r.get("objectId");
                String reviewId = r.getObjectId();
                ParseQuery<ParseObject> queryLikes = new ParseQuery<ParseObject>("ReviewLike");
                queryLikes.whereEqualTo("review_id", reviewId);
                int numLikes = queryLikes.find().size();
                Log.v("AR3 totLikes", numLikes + "");
                ReviewItem listItem = new ReviewItem(username, text, numLikes, reviewId);
                items.add(listItem);
            }
        } catch (Exception e) {


        }
        return items;
    }

    private ArrayList<ReviewItem> sorting (ArrayList<ReviewItem> reviewItems){
        int size = reviewItems.size();
        int currLikes = 0;
        int swapIndex = 0;
        for(int i=0; i< size-1; i++) {
            currLikes = reviewItems.get(i).likes;
            swapIndex = i;
            for (int j = i+1; j < size; j++) {
                int newLikes = reviewItems.get(j).likes;
                if(newLikes < currLikes){
                    currLikes = newLikes;
                    swapIndex = j;
                }
            }
            if(swapIndex != i){
                ReviewItem temp = reviewItems.get(swapIndex);
                reviewItems.set(i, reviewItems.get(swapIndex));
                reviewItems.set(swapIndex, temp);
            }
        }
        return reviewItems;
    }

    /*
    public void likeReview (View v){
        if(LoginActivity.userNameSession == null){
            Context context = getApplicationContext();
            CharSequence text = "Please log in";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else{
            String userName = LoginActivity.userNameSession;

            ParseQuery<ParseObject> queryFoodTruck = new ParseQuery<ParseObject>("Review");
            queryFoodTruck.whereEqualTo("user_name", LoginActivity.userNameSession);
            try {
                List<ParseObject> foodTrucks = queryFoodTruck.find();
                //String foodTruckID = (String) foodTrucks.get(0).get("objectId");
                String foodTruckID = foodTrucks.get(0).getObjectId();
                ParseQuery<ParseObject> queryUserName = new ParseQuery<ParseObject>("User");
                queryFoodTruck.whereEqualTo("user_name", LoginActivity.userNameSession);
                List<ParseObject> users = queryUserName.find();
               // String userID = (String) users.get(0).get("objectId");
                String userID =  users.get(0).getObjectId();

                Intent i = new Intent(getApplicationContext(), ReviewPage.class);
                startActivity(i);
            } catch (Exception e) {


            }
        }

    }*/


}
