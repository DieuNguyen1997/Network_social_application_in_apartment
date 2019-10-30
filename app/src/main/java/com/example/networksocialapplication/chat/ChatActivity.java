package com.example.networksocialapplication.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.ListChatActivity;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.StickerIndexingService;
import com.example.networksocialapplication.activities.MainActivity;
import com.example.networksocialapplication.adapters.MessageAdapter;
import com.example.networksocialapplication.models.Message;
import com.example.networksocialapplication.time_current.Time;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ChatActivity";

    private Toolbar mToolbar;
    private CircleImageView mAvatar;
    private TextView mUsername;
    private ImageView mTakePhoto;
    private ImageView mChoosePhoto;
    private ImageView mChooseSticker;
    private EditText mContentChat;
    private ImageView mSendChat;
    private RecyclerView mRecyclerView;

    private DatabaseReference mUserRef;
    private DatabaseReference mChatRef;

    private String mCurrentUserId;
    private String mUserReceiveid;
    private Time mTimeCurrent;

    private MessageAdapter mMessageAdapter;
    private List<Message> mMessages;
    private ValueEventListener mSeenListenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initFirebase();
        initView();
        initToolbar();
        initRecyclerview();

    }

    private void seenMessage(final String userId){
        mSeenListenter = mChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Message message = data.getValue(Message.class);
                    if (message.getReceivedId().equals(mCurrentUserId) && message.getSenderId().equals(userId)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seen", true);
                        data.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayMessage(final String imageUrl) {

        mChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    if (message.getReceivedId().equals(mCurrentUserId) && message.getSenderId().equals(mUserReceiveid) ||
                            message.getReceivedId().equals(mUserReceiveid) && message.getSenderId().equals(mCurrentUserId)) {
                        mMessages.add(message);
                    }
                }
                mMessageAdapter = new MessageAdapter(getApplicationContext(), mMessages, imageUrl);
                mMessageAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mMessageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        seenMessage(mUserReceiveid);

    }

    private void initRecyclerview() {
        mRecyclerView = findViewById(R.id.recycler_view_list_message);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mMessages = new ArrayList<>();
    }

    private void initView() {
        mAvatar = findViewById(R.id.img_avatar_toolbar_chat);
        mUsername = findViewById(R.id.txt_username_toolbar_chat);
        mTakePhoto = findViewById(R.id.img_take_photo_chat_activity);
        mChoosePhoto = findViewById(R.id.img_choose_photo_chat_activity);
        mChooseSticker = findViewById(R.id.img_choose_sticker_chat_activity);
        mContentChat = findViewById(R.id.edt_content_chat);
        mSendChat = findViewById(R.id.img_send_chat);
        mTimeCurrent = new Time();

        Intent intent = getIntent();
        mUserReceiveid = intent.getStringExtra("otherUserId");
        mCurrentUserId = intent.getStringExtra("currentUserId");

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
                startActivity(new Intent(ChatActivity.this, ListChatActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        displayUserReceiveInfor();
    }

    private void displayUserReceiveInfor() {
        mUserRef.child(mUserReceiveid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String avatar = dataSnapshot.child("avatar").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    Glide.with(getApplicationContext()).load(avatar).into(mAvatar);
                    mUsername.setText(username);
                    displayMessage(avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
        String chatId = mChatRef.push().getKey();
        String timeCurrent = mTimeCurrent.getTimeCurrent();

        Message message = new Message(chatId, mCurrentUserId, mUserReceiveid, content, timeCurrent, false);
        if (TextUtils.isEmpty(content)) {
            mContentChat.setError("Hãy nhập nội dung chát");
        } else {
            mChatRef.child(chatId).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mContentChat.setText("");
                }
            });
        }
    }

    private void status(String status){
        mUserRef.child(mCurrentUserId).child("status").setValue(status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mChatRef.removeEventListener(mSeenListenter);
        status("offline");
    }
}
