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
import java.util.List;


public class MenuActivity extends Activity {
    private String foodTruckName;
    private ListView menuListView;
    private List<ParseObject> menuParseObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //retrieve information about which food truck's information to load
        Intent i = getIntent();
        foodTruckName = i.getStringExtra("TRUCK_NAME");
        Log.v("menu", "got truck name" + foodTruckName);
        //start pulling from database
        new RemoteDataTask().execute();
    }

    //Private adapter class that allows us to create a custom UI element for each element in the
    //ListView.
    private class MenuAdapter extends ArrayAdapter<MenuItem> {
        Context context;
        int layoutResourceId;
        ArrayList<MenuItem> menuData; //all the information about the specials

        public MenuAdapter(Context context, int layoutResourceId, ArrayList<MenuItem> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.menuData = data;
        }

        //Identifies which row to upload information to
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            MenuHolder holder;

            if (row == null) {
                //inflate the row
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                //set the views within the holder
                holder = new MenuHolder();
                holder.txtItem = (TextView) row.findViewById(R.id.item);
                holder.txtDesc = (TextView) row.findViewById(R.id.desc);
                holder.txtHealthy = (TextView) row.findViewById(R.id.h);
                holder.txtCategory = (TextView) row.findViewById(R.id.category);
                row.setTag(holder);

            } else {
                holder = (MenuHolder) row.getTag();
            }

            //set the text
            MenuItem menu = menuData.get(position);
            if (menu.item == null && menu.desc == null && menu.healthy == null) {
                holder.txtCategory.setText(menu.category);
                holder.txtItem.setText("");
                holder.txtDesc.setText("");
                holder.txtHealthy.setText("");
            }
            else {
                holder.txtItem.setText(menu.item);
                holder.txtDesc.setText(menu.desc);
                holder.txtHealthy.setText(menu.healthy);
                holder.txtCategory.setText("");

            }

            return row;
        }

        //Private class to hold each textView within the custom UI element
        private class MenuHolder {
            TextView txtItem;
            TextView txtDesc;
            TextView txtHealthy;
            TextView txtCategory;
        }
    }

    //Method: Rebecca : Thread that pulls data from the database and populates both the
    //specialsParseObjects and provides the arguments to the SpecialsAdapter
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            //Get the current list of foodtrucks
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Menu");
            query.whereEqualTo("truck_name", foodTruckName);
            try {
                menuParseObjects = query.find();
            } catch (ParseException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Put the list of todos into the list viewer
            ArrayList<MenuItem> menuData = new ArrayList();
            Log.v("menu", "" + menuParseObjects);

            //if there are specials available
            if (MenuActivity.this.menuParseObjects != null) {
                for (ParseObject menuItem : MenuActivity.this.menuParseObjects) {
                    ArrayList<Integer> healthy = (ArrayList<Integer>) menuItem.get("healthy");
                    ArrayList<String> item_names = (ArrayList<String>) menuItem.get("item_name");
                    ArrayList<String> item_description = (ArrayList<String>) menuItem.get("item_description");
                    String categoryName = (String) menuItem.get("category_name");
                    menuData.add(new MenuItem(null, null, null, categoryName));
                    for (int i = 0; i < healthy.size(); i++) {
                        String isHealthy = "";
                        if (healthy.get(i) == 1) { isHealthy = "H  "; }
                        menuData.add((new MenuItem(item_names.get(i), item_description.get(i), isHealthy, "")));
                    }
                }
            }
            //set the adapter for the view
            MenuAdapter adapter = new MenuAdapter(MenuActivity.this, R.layout.menu_row, menuData);
            menuListView = (ListView) findViewById(R.id.listOfMenu);
            menuListView.setAdapter(adapter);
        }
    }

    private class MenuItem {
        String category;
        String item;
        String desc;
        String healthy;

        //constructor
        private MenuItem(String item, String desc, String healthy, String category) {
            this.item = item;
            this.desc = desc;
            this.healthy = healthy;
            this.category = category;
        }
    }
}


