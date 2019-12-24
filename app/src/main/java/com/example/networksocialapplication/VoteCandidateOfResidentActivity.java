package com.example.networksocialapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.networksocialapplication.adapters.VoteCandidateAdapter;
import com.example.networksocialapplication.models.Candidate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VoteCandidateOfResidentActivity extends AppCompatActivity {

    private TextView mTextViewGuide;
    private LinearLayout mLayoutDes;
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private LinearLayout mLayout;
    private String mYear;
    private Toolbar mToolbar;

    private DatabaseReference mCandidateRef;
    private DatabaseReference mElectionRef;

    private List<Candidate> mCandidates;
    private VoteCandidateAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_candidate_of_resident);

        initToolbar();
        initFirebase();
        checkVotedResident();
//        setUpLayout();
        initRecylcerview();
        initView();
    }

    private void checkVotedResident() {
        mLayout = findViewById(R.id.layout_vote_candidate_resident);
        mTextView = findViewById(R.id.txt_no_have_election);
        DatabaseReference mVotedResiddentRef = FirebaseDatabase.getInstance().getReference().child("VotedResident");
        mVotedResiddentRef.child(mYear).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentId  = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (dataSnapshot.child(currentId).exists()){
                    mTextView.setVisibility(View.VISIBLE);
                    mLayout.setVisibility(View.GONE);
                }else {
                    mTextView.setVisibility(View.GONE);
                    mLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initToolbar() {
        Calendar calendar = Calendar.getInstance();
        mYear = String.valueOf(calendar.get(Calendar.YEAR));
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Bầu cử ban quản lý năm " + mYear);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initFirebase() {
        mElectionRef = FirebaseDatabase.getInstance().getReference().child("Elections");
        mCandidateRef = FirebaseDatabase.getInstance().getReference().child("Candidates");
    }


    private void setUpLayout() {



        mElectionRef.child(mYear).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mLayout.setVisibility(View.VISIBLE);
                } else {
                    mTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//
//
private void displayListCandidate() {
    mCandidateRef.child(mYear).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                Candidate candidate = data.getValue(Candidate.class);
                mCandidates.add(candidate);
            }
            mAdapter = new VoteCandidateAdapter(VoteCandidateOfResidentActivity.this, mCandidates);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.setAdapter(mAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}
//
    private void initRecylcerview() {
        mCandidates = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recycler_view_list_candidate_in_resident);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        displayListCandidate();
    }

    private void initView() {
        mTextViewGuide = findViewById(R.id.txt_guide_vote_election);
        mLayoutDes = findViewById(R.id.layout_des_vote_election);
        mTextViewGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLayoutDes.isShown()){
                    mLayoutDes.setVisibility(View.GONE);
                }else {
                    mLayoutDes.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
