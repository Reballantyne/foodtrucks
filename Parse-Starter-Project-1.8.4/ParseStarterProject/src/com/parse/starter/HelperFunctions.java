package com.parse.starter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Helper Functions used throughout the program
 */
public class HelperFunctions {

    public static void displayToast(Context context, String message) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }


    /*
    public static Boolean displayDialog (String userNameOld, String passwordOld, Context context, ParseQuery<ParseObject> query){

        final String password = passwordOld;
        final String userName = userNameOld;
        final boolean correctMatch = false;
        try {
            query.whereEqualTo("user_name", userName);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> users, ParseException e) {
                    //Checker the user information is correct
                    if (e == null) {
                        for (ParseObject u : users) {
                            String passwordMatch = (String) u.get("password");
                            if (passwordMatch.equals(password)) {
                                LoginActivity.userNameSession = userName;
                                correctMatch = true;
                            } else {
                                HelperFunctions.displayToast(context,"Incorrect Password/UserName");

                            }

                        }
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }
            });
    }

    */

}
