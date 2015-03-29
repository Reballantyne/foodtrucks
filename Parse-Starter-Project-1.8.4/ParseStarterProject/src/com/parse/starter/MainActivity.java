package com.parse.starter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class MainActivity extends Activity {
    private List<ParseObject> foodTrucks;

    @TargetApi(11)
    @Override
    //Method: Rebecca
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Search for a food truck.");
        new RemoteDataTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    //displays the on screen keyboard when a user clicks on the searchView
    public void searchClick(){
        SearchView search = (SearchView) findViewById(R.id.searchView);
        search.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
    }

    //Method: Rebecca
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            //Get the current list of foodtrucks
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("FoodTruck");
            query.orderByDescending("name");
            try {
                foodTrucks = query.find();
            } catch (ParseException e) { }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Put the list of todos into the list viewer
            ListView foodList = (ListView) findViewById(R.id.listView);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
            if (foodTrucks != null) {
                for (ParseObject truck : foodTrucks) {
                    adapter.add((String) truck.get("name"));
                }
            }
            foodList.setAdapter(adapter);
        }
    }

}
