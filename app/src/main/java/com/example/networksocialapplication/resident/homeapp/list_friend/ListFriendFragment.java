package com.example.networksocialapplication.resident.homeapp.list_friend;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.FriendAdapter;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ListFriendFragment extends Fragment {

    private FriendAdapter mFriendAdapter;
    private RecyclerView mRecyclerView;
    private List<Resident> mUsers;

    private DatabaseReference mFriendRef;
    private String mCurrentUserID;
    private DatabaseReference mUserRef;




    public ListFriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_list_friend, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view_list_friend);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL, false));
        mUsers = new ArrayList<>();
        initFirebase();
        displayListFriend();
        return view;
    }

    private void initFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mCurrentUserID = auth.getCurrentUser().getUid();
        mFriendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void displayListFriend() {
        mFriendRef.child(mCurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()){
                    String mFriendID = item.getKey();
                    mUserRef.child(mFriendID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Resident user = dataSnapshot.getValue(Resident.class);
                            mUsers.add(user);
                            mFriendAdapter = new FriendAdapter(getContext(),mUsers);
                            mFriendAdapter.notifyDataSetChanged();
                            mRecyclerView.setAdapter(mFriendAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
