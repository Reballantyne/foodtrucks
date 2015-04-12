package com.parse.starter;

/**
 * Created by raginiraghavan on 4/12/15.
 */
public class ReviewItem {
    public String user_name;
    public String review;
    public int likes;


    public ReviewItem(String user_name, String review, int likes){
        this.user_name = user_name;
        this.review = review;
        this.likes = likes;
    }



}
