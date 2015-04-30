package com.parse.starter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import android.view.Gravity;

/**
 * Created by Rebecca_2 on 3/29/2015.
 */
public class FoodTruckActivity extends Activity {
    private List<ParseObject> foodTrucks; //list of all food trucks
    static String foodTruckName; //name of food truck to load page with
    private static String telNumber; //telephone number of truck
    private static String website; //website of truck
    private static String hoursOpen; //hours the truck is open
    private static String openClosed; //whether or  not the truck is open now
    private static String address; //address of the truck
    private static String mainGenre; //genre of the truck
    private static boolean hasHealthy; //whether or not the truck has healthy options
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private ImageView img;
    private Bitmap uploadPicture = null;
    //Method: Rebecca
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_truck_page);

        //retrieve information about which food truck's information to load
        Intent intent = getIntent();
        foodTruckName = intent.getStringExtra("TRUCK_NAME");

        //get other information about the foodtruck from the database
        new GetFoodTruckInfo().execute();

        TextView reviewScreen = (TextView) findViewById(R.id.ReviewClick);
        ImageView picBox = (ImageView) findViewById(R.id.PictureBox);
        Bitmap screenPic = getFirstPicture();
        if (picBox != null) {
            picBox.setImageBitmap(screenPic);
        }
        reviewScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), ReviewPage.class);
                startActivity(i);
            }
        });
        Button uploadPicButton = (Button) findViewById(R.id.UploadPic);
        uploadPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
    }

    private Bitmap getFirstPicture() {

        try {
            ParseQuery<ParseObject> queryFoodTruck = new ParseQuery<ParseObject>("FoodTruck");
            queryFoodTruck.whereEqualTo("name", FoodTruckActivity.foodTruckName);
            List<ParseObject> foodTrucks = queryFoodTruck.find();
            ParseObject foodTruckO = foodTrucks.get(0);
            String foodTruckID = (String) foodTruckO.getObjectId();
            ParseQuery<ParseObject> queryPhotos = new ParseQuery<ParseObject>("Photo");
            queryPhotos.whereEqualTo("foodtruck_id", foodTruckID);
            List<ParseObject> dataPhotos = queryPhotos.find();
            for (ParseObject photoObject : dataPhotos) {
                ParseFile photo = (ParseFile) photoObject.get("photo_file");
                byte[] file = photo.getData();
                Bitmap image = BitmapFactory.decodeByteArray(file, 0, file.length);
                return image;
            }
        } catch (Exception e) {

        }
        return null;
    }

    //Method: Rebecca. Call a FoodTruck using the telephone number stored as a field.
    public void callTruck(View v) {
        if (telNumber == null) {
            CharSequence text = "Phone number not available";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(telNumber));
            EndCallListener callListener = new EndCallListener();
            TelephonyManager mTM = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            mTM.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
            startActivity(callIntent);
        }
    }

    //Method: Rebecca. Wait for the end of a phone call, or log relevant phone call errors.
    private class EndCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (TelephonyManager.CALL_STATE_RINGING == state) {
                Log.i("phone error", "RINGING, number: " + incomingNumber);
            }
            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                Log.i("phone error", "OFFHOOK");
            }
            if (TelephonyManager.CALL_STATE_IDLE == state) {
                Log.i("phone error", "IDLE");
            }
        }
    }

    //Class: Rebecca. This class retrieves all relevant food truck information from the database
    //and populates the FoodTruckPage with that information.
    private class GetFoodTruckInfo extends AsyncTask<Void, Void, Void> {

        //This method retrieves the relevant ParseObject for the FoodTruck specified
        //as part of the savedInstanceState Bundle
        protected Void doInBackground(Void... params) {
            //Get the current list of foodtrucks from parse
            final String[] result = new String[4];
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FoodTruck");
            query.whereEqualTo("name", foodTruckName);
            try {
                foodTrucks = query.find();
            } catch (ParseException e) {
            }
            return null;
        }

        // Rebecca: Method populates the page with relevant information.
        @Override
        protected void onPostExecute(Void v) {
            //pulls the data from the database
            for (ParseObject truck : foodTrucks) {
                address = (String) truck.get("address");

                telNumber = (String) truck.get("phoneNum");
                //telNumber = "tel:" + truck.get("phoneNum");

                //create an arraylist to hold all possible genres for the truck
                ArrayList<String> genreList = (ArrayList<String>) truck.get("categories");
                if (genreList != null) {
                    //pick the main(first) genre to display on the screen
                    mainGenre = genreList.get(0);
                }
                website = (String) truck.get("url");
                //get if it has healthy options
                hasHealthy = (Boolean) truck.get("hasHealthyOptions");
                //parse information about opening and closing times
                retrieveOpenClosedInformation(truck);
            }
            //displays all the information on the screen
            displayFoodTruckData();
        }

    }

    //Rebecca: Displays all the data for a foodtruck
    private void displayFoodTruckData() {
        //set the name of the truck
        TextView nameView = (TextView) findViewById(R.id.name);
        nameView.setText(foodTruckName);

        //set the address of the truck
        TextView addressView = (TextView) findViewById(R.id.address);
        addressView.setText(address);

        //set the genre of the truck
        TextView truckGenreView = (TextView) findViewById(R.id.genre);
        truckGenreView.setText(mainGenre);

        //set the times of the truck
        TextView truckTimesView = (TextView) findViewById(R.id.hoursToday);
        truckTimesView.setText("Hours Today: " + hoursOpen);

        //set the open closed tag and color it
        TextView oCView = (TextView) findViewById(R.id.oC);
        if (openClosed.equals("Open")) {
            oCView.setTextColor(Color.parseColor("#ff318e21"));
        }
        oCView.setText(openClosed);

        //set the healthy tag and color it
        TextView healthy = (TextView) findViewById(R.id.isHealthy);
        if (hasHealthy) {
            healthy.setText("Healthy");
            healthy.setTextColor(Color.parseColor("#ff318e21"));
        }
    }

    //Method: Rebecca - retrieves information about when a truck is open or closed
    private void retrieveOpenClosedInformation(ParseObject truck){
        //identify current day of week
        Calendar current = Calendar.getInstance();
        int dayOfWeek = current.get(Calendar.DAY_OF_WEEK) - 1;

        //identify opening and closing time of the truck
        ArrayList<Integer> open = (ArrayList<Integer>) truck.get("opening_times");
        ArrayList<Integer> close = (ArrayList<Integer>) truck.get("closing_times");

        if (open == null || close == null) {
            openClosed = "";
            hoursOpen = "Hours not available";
        } else {

            //identify the open time for the day of the week it is today
            int openTime = open.get(dayOfWeek);
            int closeTime = close.get(dayOfWeek);

            //format the string to display the hours to the screen
            formatOpenClosedText(openTime, closeTime);

            //check whether open now or not and set the open-closed tag
            SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
            String currentTime = sdf.format(current.getTime());
            int militaryTime = Integer.parseInt(currentTime);
            if (militaryTime < openTime || militaryTime > closeTime) {
                openClosed = "Closed";
            } else {
                openClosed = "Open";
            }
        }
    }

    //Method: Rebecca - parses a integer that contains the time into a properly formatted time
    //String that can be displayed on the screen in the following format: ("11:00")
    private void formatOpenClosedText(int openTime, int closeTime) {
        if (openTime == -1) {
            hoursOpen = "Closed today";
        } else {

            //change the int into a string so it is easier to manipulate
            String openString = "" + openTime;
            String closeString = "" + closeTime;

            //create substrings to re-attach with a : later
            String openEnd = openString.substring(openString.length() - 2, openString.length());
            String openBeg = openString.substring(0, openString.length() - 2);
            String closeEnd = closeString.substring(closeString.length() - 2, closeString.length());
            String closeBeg = closeString.substring(0, closeString.length() - 2);

            //compile the substrings together
            String startOpenString = openBeg + ":" + openEnd;
            String startCloseString = closeBeg + ":" + closeEnd;
            hoursOpen = startOpenString + " - " + startCloseString;
        }
    }

    public void photoClick(View v) {
        Intent i = new Intent(this, PictureSwiper.class);
        startActivity(i);
    }

    //Method: Shilpa & Srinidhi. Redirects to reviews if users want to see more reviews
    public void seeMoreReviews(View v) {
        Intent i = new Intent(this, ReviewPage.class);
        startActivity(i);
    }

    //Method: Rebecca. Redirects to the foodtruck's website.
    public void viewWebsite(View v) {
        Intent intent = new Intent("android.intent.action.VIEW",
                Uri.parse(website));
        startActivity(intent);
    }

    //Method: Rebecca. Main Menu botton. Redirects to the Search page.
    public void goSearch(View v) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
    }

    //Method: Rebecca. Main Menu button. Forwards to the Specials page.
    public void goSpecials(View v) {
        Intent i = new Intent(getApplicationContext(), SpecialsActivity.class);
        startActivity(i);
    }

    //Method: Nikila & Paarth : Main Menu button. Forwards to the Map Page.
    public void goMap(View v) {
        Intent i = new Intent(getApplicationContext(), NearbyActivity.class);
        startActivity(i);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == SELECT_PICTURE) {
                    Uri selectedImageUri = data.getData();
                    Log.v("imagePath", "got here");
                    Bitmap uploadPicture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("FoodTruck");
                    query.whereEqualTo("name", FoodTruckActivity.foodTruckName);
                    List<ParseObject> queryId = query.find();
                    String userID = queryId.get(0).getObjectId();
                    ParseObject newPhoto = new ParseObject("Photo");
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    uploadPicture.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                    byte[] dataPicture = byteStream.toByteArray();
                    ParseFile image= new ParseFile("image.png", dataPicture);
                    newPhoto.put("photo_file", image);
                    newPhoto.put("foodtruck_id", userID);
                    newPhoto.saveInBackground();

                }
            }
        } catch (Exception e){
            Log.v("imagePath", "Error in getting image");
            e.printStackTrace();

        }
    }

    /*
    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
    */



}

