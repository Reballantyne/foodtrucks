package com.parse.starter;

/**
 * This class enables storing of a review
 * @author Shilpa Kannan, Srinidhi Raghavan
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
