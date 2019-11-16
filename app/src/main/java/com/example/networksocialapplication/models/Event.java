package com.example.networksocialapplication.models;

public class Event extends PostParent {
    private String mEventId;
    private String mNameEvent;
    private String mLocation;
    private String mDateStart;
    private String mTimeStar;
    private String mDateFinish;
    private String mTimeFinish;

    public Event() {
    }

    public Event(String date, String time, String content, String eventId, String nameEvent, String location, String dateStart, String timeStar, String dateFinish, String timeFinish) {
        super(date, time, content);
        mEventId = eventId;
        mNameEvent = nameEvent;
        mLocation = location;
        mDateStart = dateStart;
        mTimeStar = timeStar;
        mDateFinish = dateFinish;
        mTimeFinish = timeFinish;
    }

    public String getEventId() {
        return mEventId;
    }

    public void setEventId(String eventId) {
        mEventId = eventId;
    }

    public String getNameEvent() {
        return mNameEvent;
    }

    public void setNameEvent(String nameEvent) {
        mNameEvent = nameEvent;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getDateStart() {
        return mDateStart;
    }

    public void setDateStart(String dateStart) {
        mDateStart = dateStart;
    }

    public String getTimeStar() {
        return mTimeStar;
    }

    public void setTimeStar(String timeStar) {
        mTimeStar = timeStar;
    }

    public String getDateFinish() {
        return mDateFinish;
    }

    public void setDateFinish(String dateFinish) {
        mDateFinish = dateFinish;
    }

    public String getTimeFinish() {
        return mTimeFinish;
    }

    public void setTimeFinish(String timeFinish) {
        mTimeFinish = timeFinish;
    }
}
