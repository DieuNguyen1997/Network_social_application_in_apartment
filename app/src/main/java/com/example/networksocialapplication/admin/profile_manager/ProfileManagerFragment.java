package com.example.networksocialapplication.admin.profile_manager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.Manager;
import com.example.networksocialapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileManagerFragment extends Fragment implements View.OnClickListener{

    private ImageView mCoverPhoto;
    private CircleImageView mAvatar;
    private TextView mTxtUsername;
    private TextView mDes;
    private LinearLayout mLayout_create_notify;
    private LinearLayout mLayout_create_event;

    private TextView mTxtHotline;
    private TextView mTxtLocation;

    private DatabaseReference mManagerDatabase;
    private String mCurrentUserId;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_manager, container, false);
        initView(view);
        initFirebase();
        displayInformationBasic();
        return view;
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
                    Manager user = dataSnapshot.getValue(Manager.class);
                    mTxtUsername.setText(user.getUsername());
                    mDes.setText(user.getDes());
                    mTxtHotline.setText(user.getHotline());
                    mTxtLocation.setText(user.getLocation());
                    Glide.with(getContext()).load(user.getAvatar()).error(R.drawable.ic_load_image_erroe).into(mAvatar);
                    Glide.with(getContext()).load(user.getCoverPhoto()).error(R.drawable.ic_load_image_erroe).into(mCoverPhoto);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
