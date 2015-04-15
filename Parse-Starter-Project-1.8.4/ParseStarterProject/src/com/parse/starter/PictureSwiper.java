package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PictureSwiper extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_swiper);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewPager.setAdapter(adapter);
    }

    private class ImagePagerAdapter extends PagerAdapter {

        /*
        private int[] mImages = new int[] {
                R.drawable.veg1,
                R.drawable.veg2,
                R.drawable.veg3
        };
        */

        private ArrayList<Bitmap> mImages = new ArrayList<Bitmap>();
        public ImagePagerAdapter() {

            try {
                ParseQuery<ParseObject> queryFoodTruck = new ParseQuery<ParseObject>("FoodTruck");
                queryFoodTruck.whereEqualTo("name", FoodTruckPage.foodTruckName);
                List<ParseObject> foodTrucks = queryFoodTruck.find();
                ParseObject foodTruckO = foodTrucks.get(0);
                String foodTruckID = (String) foodTruckO.getObjectId();
                ParseQuery<ParseObject> queryPhotos = new ParseQuery<ParseObject>("Photo");
                queryPhotos.whereEqualTo("foodtruck_id", foodTruckID);
                List<ParseObject> dataPhotos = queryPhotos.find();
                for (ParseObject photoObject : dataPhotos){
                    ParseFile photo = (ParseFile) photoObject.get("photo_file");
                    byte[] file  = photo.getData();
                    Bitmap image = BitmapFactory.decodeByteArray(file,0,file.length);
                    mImages.add(image);

                }
            } catch(Exception e){
                e.printStackTrace();
            }



        }

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = PictureSwiper.this;
            ImageView imageView = new ImageView(context);
            int padding = context.getResources().getDimensionPixelSize(
                    R.dimen.activity_vertical_margin);
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageBitmap(mImages.get(position));
            //imageView.setImageResource(mImages.get(position));
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }
}
