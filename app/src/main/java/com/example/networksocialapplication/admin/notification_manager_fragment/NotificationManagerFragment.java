package com.example.networksocialapplication.admin.notification_manager_fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.networksocialapplication.CreateNotificationActivity;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.NotificationSendManagerAdapter;
import com.example.networksocialapplication.models.NotificationFromManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class NotificationManagerFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private NotificationSendManagerAdapter mAdapter;
    private List<NotificationFromManager> mNotifications;
    private FloatingActionButton mBtnAdd;
    private DatabaseReference mNotificationRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_manager, container, false);
        initRecyclerview(view);
        initView(view);
        initFirebase();
        displayListNotifi();
        return view;
    }

    private void initFirebase() {
        mNotificationRef = FirebaseDatabase.getInstance().getReference().child("NotificationFromManager");
    }

    private void displayListNotifi() {
        mNotificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    NotificationFromManager notification = data.getValue(NotificationFromManager.class);
                    mNotifications.add(notification);
                    mAdapter = new NotificationSendManagerAdapter(getActivity(), mNotifications);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.setAdapter(mAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initView(View view) {

        mBtnAdd = view.findViewById(R.id.btn_add_notify_fragment);
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreateNotificationActivity.class));
            }
        });
    }

    private void initRecyclerview(View view) {
        mNotifications = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recycler_view_list_fragment_notification_manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
    }


}
