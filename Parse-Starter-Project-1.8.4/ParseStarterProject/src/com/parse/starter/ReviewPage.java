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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * The ReviewPage Activity creates a page to display the reviews
 * @author Shilpa Kannan, Srinidhi Raghavan
 */
public class ReviewPage extends Activity {
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_page);
        //Pull all the reviews and pass it to the review adapter to create a list
        ReviewAdapter adapter = new ReviewAdapter(this, generateData());
        TextView foodTruck = (TextView) findViewById(R.id.foodTruck);
        //Display the food truck name
        foodTruck.setText(FoodTruckActivity.foodTruckName);
        ListView listView = (ListView) findViewById(R.id.listReview);
        listView.setAdapter(adapter);
        context = this;
        Button buttonCreateReview = (Button) findViewById(R.id.wrButton);
        buttonCreateReview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //If user is already logged in
                if (LoginActivity.userNameSession != null) {
                    Intent i = new Intent(context, AddReview.class);
                    startActivity(i);
                }
                //otherwise prompt them
                helperDialogLogIn();
            }
        });
    }

    //This is a helper method that contains the dialog functionality to
    //prompt and check a user's password if they are not already logged in
    private void helperDialogLogIn() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_login);
        dialog.setTitle("Please log in");

        Button dialogCancel = (Button) dialog.findViewById(R.id.cancel);
        Button dialogLogin = (Button) dialog.findViewById(R.id.SignIn);
        //Cancel makes the dialog go away
        dialogCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //Log in checks the user name and password
        dialogLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("User");
                EditText passwordView = (EditText) dialog.findViewById(R.id.passwordDialog);
                final String password = passwordView.getText().toString();
                EditText userNameView = (EditText) dialog.findViewById(R.id.usernameDialog);
                final String userName = userNameView.getText().toString();
                try {
                    query.whereEqualTo("user_name", userName);
                    //Check the users with the same user name
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> users, ParseException e) {
                            if (e == null) {
                                for (ParseObject u : users) {
                                    String passwordMatch = (String) u.get("password");
                                    //If the password is right, go to the add a review
                                    //if (passwordMatch.equals(password)) {
                                    if (BCrypt.checkpw(password,passwordMatch)){
                                        LoginActivity.userNameSession = userName;
                                        Intent i = new Intent(getApplicationContext(), AddReview.class);
                                        startActivity(i);
                                    } else {
                                        //Otherwise show a toast that the log in is incorrect
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

                } catch (Exception e) {

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

    //This method pulls all the relevant reviews for a food truck in sorted
    //descending order by number of reviews
    private ArrayList<ReviewItem> generateData() {
        ArrayList<ReviewItem> items = new ArrayList<ReviewItem>();
        ParseQuery<ParseObject> queryFoodTruck = new ParseQuery<ParseObject>("FoodTruck");
        //Pull the food truck information
        queryFoodTruck.whereEqualTo("name", FoodTruckActivity.foodTruckName);
        try {
            List<ParseObject> foodTrucks = queryFoodTruck.find();
            String foodTruck = foodTrucks.get(0).getObjectId();
            ParseQuery<ParseObject> queryReview = new ParseQuery<ParseObject>("Review");
            //Use the food truck ID to find all reviews
            queryReview.whereEqualTo("foodtruck_id", foodTruck);
            List<ParseObject> reviews = queryReview.find();
            int numReviews = reviews.size();
            TextView reviewNumber = (TextView) findViewById(R.id.numReviews);
            //Display the number of reviews
            reviewNumber.setText(numReviews + " reviews");
            //Create a ReviewItem for each review
            for (ParseObject r : reviews) {
                String username = (String) r.get("user_name");
                String text = (String) r.get("text");
                String reviewId = r.getObjectId();
                ParseQuery<ParseObject> queryLikes = new ParseQuery<ParseObject>("ReviewLike");
                queryLikes.whereEqualTo("review_id", reviewId);
                int numLikes = queryLikes.find().size();
                ReviewItem listItem = new ReviewItem(username, text, numLikes, reviewId);
                items.add(listItem);
            }
        } catch (Exception e) {


        }
        //Sort!
        return sorting(items);
    }

    //This method sorts the food truck reviews in descending order of likes
    private ArrayList<ReviewItem> sorting(ArrayList<ReviewItem> reviewItems) {
        int size = reviewItems.size();
        int currLikes = 0;
        int swapIndex = 0;
        for (int i = 0; i < size - 1; i++) {
            currLikes = reviewItems.get(i).likes;
            swapIndex = i;
            for (int j = i + 1; j < size; j++) {
                int newLikes = reviewItems.get(j).likes;
                //If a review has more likes further down the list
                if (newLikes > currLikes) {
                    currLikes = newLikes;
                    swapIndex = j;
                }
            }
            if (swapIndex != i) {//If it is out of order, swap!
                ReviewItem temp = reviewItems.get(i);
                reviewItems.set(i, reviewItems.get(swapIndex));
                reviewItems.set(swapIndex, temp);
            }
        }
       // Collections.reverse(reviewItems);
        return reviewItems;
    }


}
