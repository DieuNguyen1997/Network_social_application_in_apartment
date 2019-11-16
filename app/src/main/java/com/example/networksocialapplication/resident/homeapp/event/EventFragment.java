package com.example.networksocialapplication.resident.homeapp.event;

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

import com.example.networksocialapplication.CreateEventActivity;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.EventAdapter;
import com.example.networksocialapplication.models.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class EventFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private EventAdapter mEventAdapter;
    private List<Event> mEvents;

    private DatabaseReference mEventRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        initRecyclerView(view);
        initFirebase();
        displayListEvent();
        return view;
    }

    private void initFirebase() {
        mEventRef = FirebaseDatabase.getInstance().getReference().child("Events");
    }

    private void displayListEvent() {
        mEventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Event event = data.getValue(Event.class);
                    mEvents.add(event);
                    mEventAdapter = new EventAdapter(getActivity(),mEvents);
                    mEventAdapter.notifyDataSetChanged();
                    mRecyclerView.setAdapter(mEventAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView(View view) {
        mEvents = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recycler_view_list_event_at_resident);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
    }



}
