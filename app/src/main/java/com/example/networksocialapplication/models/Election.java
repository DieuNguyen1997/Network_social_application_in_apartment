package com.example.networksocialapplication.models;

public class Election {
    private String mElectionId;
    private String mYear;

    public Election(String electionId, String year) {
        mElectionId = electionId;
        mYear = year;
    }

    public Election() {
    }

    public String getElectionId() {
        return mElectionId;
    }

    public void setElectionId(String electionId) {
        mElectionId = electionId;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
    }
}
