package com.example.networksocialapplication.user.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.user.list_chat_activity.ListChatActivity;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.MessageAdapter;
import com.example.networksocialapplication.models.Message;
import com.example.networksocialapplication.models.User;
import com.example.networksocialapplication.user.notifications.APIService;
import com.example.networksocialapplication.user.notifications.Client;
import com.example.networksocialapplication.user.notifications.Data;
import com.example.networksocialapplication.user.notifications.MyResponse;
import com.example.networksocialapplication.user.notifications.Sender;
import com.example.networksocialapplication.user.notifications.Token;
import com.example.networksocialapplication.time_current.Time;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private CircleImageView mImgOn;
    private CircleImageView mImgOff;

    private DatabaseReference mUserRef;
    private DatabaseReference mChatRef;
    private DatabaseReference mTokenRef;

    private String mCurrentUserId;
    private String mUserReceiveid;
    private Time mTimeCurrent;

    private MessageAdapter mMessageAdapter;
    private List<Message> mMessages;
    private ValueEventListener mSeenListenter;

    private APIService mAPIService;
    private boolean mNotify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAPIService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);
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
        mImgOn = findViewById(R.id.img_status_on_chat_activity);
        mImgOff = findViewById(R.id.img_status_off_chat_activity);
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
        mTokenRef = FirebaseDatabase.getInstance().getReference().child("Tokens");
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
                    String status = dataSnapshot.child("status").getValue().toString();
                    if (status.equals("online")){
                        mImgOn.setVisibility(View.VISIBLE);
                        mImgOff.setVisibility(View.GONE);
                    }else if (status.equals("offline")){
                        mImgOn.setVisibility(View.GONE);
                        mImgOff.setVisibility(View.VISIBLE);
                    }
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
                mNotify = true;
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

        final String messageSend = content;
        mUserRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (mNotify){
                    sendNotification(mUserReceiveid,user.getUsername(),messageSend);
                }
                mNotify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String userReceiveid, final String username, final String messageSend) {
        Query query = mTokenRef.orderByKey().equalTo(userReceiveid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()){
                    Token token = item.getValue(Token.class);
                    Data data = new Data(mCurrentUserId,R.mipmap.ic_launcher, username + " : " +messageSend, "Tin nhắn mới", mUserReceiveid);
                    Sender sender = new Sender(data, token.getToken());

                    mAPIService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                            if (response.code() == 200){
                                if (response.body().mSuccess == 1){
                                    Toast.makeText(getApplicationContext(),"Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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