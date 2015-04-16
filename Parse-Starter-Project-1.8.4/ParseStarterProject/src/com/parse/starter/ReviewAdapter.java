package com.parse.starter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by raginiraghavan on 4/12/15.
 */
public class ReviewAdapter extends ArrayAdapter<ReviewItem> {

    private final Context context;
    private final ArrayList<ReviewItem> reviewItemsList;

    public ReviewAdapter(Context context, ArrayList<ReviewItem> reviewItemList) {
        super(context, R.layout.review_row, reviewItemList);
        this.context = context;
        this.reviewItemsList = reviewItemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Create inflater
        final int pos = position;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.review_row, parent, false);

        // 3. Get the two text view from the rowView
        TextView userNameView = (TextView) rowView.findViewById(R.id.User_Name);
        TextView likesView = (TextView) rowView.findViewById(R.id.Likes);
        TextView review = (TextView) rowView.findViewById(R.id.reviews);
        TextView reviewID = (TextView) rowView.findViewById(R.id.reviewID);

        // 4. Set the text for textView
        Log.v("RA1 Likes:", reviewItemsList.get(position).likes + "");
        Log.v("RA1", reviewItemsList.get(position).user_name);
        userNameView.setText(reviewItemsList.get(position).user_name);
        likesView.setText(reviewItemsList.get(position).likes + "");
        review.setText(reviewItemsList.get(position).review);
        reviewID.setText(reviewItemsList.get(position).reviewID);

        ImageView likeThumb = (ImageView) rowView.findViewById(R.id.likeThumb);
        likeThumb.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {
                    if (LoginActivity.userNameSession != null) {
                        helperLike(pos);
                    } else {
                        helperDialogLogIn(pos);
                    }
                } catch (Exception e) {

                }
            }
        });


        // 5. return rowView
        return rowView;
    }

    private void helperLike(int pos) {
        try {

            String revId = reviewItemsList.get(pos).reviewID;
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("ReviewLike");
            Log.v("RAD1: review id", revId);
            query.whereEqualTo("review_id", revId);
            List<ParseObject> queryReview = query.find();
            boolean beenReview = false;
            for (ParseObject r : queryReview) {
                if (r.get("user_name").equals(LoginActivity.userNameSession)) {
                    beenReview = true;
                }
            }
            if (!beenReview) {

                ParseQuery<ParseObject> queryUserName = new ParseQuery<ParseObject>("User");
                queryUserName.whereEqualTo("user_name", LoginActivity.userNameSession);
                List<ParseObject> queryId = queryUserName.find();
                String userID = queryId.get(0).getObjectId();
                Calendar current = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("MMM DD, yyyy, HH:mm");
                String dateNow = sdf.format(current.getTime());
                Date currentTime = sdf.parse(dateNow);
                ParseObject newReviewLike = new ParseObject("ReviewLike");

                newReviewLike.put("user_name", LoginActivity.userNameSession);
                newReviewLike.put("review_id", revId);
                newReviewLike.put("user_id", userID);
                newReviewLike.saveInBackground();
                reviewItemsList.get(pos).likes = reviewItemsList.get(pos).likes + 1;
                notifyDataSetChanged();


            } else {
                Context context = getContext();
                CharSequence text = "Already Liked!!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void helperDialogLogIn(int pos) {
        final int position = pos;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_login);
        dialog.setTitle("Please log in");

        Button dialogCancel = (Button) dialog.findViewById(R.id.cancel);
        Button dialogLogin = (Button) dialog.findViewById(R.id.SignIn);
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> users, ParseException e) {
                            if (e == null) {
                                for (ParseObject u : users) {
                                    String passwordMatch = (String) u.get("password");
                                    if (passwordMatch.equals(password)) {
                                        LoginActivity.userNameSession = userName;
                                        dialog.dismiss();
                                        helperLike(position);
                                    } else {
                                        CharSequence text = "Incorrect Password/UserName";
                                        int duration = Toast.LENGTH_SHORT;

                                        Toast toast = Toast.makeText(context, text, duration);
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
