package com.example.networksocialapplication.models;

public class Room {
    private String mRoomId;
    private String mApartmentId;
    private String mName;

    public Room(String apartmentId, String name) {
        mApartmentId = apartmentId;
        mName = name;
    }


    public Room(String name) {
        mName = name;
    }

    public Room() {
    }

    public String getApartmentId() {
        return mApartmentId;
    }

    public void setApartmentId(String apartmentId) {
        mApartmentId = apartmentId;
    }

    public String getRoomId() {
        return mRoomId;
    }

    public void setRoomId(String roomId) {
        mRoomId = roomId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
