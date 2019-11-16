package com.example.networksocialapplication.models;

public class PostParent {
    private String mPostId;
    private String mDatePosted;
    private String mTimePosted;
    private String mContentPost;
    private String mImagePost;

    public PostParent() {
    }

    public PostParent(String date, String time, String content) {
        mDatePosted = date;
        mTimePosted = time;
        mContentPost = content;
    }

    public String getPostPaId() {
        return mPostId;
    }

    public void setPostPaId(String postPaId) {
        mPostId = postPaId;
    }

    public String getDatePosted() {
        return mDatePosted;
    }

    public void setDatePosted(String datePosted) {
        mDatePosted = datePosted;
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
}
