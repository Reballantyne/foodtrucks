package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that creates the Picture Swiping View
 *
 * @author Srinidhi Raghavan
 */


public class PictureSwiper extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_swiper);
        //Custom View
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter();
        //Sets the custom View
        viewPager.setAdapter(adapter);
    }


    //Custom PageAdapter to swipe photos and switch screens
    private class ImagePagerAdapter extends PagerAdapter {

        //Array of Images from parse
        private ArrayList<Bitmap> mImages = new ArrayList<Bitmap>();

        public ImagePagerAdapter() {
            try {
                //gets needed objects from parse
                ParseQuery<ParseObject> queryFoodTruck = new ParseQuery<ParseObject>("FoodTruck");
                queryFoodTruck.whereEqualTo("name", FoodTruckActivity.foodTruckName);
                List<ParseObject> foodTrucks = queryFoodTruck.find();
                ParseObject foodTruckO = foodTrucks.get(0);
                String foodTruckID = (String) foodTruckO.getObjectId();
                ParseQuery<ParseObject> queryPhotos = new ParseQuery<ParseObject>("Photo");
                queryPhotos.whereEqualTo("foodtruck_id", foodTruckID);
                List<ParseObject> dataPhotos = queryPhotos.find();
                //gets photos from parse and decodes them
                for (ParseObject photoObject : dataPhotos) {
                    ParseFile photo = (ParseFile) photoObject.get("photo_file");
                    byte[] file = photo.getData();
                    Bitmap image = BitmapFactory.decodeByteArray(file, 0, file.length);
                    mImages.add(image);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        //gets the count
        @Override
        public int getCount() {
            return mImages.size();
        }

        //gets current object
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        //gets the image
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = PictureSwiper.this;
            ImageView imageView = new ImageView(context);
            //Adds padding
            int padding = context.getResources().getDimensionPixelSize(
                    R.dimen.activity_vertical_margin);
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setImageBitmap(mImages.get(position));
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        //removes certain object
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }
}
