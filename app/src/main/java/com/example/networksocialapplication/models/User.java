package com.example.networksocialapplication.models;

public class User {
    private String mUserID;
    private String mUsername;
    private String mDes;
    private String mAvatar;
    private String mCoverPhoto;
    private String mPhoneNumber;

    public User() {
    }



    public User(String username, String avatar) {
        mUsername = username;
        mAvatar = avatar;
    }

    public User(String username, String des, String avatar, String coverPhoto) {
        mUsername = username;
        mDes = des;
        mAvatar = avatar;
        mCoverPhoto = coverPhoto;
    }

    public User(String userID, String username, String des) {
        mUserID = userID;
        mUsername = username;
        mDes = des;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getCoverPhoto() {
        return mCoverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        mCoverPhoto = coverPhoto;
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

    @Override
    public String toString() {
        return "User{" +
                "mUserID='" + mUserID + '\'' +
                ", mUsername='" + mUsername + '\'' +
                ", mDes='" + mDes + '\'' +
                ", mAvatar='" + mAvatar + '\'' +
                '}';
    }
}
