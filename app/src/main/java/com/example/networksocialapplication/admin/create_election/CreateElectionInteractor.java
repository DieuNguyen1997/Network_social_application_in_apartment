package com.example.networksocialapplication.admin.create_election;

import android.app.Activity;

import com.example.networksocialapplication.models.Candidate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateElectionInteractor implements CreateElectionContract.Intractor {

    private CreateElectionContract.onCreateElectionListener mListener;

    public CreateElectionInteractor(CreateElectionContract.onCreateElectionListener listener) {
        mListener = listener;
    }

    @Override
    public void performFirebaseAddCandidate(Activity activity, Candidate candidate) {
        DatabaseReference candidateRef = FirebaseDatabase.getInstance().getReference().child(candidate.getYear());
        String id = candidateRef.push().getKey();
        candidateRef.child(id).setValue(candidate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mListener.onAddSuccess("Thêm ứng viên thành công");
            }
        });
    }
}
