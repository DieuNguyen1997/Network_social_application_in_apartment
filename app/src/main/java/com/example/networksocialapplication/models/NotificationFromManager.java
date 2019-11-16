package com.example.networksocialapplication.models;

public class NotificationFromManager  extends PostParent{
    private String mNotifyId;
    private  String mTitle;

    public NotificationFromManager() {

    }

    public NotificationFromManager(String date, String time, String content, String notifyId, String title) {
        super(date, time, content);
        mNotifyId = notifyId;
        mTitle = title;
    }

    public String getNotifyId() {
        return mNotifyId;
    }

    public void setNotifyId(String notifyId) {
        mNotifyId = notifyId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
