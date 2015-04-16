package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseObject;
import com.parse.*;

import java.util.*;

import android.util.*;
import android.widget.Toast;

import com.parse.ParseAnalytics;

// THIS IS ALL SAMPLE CODE. NOT ACTUALLY USED IN PROJECT.


public class ParseStarterProjectActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity main = new MainActivity();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);


//        This is some example code about how to add a user. Message Paarth or Nikila if confused.
//        ParseObject me = new ParseObject("Customer");
//        me.put("first_name", "rebecca");
//        me.put("last_name", "ballantyne");
//        me.put("password", "shouldbehashed");
//        me.put("phoneNum", "555-555-5555");
//        me.put("login", "rebecca");
//        me.saveInBackground();

//        This is some example code about how to query for a user. Message Paarth or Nikila if confused.
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
//        query.whereEqualTo("first_name", "rebecca");
//        query.findInBackground(new FindCallback<ParseObject>() {
//            public void done(List<ParseObject> user, ParseException e) {
//                if (e == null) {
//                    for (ParseObject u: user) {
//                        System.out.println(u.get("last_name"));
//                    }
//                } else {
//                    Log.d("score", "Error: " + e.getMessage());
//                }
//            }
//        });

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}
