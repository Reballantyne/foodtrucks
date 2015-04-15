package com.parse.starter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by raginiraghavan on 4/12/15.
 */
public class ReviewAdapter extends ArrayAdapter<ReviewItem> {

    private final Context context;
    private  final ArrayList<ReviewItem> reviewItemsList;
    public ReviewAdapter(Context context, ArrayList<ReviewItem> reviewItemList){
        super(context, R.layout.review_row, reviewItemList);
        this.context = context;
        this.reviewItemsList = reviewItemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.review_row, parent, false);

        // 3. Get the two text view from the rowView
        TextView userNameView = (TextView) rowView.findViewById(R.id.User_Name);
        TextView likesView = (TextView) rowView.findViewById(R.id.Likes);
        TextView review = (TextView) rowView.findViewById(R.id.reviews);

                // 4. Set the text for textView
        userNameView.setText(reviewItemsList.get(position).user_name);
        likesView.setText(reviewItemsList.get(position).likes);
        review.setText(reviewItemsList.get(position).review);

        // 5. retrn rowView
        return rowView;
    }
}
