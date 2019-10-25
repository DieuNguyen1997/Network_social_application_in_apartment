package com.example.networksocialapplication.models;

import java.util.ArrayList;

public class Post {
    private String mPostId;
    private String mUserID;
    private String mAvatar;
    private String mUsername;
    private String mDatePosted;
    private String mTimePosted;
    private String mContentPost;
    private String mImagePost;
    private ArrayList<String> mListImagePost;


    public Post(String postId, String userID, String avatar, String username, String datePosted, String timePosted, String contentPost) {
        mPostId = postId;
        mUserID = userID;
        mAvatar = avatar;
        mUsername = username;
        mDatePosted = datePosted;
        mTimePosted = timePosted;
        mContentPost = contentPost;
    }

    public Post(String userID, String avatar, String username, String datePosted, String timePosted, String contentPost) {
        mUserID = userID;
        mAvatar = avatar;
        mUsername = username;
        mDatePosted = datePosted;
        mTimePosted = timePosted;
        mContentPost = contentPost;
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
        return mDatePosted;
    }

    public void setDatePosted(String datePosted) {
        mDatePosted = datePosted;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public ArrayList<String> getListImagePost() {
        return mListImagePost;
    }

    public void setListImagePost(ArrayList<String> listImagePost) {
        mListImagePost = listImagePost;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getTimePosted() {
        return mTimePosted;
    }

    public void setTimePosted(String timePosted) {
        mTimePosted = timePosted;
    }

    public String getContentPost() {
        return mContentPost;
    }

    public void setContentPost(String contentPost) {
        mContentPost = contentPost;
    }

    public String getImagePost() {
        return mImagePost;
    }

    public void setImagePost(String imagePost) {
        mImagePost = imagePost;
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
                ", mAvatar='" + mAvatar + '\'' +
                ", mUsername='" + mUsername + '\'' +
                ", mDatePosted='" + mDatePosted + '\'' +
                ", mTimePosted='" + mTimePosted + '\'' +
                ", mContentPost='" + mContentPost + '\'' +
                ", mImagePost='" + mImagePost + '\'' +
                '}';
    }
}
