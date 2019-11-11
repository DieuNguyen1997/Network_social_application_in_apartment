package com.example.networksocialapplication.models;

import java.util.ArrayList;

public class Post {
    private String mPostId;
    private String mUserID;
//    private String mAvatar;
//    private String mUsername;
    private String mDate;
    private String mTime;
    private String mContent;
    private String mImage;
    private ArrayList<String> mListImagePost;


    public Post(String postId, String userID, String datePosted, String timePosted, String contentPost) {
        mPostId = postId;
        mUserID = userID;
//        mAvatar = avatar;
//        mUsername = username;
        mDate = datePosted;
        mTime = timePosted;
        mContent = contentPost;
    }

    public Post(String userID,String datePosted, String timePosted, String contentPost) {
        mUserID = userID;
//        mAvatar = avatar;
//        mUsername = username;
        mDate = datePosted;
        mTime = timePosted;
        mContent = contentPost;
    }

    public Post() {
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String userID) {
        mUserID = userID;
    }

    public String getDatePosted() {
        return mDate;
    }

    public void setDatePosted(String datePosted) {
        mDate= datePosted;
    }

//    public String getAvatar() {
//        return mAvatar;
//    }
//
//    public void setAvatar(String avatar) {
//        mAvatar = avatar;
//    }

    public ArrayList<String> getListImagePost() {
        return mListImagePost;
    }

    public void setListImagePost(ArrayList<String> listImagePost) {
        mListImagePost = listImagePost;
    }

//    public String getUsername() {
//        return mUsername;
//    }
//
//    public void setUsername(String username) {
//        mUsername = username;
//    }

    public String getTimePosted() {
        return mTime;
    }

    public void setTimePosted(String timePosted) {
        mTime = timePosted;
    }

    public String getContentPost() {
        return mContent;
    }

    public void setContentPost(String contentPost) {
        mContent = contentPost;
    }

    public String getImagePost() {
        return mImage;
    }

    public void setImagePost(String imagePost) {
        mImage = imagePost;
    }

    public String getPostId() {
        return mPostId;
    }

    public void setPostId(String postId) {
        mPostId = postId;
    }

    @Override
    public String toString() {
        return "Post{" +
                "mUserID='" + mUserID + '\'' +
//                ", mAvatar='" + mAvatar + '\'' +
//                ", mUsername='" + mUsername + '\'' +
                ", mDatePosted='" + mDate + '\'' +
                ", mTimePosted='" + mTime+ '\'' +
                ", mContentPost='" + mContent + '\'' +
                ", mImagePost='" + mImage+ '\'' +
                '}';
    }
}
