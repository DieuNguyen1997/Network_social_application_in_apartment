package com.example.networksocialapplication.models;

public class Candidate {
    private  String mCadiadateId;
    private String mFullName;
    private String mDateOfBirth;
    private String mAddress;
    private String mJob;
    private String mStartYear;
    private String mSlogan;
    private String mYear;

    public Candidate(String cadiadateId, String fullName, String dateOfBirth, String address, String job, String startYear, String slogan, String year) {
        mCadiadateId = cadiadateId;
        mFullName = fullName;
        mDateOfBirth = dateOfBirth;
        mAddress = address;
        mJob = job;
        mStartYear = startYear;
        mSlogan = slogan;
        mYear = year;
    }

    public Candidate(String cadiadateId, String fullName, String dateOfBirth, String address, String job, String startYear, String slogan) {
        mCadiadateId = cadiadateId;
        mFullName = fullName;
        mDateOfBirth = dateOfBirth;
        mAddress = address;
        mJob = job;
        mStartYear = startYear;
        mSlogan = slogan;
    }

    public String getCadiadateId() {
        return mCadiadateId;
    }

    public void setCadiadateId(String cadiadateId) {
        mCadiadateId = cadiadateId;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String fullName) {
        mFullName = fullName;
    }

    public String getDateOfBirth() {
        return mDateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        mDateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getJob() {
        return mJob;
    }

    public void setJob(String job) {
        mJob = job;
    }

    public String getStartYear() {
        return mStartYear;
    }

    public void setStartYear(String startYear) {
        mStartYear = startYear;
    }

    public String getSlogan() {
        return mSlogan;
    }

    public void setSlogan(String slogan) {
        mSlogan = slogan;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
    }
}

