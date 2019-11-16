package com.example.networksocialapplication.resident.homeapp.notifications;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.NotificationAdapter;
import com.example.networksocialapplication.adapters.NotificationSendManagerAdapter;
import com.example.networksocialapplication.models.Notification;
import com.example.networksocialapplication.models.NotificationFromManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private NotificationAdapter mAdapter;
    private List<Notification> mNotifications;

    private RecyclerView mRecyclerViewFromManager;
    private NotificationSendManagerAdapter mManagerAdapter;
    private List<NotificationFromManager> mFromManagers;

    public NotificationFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        initRecyclerViewFromFriend(view);
        initRecyclerViewFromManager(view);
        displayNotificationFromFriend();
        displayNotificationFromManager();

        return view;
    }

    private void displayNotificationFromManager() {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("NotificationFromManager");
        notificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    NotificationFromManager notification = data.getValue(NotificationFromManager.class);
                    mFromManagers.add(notification);
                }
                Collections.reverse(mFromManagers);
                mManagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerViewFromManager(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_list_notify_from_manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mFromManagers = new ArrayList<>();
        mManagerAdapter = new NotificationSendManagerAdapter(getActivity(),mFromManagers);
        mRecyclerView.setAdapter(mManagerAdapter);
    }

    private void initRecyclerViewFromFriend(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_list_notify_from_friend);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mNotifications = new ArrayList<>();
        mAdapter = new NotificationAdapter(getActivity(),mNotifications);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void displayNotificationFromFriend() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(user.getUid());
        notificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Notification notification = data.getValue(Notification.class);
                    mNotifications.add(notification);
                }
                Collections.reverse(mNotifications);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
