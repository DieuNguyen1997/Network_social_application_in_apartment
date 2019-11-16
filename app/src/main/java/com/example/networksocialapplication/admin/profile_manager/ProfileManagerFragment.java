package com.example.networksocialapplication.admin.profile_manager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.ReflectAdapter;
import com.example.networksocialapplication.adapters.ReflectInManagerAdapter;
import com.example.networksocialapplication.models.Manager;
import com.example.networksocialapplication.models.Reflect;
import com.example.networksocialapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileManagerFragment extends Fragment implements View.OnClickListener {

    private ImageView mCoverPhoto;
    private CircleImageView mAvatar;
    private TextView mTxtUsername;
    private TextView mDes;
    private LinearLayout mLayout_create_notify;
    private LinearLayout mLayout_create_event;
    private RecyclerView mRecyclerView;

    private TextView mTxtHotline;
    private TextView mTxtLocation;

    private DatabaseReference mManagerDatabase;
    private DatabaseReference mReflectRef;
    private String mCurrentUserId;
    private List<Reflect> mReflects;
    private ReflectInManagerAdapter mReflectAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_manager, container, false);
        initView(view);
        initFirebase();
        initRecyclerview(view);
        displayInformationBasic();
        displayListReflectFromResident();
        return view;
    }

    private void initRecyclerview(View view) {
        mReflects = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recycler_view_list_reflect_profile_manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
    }

    private void displayListReflectFromResident() {
        mReflectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Reflect reflect = item.getValue(Reflect.class);
                    mReflects.add(reflect);
                    mReflectAdapter = new ReflectInManagerAdapter(getActivity(), mReflects);
                    mReflectAdapter.notifyDataSetChanged();
                    mRecyclerView.setAdapter(mReflectAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initView(View view) {
        mLayout_create_notify = view.findViewById(R.id.root_create_notification);
        mLayout_create_event = view.findViewById(R.id.root_create_event);
        mTxtHotline = view.findViewById(R.id.txt_hotline_profile_manager);
        mTxtLocation = view.findViewById(R.id.txt_location_profile_manager);

        mCoverPhoto = view.findViewById(R.id.img_cover_photo_profile_manager);
        mAvatar = view.findViewById(R.id.img_avatar_profile_manager);
        mTxtUsername = view.findViewById(R.id.txt_username_profile_manager);
        mDes = view.findViewById(R.id.txt_des_profile_manager);

        mLayout_create_event.setOnClickListener(this);
        mLayout_create_notify.setOnClickListener(this);
    }

    private void displayInformationBasic() {
        mManagerDatabase.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Manager manager = dataSnapshot.getValue(Manager.class);
                    String manaderId = manager.getManagerId();
                    Log.d("profile", manaderId);
                    mTxtUsername.setText(manager.getUsername());
                    mDes.setText(manager.getDes());
                    mTxtHotline.setText(manager.getPhoneNumber());
                    mTxtLocation.setText(manager.getLocation());
                    Glide.with(getActivity()).load(manager.getAvatar()).error(R.drawable.ic_load_image_erroe).into(mAvatar);
                    Glide.with(getActivity()).load(manager.getCoverPhoto()).error(R.drawable.ic_load_image_erroe).into(mCoverPhoto);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initFirebase() {
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mManagerDatabase = FirebaseDatabase.getInstance().getReference().child("Manager");
        mReflectRef = FirebaseDatabase.getInstance().getReference().child("Reflect");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root_create_notification:
                createNotification();
                break;
            case R.id.root_create_event:
                createEvent();
                break;
        }
    }

    private void createEvent() {
    }

    private void createNotification() {
    }
}
