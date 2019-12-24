package com.example.networksocialapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.networksocialapplication.adapters.ReflectAdapter;
import com.example.networksocialapplication.adapters.ReflectInManagerAdapter;
import com.example.networksocialapplication.models.Apartment;
import com.example.networksocialapplication.models.Reflect;
import com.example.networksocialapplication.models.Room;
import com.example.networksocialapplication.resident.homeapp.add_post.AddPostActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;


public class ReflectWaitProgressFragment extends Fragment implements StatusReflect {

    private DatabaseReference mReflectRef;
    private List<Reflect> mReflects;
    private RecyclerView mRecyclerView;
    private ReflectInManagerAdapter mReflectAdapter;
    private DatabaseReference mRoomRef;
    private List<Room> mRooms;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reflect_wait_progress, container, false);
        initRecyclerview(view);
//        Button button = view.findViewById(R.id.btn_room);
//        Button buttonA = view.findViewById(R.id.btn_apartment);
//        buttonA.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                initRoom();
//            }
//        });
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                initRoom();
//            }
//        });
        initFirebase();
        displayListReflectFromResident();
        return view;
    }

    private void initApartment() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Apartments");
        Apartment apartment = new Apartment("Apartment_HGP","Hồ Gươm Plaza","Đường Nguyễn Trãi, Trần Phú, Hà Đông, Hà Nội","144");
        databaseReference.child("Apartment_HGP").setValue(apartment);
    }

    private void initRoom() {
        mRooms = new ArrayList<>();
        mRoomRef = FirebaseDatabase.getInstance().getReference().child("Rooms");
        for (int i = 1; i <= 9; i++) {
            for (int j = 0; j <= 15; j++) {
                if (j <= 9) {
                    String nameRoom =  "Phòng "+ i + "0" + j;
                    Room room = new Room("Apartment_HGP",nameRoom);
                    mRooms.add(room);

                } else if (j > 9) {
                    String NameRoom =  "Phòng "+ i + "" + j;
                    Room room = new Room("Apartment_HGP",NameRoom);
                    mRooms.add(room);
                }

            }
        }
        mRoomRef.setValue(mRooms);
        mRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String roomKey = data.getKey();
                    mRoomRef.child(roomKey).child("roomId").setValue(roomKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void initFirebase() {
        mReflectRef = FirebaseDatabase.getInstance().getReference().child("Reflect");
    }

    private void initRecyclerview(View view) {
        mReflects = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recycler_view_list_reflect_wait);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
    }

    private void displayListReflectFromResident() {
        mReflectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Reflect reflect = item.getValue(Reflect.class);
                    if (reflect.getStatus().equals(WAIT_PROGRESS)) {
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
