package com.parse.starter;

import android.app.Activity;
import android.os.Bundle;
import com.parse.ParseObject;

import com.parse.ParseAnalytics;

public class ParseStarterProjectActivity extends Activity {
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

//        Example on how to create an object in parse
//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "cool beans");
//        testObject.saveInBackground();

		ParseAnalytics.trackAppOpenedInBackground(getIntent());
	}
}
