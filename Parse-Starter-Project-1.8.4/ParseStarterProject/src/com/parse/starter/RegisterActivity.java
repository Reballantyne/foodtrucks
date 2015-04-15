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


public class RegisterActivity extends Activity {
    String[] output = new String [4];

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


    public void createNewUser1(View v) {
        Log.v("RA0", "got here1");
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("User");
        EditText fullNameET = (EditText) findViewById(R.id.reg_fullname);
        output[0] = fullNameET.getText().toString();
        EditText emailET = (EditText) findViewById(R.id.reg_email);
        output[1] = emailET.getText().toString();
        EditText userIDET = (EditText) findViewById(R.id.reg_id);
        output[2] = userIDET.getText().toString();
        EditText passwordET = (EditText) findViewById(R.id.reg_password);
        output[3] = passwordET.getText().toString();
        ParseObject user = new ParseObject("User");
        Log.v("RA1", "got here1");
        user.put("first_name", output[0]);
        user.put("user_name", output[2]);
        user.put("password", output[3]);
        user.saveInBackground();
        Context context = getApplicationContext();
        CharSequence text = "Thanks for registering!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        Intent i = new Intent(this, MainActivity.class);
        Log.v("RA1", "got here2");
        startActivity(i);
        /*
        try {
            //query.whereEqualTo("login", "rebecca");
            query.whereEqualTo("login", userName);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> users, ParseException e) {
                    if (e == null) {
                        for (ParseObject u : users) {
                            String passwordMatch = (String) u.get("password");
                            if (passwordMatch.equals(password)) {
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                            } else {
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

        } catch (Exception e){

        }
        */
    }




}
