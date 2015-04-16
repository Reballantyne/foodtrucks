package com.parse.starter;

/**
 * Created by raginiraghavan on 4/12/15.
 */
public class ReviewItem {
    public String user_name;
    public String review;
    public int likes;
    public String reviewID;


    public ReviewItem(String user_name, String review, int likes, String reviewId) {
        this.user_name = user_name;
        this.review = review;
        this.likes = likes;
        this.reviewID = reviewId;
    }


}
