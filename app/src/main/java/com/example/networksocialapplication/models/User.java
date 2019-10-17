package com.example.networksocialapplication.models;

public class User {
    private String mUserID;
    private String mUserName;
    private String mDes;
    private String mAvatarUrl;
    private String mPhoneNumber;
    private String mGender;
    private String mDateOfBirth;
    private String mNameRoom;

    public User() {
    }

    public User(String userID, String userName, String des, String avatarUrl, String phoneNumber, String gender, String dateOfBirth) {
        mUserID = userID;
        mUserName = userName;
        mDes = des;
        mAvatarUrl = avatarUrl;
        mPhoneNumber = phoneNumber;
        mGender = gender;
        mDateOfBirth = dateOfBirth;
    }

    public User(String userName, String des) {
        mUserName = userName;
        mDes = des;
    }

    public User(String phoneNumber, String gender, String dateOfBirth) {
        mPhoneNumber = phoneNumber;
        mGender = gender;
        mDateOfBirth = dateOfBirth;
    }

    public User(String phoneNumber, String gender, String dateOfBirth, String nameRoom) {
        mPhoneNumber = phoneNumber;
        mGender = gender;
        mDateOfBirth = dateOfBirth;
        mNameRoom = nameRoom;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        mAvatarUrl = avatarUrl;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String userID) {
        mUserID = userID;
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

    public String getNameRoom() {
        return mNameRoom;
    }

    public void setNameRoom(String nameRoom) {
        mNameRoom = nameRoom;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getDes() {
        return mDes;
    }

    public void setDes(String des) {
        mDes = des;
    }
}
