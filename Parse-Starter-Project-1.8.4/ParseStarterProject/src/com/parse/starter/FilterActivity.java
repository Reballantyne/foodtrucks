package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

/**
 * The filter activity. Enables users to filter the food trucks based on certain preset criteria
 * @author Shilpa Kannan
 */

public class FilterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
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

    public void onFilterClicked(View view) {
        RadioGroup buttonID = (RadioGroup) findViewById(R.id.radio_group);
        //Get the option selected for filtering
        int checked = buttonID.getCheckedRadioButtonId();
        if (checked > -1) {//As long as something is checked
            //Filter & display the food trucks on the home page
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("filter", checked);
            startActivity(i);
        }
    }
}
