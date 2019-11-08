package com.example.networksocialapplication.models;

public class Manager extends User{
    private String mHotline;
    private String mLocation;

    public String getHotline() {
        return mHotline;
    }

    public void setHotline(String hotline) {
        mHotline = hotline;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }
}
