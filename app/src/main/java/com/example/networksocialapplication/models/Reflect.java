package com.example.networksocialapplication.models;

public class Reflect extends PostParent{
    private String mUserID;
    private String mTitle;
    private String mField;
    private String mReflectId;


    public Reflect() {
    }

    public Reflect(String userID, String date, String time, String content, String title, String field, String reflectId) {
        super(date, time, content);
        mUserID = userID;
        mTitle = title;
        mField = field;
        mReflectId = reflectId;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String userID) {
        mUserID = userID;
    }

    public String getReflectId() {
        return mReflectId;
    }

    public void setReflectId(String reflectId) {
        mReflectId = reflectId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getField() {
        return mField;
    }

    public void setField(String field) {
        mField = field;
    }
}
