package com.parse.starter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SpecialsActivity extends Activity {
    private ListView specialsListView;
    private List<ParseObject> specialsParseObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specials);
        //pull information from the database and populate the specials list view
        new RemoteDataTask().execute();
    }

    //Private adapter class that allows us to create a custom UI element for each element in the
    //ListView.
    private class SpecialsAdapter extends ArrayAdapter<SpecialsItem> {
        Context context;
        int layoutResourceId;
        ArrayList<SpecialsItem> specialsData; //all the information about the specials

        public SpecialsAdapter(Context context, int layoutResourceId, ArrayList<SpecialsItem> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.specialsData = data;
        }

        //Identifies which row to upload information to
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            SpecialHolder holder = null;

            if (row == null) {
                //inflate the row
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                //set the views within the holder
                holder = new SpecialHolder();
                holder.txtTitle = (TextView) row.findViewById(R.id.title);
                holder.txtDesc = (TextView) row.findViewById(R.id.desc);
                holder.txtName = (TextView) row.findViewById(R.id.truckName);
                holder.txtEnd = (TextView) row.findViewById(R.id.endDate);
                row.setTag(holder);

            } else {
                holder = (SpecialHolder) row.getTag();
            }

            //set the text
            SpecialsItem special = specialsData.get(position);
            holder.txtTitle.setText(special.title);
            holder.txtDesc.setText(special.desc);
            holder.txtEnd.setText(special.endDate);
            holder.txtName.setText(special.truckName);

            return row;
        }

        //Private class to hold each textView within the custom UI element
        private class SpecialHolder {
            TextView txtTitle;
            TextView txtDesc;
            TextView txtName;
            TextView txtEnd;
        }

    }

    //Method: Rebecca : Thread that pulls data from the database and populates both the
    //specialsParseObjects and provides the arguments to the SpecialsAdapter
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            //Get the current list of foodtrucks
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Special");
            try {
                specialsParseObjects = query.find();
            } catch (ParseException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Put the list of todos into the list viewer
            ArrayList<SpecialsItem> specialsData = new ArrayList();

            //if there are specials available
            if (SpecialsActivity.this.specialsParseObjects != null) {
                for (ParseObject special : SpecialsActivity.this.specialsParseObjects) {

                    //calculate if date is in bounds
                    Date startDate = (Date) special.get("start_date");
                    Date endDate = (Date) special.get("end_date");
                    Date currentDate = new Date();
                    if (currentDate.after(startDate) && currentDate.before(endDate)) {
                        //add the special if the date is in bounds
                        specialsData.add(new SpecialsItem((String) special.get("title"),
                                (String) special.get("desc"),
                                "Ends: " + special.get("date_end"),
                                "Location: " + special.get("truck_name")));
                    }
                }
            }
            //set the adapter for the view
            SpecialsAdapter adapter = new SpecialsAdapter(SpecialsActivity.this, R.layout.specials_row, specialsData);
            specialsListView = (ListView) findViewById(R.id.listOfSpecials);
            specialsListView.setAdapter(adapter);
        }
    }

    //Method: Rebecca : Main Menu Button. Forwards to the Search Page.
    public void goSearch(View v) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    //Method: Nikila & Paarth : Main Menu button. Forwards to the Map Page.
    public void goMap(View v) {
        Intent i = new Intent(getApplicationContext(), NearbyActivity.class);
        startActivity(i);
    }
    
      public void goFavorites(View v){
        if(LoginActivity.userNameSession != null) {
            Intent i = new Intent(getApplicationContext(), FavoritesActivity.class);
            startActivity(i);
        }
        helperDialogLogIn();
    }

    //This is a helper method that contains the dialog functionality to
    //prompt and check a user's password if they are not already logged in
    private void helperDialogLogIn() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_login);
        dialog.setTitle("Please log in");

        Button dialogCancel = (Button) dialog.findViewById(R.id.cancel);
        Button dialogLogin = (Button) dialog.findViewById(R.id.SignIn);
        //Cancel makes the dialog go away
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //Log in checks the user name and password
        dialogLogin.setOnClickListener(new View.OnClickListener() {
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
                                    if (BCrypt.checkpw(password, passwordMatch)){
                                        LoginActivity.userNameSession = userName;
                                        Intent i = new Intent(getApplicationContext(), FavoritesActivity.class);
                                        startActivity(i);
                                        break;
                                    } else {
                                        //Otherwise show a toast that the log in is incorrect
                                        CharSequence text = "Incorrect Password/UserName";
                                        int duration = Toast.LENGTH_SHORT;

                                        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
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
}
