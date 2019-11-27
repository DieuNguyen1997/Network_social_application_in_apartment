package com.example.networksocialapplication.models;

public class Resident extends User {

    private String residentId;
    private String mGender;
    private String mDateOfBirth;
    private String mStatus;

    public Resident() {
    }

    public String getResidentId() {
        return residentId;
    }

    public void setResidentId(String residentID) {
        residentId = residentID;
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

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }
}
