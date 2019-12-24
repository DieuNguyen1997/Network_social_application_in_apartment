package com.example.networksocialapplication.models;

public class Apartment {
    private String mApartmentId;
    private String mName;
    private String mAddress;
    private String mNumberRoom;

    public Apartment(String apartmentId, String name, String address, String numberRoom) {
        mApartmentId = apartmentId;
        mName = name;
        mAddress = address;
        mNumberRoom = numberRoom;
    }

    public String getApartmentId() {
        return mApartmentId;
    }

    public void setApartmentId(String apartmentId) {
        mApartmentId = apartmentId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getNumberRoom() {
        return mNumberRoom;
    }

    public void setNumberRoom(String numberRoom) {
        mNumberRoom = numberRoom;
    }
}
