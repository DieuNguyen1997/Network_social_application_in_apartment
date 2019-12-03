package com.example.networksocialapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.adapters.ChatAdapter;
import com.example.networksocialapplication.database.UserDatabase;
import com.example.networksocialapplication.models.Manager;
import com.example.networksocialapplication.models.Message;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.user.list_chat_fragment.ListChatFragment;
import com.example.networksocialapplication.user.list_overate_status.OveratStatusFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatManagerActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CircleImageView mAvatarChat;

    private String mCurrentUserId;
    private DatabaseReference mUserRef;

    private final static String TAG = "chat";
    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;
    private List<Resident> mUsers;
    private List<String> mUserIdList;

    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mTokenRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_manager);

        initToolbar();
        initView();
        initRecyclerview();
        initFirebase();
        displayListChat();
    }

    private void initFirebase() {
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Chats");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mTokenRef = FirebaseDatabase.getInstance().getReference().child("Tokens");
    }

    private void displayListChat() {
        mMessageDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (final DataSnapshot data : dataSnapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    if (message.getSenderId().equals(mCurrentUserId)) {
                        mUserIdList.add(message.getReceivedId());
                    }
                    if (message.getReceivedId().equals(mCurrentUserId)) {
                        mUserIdList.add(message.getSenderId());
                    }
                }
                mUserIdList.toString();
                readChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void readChat() {
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //get all usser
                    Resident user = data.getValue(Resident.class);

                    for (String userid : mUserIdList) {
                        //check user cos id trong lisst mUserIdList
                        if (userid.equals(user.getResidentId())){

                            if (mUsers.size() != 0){
                                for (int i  = 0; i < mUsers.size(); i++){
                                    Resident userCheck = mUsers.get(i);
                                    if (!userCheck.getResidentId().equals(user.getResidentId())){
                                        mUsers.add(user);
                                    }
                                }
                            } else {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                mChatAdapter = new ChatAdapter(getApplicationContext(), mUsers,true);
                mChatAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mChatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerview() {
        mRecyclerView = findViewById(R.id.recycler_view_list_chat_manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
    }

    private void initView() {
        mUsers = new ArrayList<>();
        mUserIdList = new ArrayList<>();
        mAvatarChat = findViewById(R.id.img_avatar_list_chat_manager);
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserInfor();
    }

    private void getUserInfor() {
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Manager");
        mUserRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String avatar = dataSnapshot.child("avatar").getValue().toString();
                Glide.with(getApplicationContext()).load(avatar).into(mAvatarChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_list_chat);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
