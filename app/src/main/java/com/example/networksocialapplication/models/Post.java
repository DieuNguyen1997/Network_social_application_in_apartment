package com.example.networksocialapplication.models;

import java.util.ArrayList;

public class Post extends PostParent {
    private String mPostId;
    private String mUserID;

//    private ArrayList<String> mListImagePost;



    public Post(String postId, String userID, String date, String time, String content) {
        super(date, time, content);
        mPostId = postId;
        mUserID = userID;

    }

    public Post(String userID,String date, String time, String content) {
        super(date, time, content);
        mUserID = userID;

    }

    public Post() {
    }


    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String userID) {
        mUserID = userID;
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
                '}';
    }
}
