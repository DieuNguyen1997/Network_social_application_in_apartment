package com.example.networksocialapplication.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private CircleImageView mAvatar;
    private TextView mUsername;
    private ImageView mTakePhoto;
    private ImageView mChoosePhoto;
    private ImageView mChooseSticker;
    private EditText mContentChat;
    private ImageView mSendChat;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private DatabaseReference mChatRef;

    private String mCurrentUserId;
    private String mUserReceiveid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initFirebase();
        initView();
        initToolbar();
    }

    private void initView() {
        mAvatar = findViewById(R.id.img_avatar_toolbar_chat);
        mUsername = findViewById(R.id.txt_username_toolbar_chat);
        mTakePhoto = findViewById(R.id.img_take_photo_chat_activity);
        mChoosePhoto = findViewById(R.id.img_choose_photo_chat_activity);
        mChooseSticker = findViewById(R.id.img_choose_sticker_chat_activity);
        mContentChat = findViewById(R.id.edt_content_chat);
        mSendChat = findViewById(R.id.img_send_chat);

        Intent intent = getIntent();
        mUserReceiveid = intent.getStringExtra("otherUserId");

        mTakePhoto.setOnClickListener(this);
        mChoosePhoto.setOnClickListener(this);
        mChooseSticker.setOnClickListener(this);
        mSendChat.setOnClickListener(this);
    }

    private void initFirebase() {
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mChatRef = FirebaseDatabase.getInstance().getReference().child("Chats");

    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        displayUserReceiveInfor();
    }

    private void displayUserReceiveInfor() {
        mUserRef.child(mUserReceiveid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String avatar = dataSnapshot.child("avatar").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();

                    Glide.with(getApplicationContext()).load(avatar).into(mAvatar);
                    mUsername.setText(username);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_choose_photo_chat_activity:
                break;
            case R.id.img_take_photo_chat_activity:
                break;
            case R.id.img_send_chat:
                sendMessage();
                break;
            case R.id.img_choose_sticker_chat_activity:
                break;
        }
    }

    private void sendMessage() {
        String content = mContentChat.getText().toString();
        if (TextUtils.isEmpty(content)){
//            mChatRef.push().child()

        }else {

        }
    }
}
