package com.example.networksocialapplication.admin.create_election;

import android.app.Activity;

import com.example.networksocialapplication.models.Candidate;

public class CreateElectionPresenter implements CreateElectionContract.Presenter, CreateElectionContract.onCreateElectionListener {

    private CreateElectionContract.View mView;
    private CreateElectionInteractor mInteractor;

    public CreateElectionPresenter(CreateElectionContract.View view, CreateElectionInteractor interactor) {
        mView = view;
        mInteractor = interactor;
    }

    @Override
    public void addCandicate(Activity activity, Candidate candidate) {
        mInteractor.performFirebaseAddCandidate(activity, candidate);
    }

    @Override
    public void onAddSuccess(String message) {
        mView.addCadicateSuccess(message);
    }

    @Override
    public void onAddFailue(String message) {
        mView.addCadicateFailue(message);
    }
}
