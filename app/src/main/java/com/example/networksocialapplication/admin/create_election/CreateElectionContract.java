package com.example.networksocialapplication.admin.create_election;

import android.app.Activity;

import com.example.networksocialapplication.models.Candidate;

public interface CreateElectionContract {
    interface View{
        void addCadicateSuccess(String message);
        void addCadicateFailue(String message);
    }

    interface Presenter{
        void addCandicate(Activity activity, Candidate candidate);
    }

    interface Intractor{
        void performFirebaseAddCandidate(Activity activity, Candidate candidate);
    }

    interface onCreateElectionListener{
        void onAddSuccess(String message);
        void onAddFailue(String message);
    }
}
