package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


/**
 * The registration screen for creating a new user
 *
 * @Srinidhi Raghavan
 */


public class RegisterActivity extends Activity {
    String[] output = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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


    //Creates a new User
    public void createNewUser1(View v) {
        //Get necessary information to update database
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("User");
        EditText fullNameET = (EditText) findViewById(R.id.reg_fullname);
        output[0] = fullNameET.getText().toString();
        EditText emailET = (EditText) findViewById(R.id.reg_email);
        output[1] = emailET.getText().toString();
        EditText userIDET = (EditText) findViewById(R.id.reg_id);
        output[2] = userIDET.getText().toString();
        EditText passwordET = (EditText) findViewById(R.id.reg_password);
        output[3] = passwordET.getText().toString();
        String hashed = BCrypt.hashpw(output[3], BCrypt.gensalt());
        ParseObject user = new ParseObject("User");
        //Add nesscesary values for the database
        user.put("first_name", output[0]);
        user.put("user_name", output[2]);
        user.put("password", hashed);
        //user.put("password", output[3]);
        user.saveInBackground();
        Context context = getApplicationContext();
        HelperFunctions.displayToast(context, "Thanks for registering");
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
