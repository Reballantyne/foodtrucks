package com.parse.starter;

import android.content.Context;
import android.widget.Toast;

/**
 * Helper Functions used throughout the program
 */
public class HelperFunctions {

    public static void displayToast(Context context, String message){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

}
