package com.example.networksocialapplication.resident.homeapp.view_profile_manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.CreateReflectActivity;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.Manager;
import com.example.networksocialapplication.user.chat.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileManagerFromUserActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mCoverPhoto;
    private CircleImageView mAvatar;
    private TextView mTxtUsername;
    private TextView mDes;
    private LinearLayout mLayout_create_reflect;
    private LinearLayout mLayout_chat_manager;

    private TextView mTxtHotline;
    private TextView mTxtLocation;

    private DatabaseReference mManagerDatabase;
    private String mManagerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_manager_from_user);
        initView();
        initFirebase();
        displayInformationBasic();

    }

    private void initView() {
        mLayout_create_reflect = findViewById(R.id.root_create_reflect);
        mLayout_chat_manager = findViewById(R.id.root_chat_with_manager);
        mTxtHotline = findViewById(R.id.txt_hotline_profile_manager_from_user);
        mTxtLocation = findViewById(R.id.txt_location_profile_manager_from_user);

        mCoverPhoto = findViewById(R.id.img_cover_photo_profile_manager_from_user);
        mAvatar = findViewById(R.id.img_avatar_profile_manager_from_user);
        mTxtUsername = findViewById(R.id.txt_username_profile_manager_from_user);
        mDes = findViewById(R.id.txt_des_profile_manager_from_user);

        mLayout_create_reflect.setOnClickListener(this);
        mLayout_chat_manager.setOnClickListener(this);
    }

    private void displayInformationBasic() {
        mManagerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Manager user = data.getValue(Manager.class);
                    mManagerId = user.getManagerId();
                    mTxtUsername.setText(user.getUsername());
                    mDes.setText(user.getDes());
                    mTxtHotline.setText(user.getPhoneNumber());
                    mTxtLocation.setText(user.getLocation());
                    Glide.with(getApplicationContext()).load(user.getAvatar()).error(R.drawable.ic_load_image_erroe).into(mAvatar);
                    Glide.with(getApplicationContext()).load(user.getCoverPhoto()).error(R.drawable.ic_load_image_erroe).into(mCoverPhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initFirebase() {
        mManagerDatabase = FirebaseDatabase.getInstance().getReference().child("Manager");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root_create_reflect:
                Intent intent = new Intent(this, CreateReflectActivity.class);
                startActivity(intent);
                break;
            case R.id.root_chat_with_manager:
                Intent intentChat = new Intent(this, ChatActivity.class);
                intentChat.putExtra("managerId", mManagerId);
                startActivity(intentChat);
                break;
        }
    }
}
