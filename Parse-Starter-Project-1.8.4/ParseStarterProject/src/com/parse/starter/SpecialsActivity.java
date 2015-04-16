package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
    private ListView listOfSpecials;
    private List<ParseObject> allSpecialsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specials);
        new RemoteDataTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_specials, menu);
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

    //Method: Rebecca
    public void goSearch(View v) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    //Method: Nikila & Paarth
    public void goMap(View v) {
        Intent i = new Intent(getApplicationContext(), NearbyActivity.class);
        startActivity(i);
    }

    private class SpecialsAdapter extends ArrayAdapter<SpecialsItem> {
        Context context;
        int layoutResourceId;
        ArrayList<SpecialsItem> data;

        public SpecialsAdapter(Context context, int layoutResourceId, ArrayList<SpecialsItem> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            SpecialHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new SpecialHolder();
                holder.txtTitle = (TextView) row.findViewById(R.id.title);
                holder.txtDesc = (TextView) row.findViewById(R.id.desc);
                holder.txtName = (TextView) row.findViewById(R.id.truckName);
                holder.txtEnd = (TextView) row.findViewById(R.id.endDate);

                row.setTag(holder);
            } else {
                holder = (SpecialHolder) row.getTag();
            }

            SpecialsItem special = data.get(position);
            holder.txtTitle.setText(special.title);
            holder.txtDesc.setText(special.desc);
            holder.txtEnd.setText(special.endDate);
            holder.txtName.setText(special.truckName);

            return row;
        }

        private class SpecialHolder {
            TextView txtTitle;
            TextView txtDesc;
            TextView txtName;
            TextView txtEnd;
        }

    }


    //Method: Rebecca
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            //Get the current list of foodtrucks
            Log.v("Getting objects", "");
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Special");
            try {
                allSpecialsData = query.find();
            } catch (ParseException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Put the list of todos into the list viewer
            ListView foodList = (ListView) findViewById(R.id.listView);
            ArrayList<SpecialsItem> specialsData = new ArrayList();
            if (allSpecialsData != null) {
                for (ParseObject special : allSpecialsData) {
                    Date startDate = (Date) special.get("start_date");
                    Date endDate = (Date) special.get("end_date");
                    Date currentDate = new Date();
                    if (currentDate.after(startDate) && currentDate.before(endDate)) {
                        specialsData.add(new SpecialsItem((String) special.get("title"),
                                (String) special.get("desc"),
                                "Ends: " + special.get("date_end"),
                                "Location: " + special.get("truck_name")));
                    }
                }
            }
            SpecialsAdapter adapter = new SpecialsAdapter(SpecialsActivity.this, R.layout.specials_row, specialsData);
            listOfSpecials = (ListView) findViewById(R.id.listOfSpecials);
            listOfSpecials.setAdapter(adapter);
        }
    }

}
