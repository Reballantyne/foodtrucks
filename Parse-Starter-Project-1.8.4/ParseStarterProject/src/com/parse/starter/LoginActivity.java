package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


/**
 * First Screen when opening the app
 * Creates Login Functionality
 *
 * @author Srinidhi Raghavan
 */

public class LoginActivity extends Activity {

    //The username used across the entire application
    static String userNameSession = null;

    //The first function called in the Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //adds the facebook button
        FacebookSdk.sdkInitialize(getApplicationContext());
        //sets the context
        setContentView(R.layout.activity_login);
        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
        //onclick listener for the register text field
        registerScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });
        TextView mainScreen = (TextView) findViewById(R.id.Without_Password);
        //onclick listener for the "continue without registering" text field
        mainScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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


    //Checks if login username and password is correct and does appropriate action
    public void loginUser(View v) {
        //Selects the User field and all necessary values
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("User");
        EditText passwordView = (EditText) findViewById(R.id.passwordField);
        final String password = passwordView.getText().toString();
        EditText userNameView = (EditText) findViewById(R.id.UserNameTF);
        final String userName = userNameView.getText().toString();
        try {
            //finds user associated value
            query.whereEqualTo("user_name", userName);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> users, ParseException e) {
                    if (e == null) {
                        for (ParseObject u : users) {
                            String passwordMatch = (String) u.get("password");
                            Log.v("hashedPassword:nonHashed1",passwordMatch );
                            BCrypt.checkpw(password,passwordMatch);
                            Log.v("hashedPassword","got here");

                            //if (passwordMatch.equals(password)) {
                            if (BCrypt.checkpw(password,passwordMatch)) {
                                //set the userName and go to Main screen
                                Log.v("hashedPassword","it matched");

                                userNameSession = userName;
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                            } else {
                                //display toast if incorrect
                                Context context = getApplicationContext();
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
}
