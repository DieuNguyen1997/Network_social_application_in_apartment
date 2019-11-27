package com.example.networksocialapplication.models;

public class Notification {
    private String mNotifyID;
    private String mUserID;
    private String mText;
    private String mPostID;
    private boolean isPost;

    public Notification() {
    }

    public Notification(String userID, String text, String postID, boolean isPost) {
        mUserID = userID;
        mText = text;
        mPostID = postID;
        this.isPost = isPost;
    }

    public String getNotifyID() {
        return mNotifyID;
    }

    public void setNotifyID(String notifyID) {
        mNotifyID = notifyID;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String userID) {
        mUserID = userID;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getPostID() {
        return mPostID;
    }

    public void setPostID(String postID) {
        mPostID = postID;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }
}
