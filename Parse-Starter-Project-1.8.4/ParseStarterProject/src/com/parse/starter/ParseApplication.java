package com.parse.starter;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseUser;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Crash Reporting.
        ParseCrashReporting.enable(this);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this, "O55K2qUiFUhSJ1SfVOW3KQDQt8p5eGuORNFoQJHp", "4aY7YBIFKkouGEpZKzPWAFdfR43nzdn3w6qtVhZD");


        //ParseUser.enableAutomaticUser(); normally not commented out
        //ParseACL defaultACL = new ParseACL(); normally not commented out
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        //ParseACL.setDefaultACL(defaultACL, true); normally not commented out
    }
}
