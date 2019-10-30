package com.example.networksocialapplication.notifications;

public class Data {
    private String mUser;
    private int mIcon;
    private String mBody;
    private String mTitle;
    private String mSent;

    public Data(String user, int icon, String body, String title, String sent) {
        mUser = user;
        mIcon = icon;
        mBody = body;
        mTitle = title;
        mSent = sent;
    }

    public Data() {
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        mUser = user;
    }

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int icon) {
        mIcon = icon;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSent() {
        return mSent;
    }

    public void setSent(String sent) {
        mSent = sent;
    }
}
