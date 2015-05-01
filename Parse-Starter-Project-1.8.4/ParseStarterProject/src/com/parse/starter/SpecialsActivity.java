package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

}
