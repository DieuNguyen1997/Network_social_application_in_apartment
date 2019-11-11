package com.example.networksocialapplication.models;

public class Manager extends User{
    private String mManagerId;
    private String mLocation;

    public Manager() {
    }

    public String getManagerId() {
        return mManagerId;
    }



    public void setManagerId(String managerId) {
        mManagerId = managerId;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }
}
