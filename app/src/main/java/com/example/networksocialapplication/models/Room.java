package com.example.networksocialapplication.models;

public class Room {
    private String mRoomId;

    private String mName;

    public Room(String roomId, String name) {
        mRoomId = roomId;
        mName = name;
    }

    public Room() {
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
