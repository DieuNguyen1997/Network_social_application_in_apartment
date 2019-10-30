package com.example.networksocialapplication.database;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDatabase {

    private DatabaseReference mUserRef;
    private Context mContext;



    public UserDatabase(Context context) {
        mContext = context;
    }

    public void setUpUserRef() {
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public User getAvatarAndUsernameUser(String mUserId) {
        final User user = new User();
        mUserRef.child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String avatar = dataSnapshot.child("avatar").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    user.setUsername(username);
                    user.setAvatar(avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return user;

    }

    public void getAvatarUser(String mUserId, final CircleImageView imgAvatar) {
        mUserRef.child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                     String avatar = dataSnapshot.child("avatar").getValue().toString();
                     Glide.with(mContext).load(avatar).into(imgAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
