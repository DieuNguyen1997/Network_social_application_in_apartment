package com.example.networksocialapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.networksocialapplication.adapters.ReflectInManagerAdapter;
import com.example.networksocialapplication.models.Reflect;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ReflectReceivedFragment extends Fragment implements StatusReflect {

    private DatabaseReference mReflectRef;
    private List<Reflect> mReflects;
    private RecyclerView mRecyclerView;
    private ReflectInManagerAdapter mReflectAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reflect_received, container, false);
        initRecyclerview(view);
        initFirebase();
        displayListReflectFromResident();
        return view;
    }

    private void initFirebase() {
        mReflectRef = FirebaseDatabase.getInstance().getReference().child("Reflect");
    }

    private void initRecyclerview(View view) {
        mReflects = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recycler_view_list_reflect_received);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
    }

    private void displayListReflectFromResident() {
        mReflectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Reflect reflect = item.getValue(Reflect.class);
                    if (reflect.getStatus().equals(RECEIVED)) {
                        mReflects.add(reflect);
                    }
                }

                mReflectAdapter = new ReflectInManagerAdapter(getActivity(), mReflects);
                mReflectAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mReflectAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
