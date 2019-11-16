package com.example.networksocialapplication.resident.homeapp.list_request_friend;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.RequestFriendAdapter;
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


public class ListRequestFriendFragment extends Fragment {

    private RequestFriendAdapter mAdapter;
    private List<Resident> mUsers;
    private RecyclerView mRecyclerView;

    private DatabaseReference mRequestFriendRef;
    private String mCurrentUserId;
    private DatabaseReference mUserRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_request_friend, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view_list_request_friend);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mUsers = new ArrayList<>();

        initFirebase();
        getListRequestFriend();
        return view;
    }

    private void initFirebase() {
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRequestFriendRef = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void getListRequestFriend() {
        mRequestFriendRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (final DataSnapshot data : dataSnapshot.getChildren()) {
                        final String sender_id = data.getKey();
                        mRequestFriendRef.child(mCurrentUserId).child(sender_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String request_type = dataSnapshot.child("request_type").getValue().toString();
                                    if (request_type.equals("Received")) {
                                        mUserRef.child(sender_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Resident user = dataSnapshot.getValue(Resident.class);
                                                mUsers.add(user);
                                                mAdapter = new RequestFriendAdapter(getActivity(), mUsers);
                                                mAdapter.notifyDataSetChanged();
                                                mRecyclerView.setAdapter(mAdapter);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                } else
                                    Toast.makeText(getActivity(), "Không có lời mời kết bạn nào", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                } else
                    Toast.makeText(getActivity(), "Không có lời mời kết bạn nào", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
