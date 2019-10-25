package com.example.networksocialapplication.models;

public class User {
    private String mUserID;
    private String mUsername;
    private String mDes;
    private String mAvatar;
    private String mPhoneNumber;
    private String mGender;
    private String mDateOfBirth;


    public User() {
    }

    public User(String userID, String username, String des) {
        mUserID = userID;
        mUsername = username;
        mDes = des;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String userID) {
        mUserID = userID;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getDes() {
        return mDes;
    }

    public void setDes(String des) {
        mDes = des;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public String getDateOfBirth() {
        return mDateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        mDateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "User{" +
                "mUserID='" + mUserID + '\'' +
                ", mUsername='" + mUsername + '\'' +
                ", mDes='" + mDes + '\'' +
                ", mAvatar='" + mAvatar + '\'' +
                ", mPhoneNumber='" + mPhoneNumber + '\'' +
                ", mGender='" + mGender + '\'' +
                ", mDateOfBirth='" + mDateOfBirth + '\'' +
                '}';
    }
}
