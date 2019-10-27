package com.example.networksocialapplication.time_current;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Time {
    public Time() {
    }

    public String getTimeCurrent() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        String mSaveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        String mSaveCurrentTime = currentTime.format(calForTime.getTime());

        return mSaveCurrentTime+ " - "+ mSaveCurrentDate;
    }
}
