package com.example.networksocialapplication.database;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.networksocialapplication.adapters.ChatAdapter;
import com.example.networksocialapplication.models.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageDatabase {
    private DatabaseReference mMessageRef;
    private Context mContext;



    public MessageDatabase(Context context) {
        mContext = context;
    }

    public void setUpMessageRef() {
        mMessageRef = FirebaseDatabase.getInstance().getReference().child("Chats");
    }

    public void getListMessage(final String currentUserid, final List<Message> messages){
        mMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        Message message = data.getValue(Message.class);
                        if (message.getSenderId().equals(currentUserid) || message.getReceivedId().equals(currentUserid)){
                            messages.add(message);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
