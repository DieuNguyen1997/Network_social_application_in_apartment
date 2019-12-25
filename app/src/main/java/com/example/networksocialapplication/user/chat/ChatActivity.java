package com.example.networksocialapplication.user.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.MediaRouteButton;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.user.comment.CommentActivity;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ChatActivity";
    private static final int REQUEST_CODE_CHOOSE_PHOTO_COMMENT = 150;

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
    private Uri mImageUri;
    private StorageReference mImageChatRef;
    private CoordinatorLayout mLayoutImage;
    private ImageView mImgChat;
    private ImageButton mDelete;
    private String mManagerId;
    private DatabaseReference mManagerRef;

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

    private void seenMessage(final String userId) {
        mSeenListenter = mChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    if (message.getReceivedId().equals(mCurrentUserId) && message.getSenderId().equals(userId)) {
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

        if (mManagerId == null) {
            mChatRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mMessages.clear();
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
        } else {
            mChatRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Message message = data.getValue(Message.class);
                        if (message.getReceivedId().equals(mCurrentUserId) && message.getSenderId().equals(mManagerId) ||
                                message.getReceivedId().equals(mManagerId) && message.getSenderId().equals(mCurrentUserId)) {
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
            seenMessage(mManagerId);
        }


    }

    private void initRecyclerview() {
        mRecyclerView = findViewById(R.id.recycler_view_list_message);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mMessages = new ArrayList<>();
    }

    private void initView() {
        mImgChat = findViewById(R.id.image_chat);
        mDelete = findViewById(R.id.btn_delete_image_chat);
        mImgOn = findViewById(R.id.img_status_on_chat_activity);
        mImgOff = findViewById(R.id.img_status_off_chat_activity);
        mAvatar = findViewById(R.id.img_avatar_toolbar_chat);
        mUsername = findViewById(R.id.txt_username_toolbar_chat);
        mChoosePhoto = findViewById(R.id.img_choose_photo_chat_activity);
        mChooseSticker = findViewById(R.id.img_choose_sticker_chat_activity);
        mContentChat = findViewById(R.id.edt_content_chat);
        mSendChat = findViewById(R.id.img_send_chat);
        mLayoutImage = findViewById(R.id.layout_image_chat);
        mTimeCurrent = new Time();

        Intent intent = getIntent();
        mUserReceiveid = intent.getStringExtra("otherUserId");

        mManagerId = getIntent().getStringExtra("managerId");

        mChoosePhoto.setOnClickListener(this);
        mChooseSticker.setOnClickListener(this);
        mSendChat.setOnClickListener(this);
        mDelete.setOnClickListener(this);
    }

    private void initFirebase() {
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mManagerRef = FirebaseDatabase.getInstance().getReference().child("Manager");
        mChatRef = FirebaseDatabase.getInstance().getReference().child("Chats");
        mTokenRef = FirebaseDatabase.getInstance().getReference().child("Tokens");
        mImageChatRef = FirebaseStorage.getInstance().getReference().child("Image_Chats");
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
        if (mManagerId == null) {
            mUserRef.child(mUserReceiveid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String avatar = dataSnapshot.child("avatar").getValue().toString();
                        String username = dataSnapshot.child("username").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();
                        if (status.equals("online")) {
                            mImgOn.setVisibility(View.VISIBLE);
                            mImgOff.setVisibility(View.GONE);
                        } else if (status.equals("offline")) {
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
        } else {
            mManagerRef.child(mManagerId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String avatar = dataSnapshot.child("avatar").getValue().toString();
                        String username = dataSnapshot.child("username").getValue().toString();
                        mImgOn.setVisibility(View.GONE);
                        mImgOff.setVisibility(View.GONE);
//                        String status = dataSnapshot.child("status").getValue().toString();
//                        if (status.equals("online")) {
//                            mImgOn.setVisibility(View.VISIBLE);
//                            mImgOff.setVisibility(View.GONE);
//                        } else if (status.equals("offline")) {
//                            mImgOn.setVisibility(View.GONE);
//                            mImgOff.setVisibility(View.VISIBLE);
//                        }
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_choose_photo_chat_activity:
                chooseImageFromGallery();
                break;

            case R.id.img_send_chat:
                mNotify = true;
                sendMessage();
                break;
            case R.id.img_choose_sticker_chat_activity:
                break;
            case R.id.btn_delete_image_chat:
                deleteImage();
                break;
        }
    }

    private void deleteImage() {
        mImageUri = null;
        mLayoutImage.setVisibility(View.GONE);
    }

    private void chooseImageFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_PHOTO_COMMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO_COMMENT && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            mImgChat.setImageURI(mImageUri);
            mLayoutImage.setVisibility(View.VISIBLE);
            mDelete.setVisibility(View.VISIBLE);
        }
    }

    private void sendMessage() {
        String content = mContentChat.getText().toString();
        final String chatId = mChatRef.push().getKey();
        String timeCurrent = mTimeCurrent.getTimeCurrent();

        if (mManagerId == null) {
            Message message = new Message(chatId, mCurrentUserId, mUserReceiveid, content, timeCurrent, false);
            if (TextUtils.isEmpty(content)) {
                mContentChat.setError("Hãy nhập nội dung chát");
            } else {
                mChatRef.child(chatId).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendImage(chatId);
                        mContentChat.setText("");
                    }
                });
            }
            final String messageSend = content;

//            mUserRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    Resident user = dataSnapshot.getValue(Resident.class);
//                    if (mNotify) {
//                        sendNotification(mUserReceiveid, user.getUsername(), messageSend);
//                    }
//                    mNotify = false;
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });

        } else {
            Message message = new Message(chatId, mCurrentUserId, mManagerId, content, timeCurrent, false);
            message.toString();
            if (TextUtils.isEmpty(content)) {
                mContentChat.setError("Hãy nhập nội dung chát");
            } else {
                mChatRef.child(chatId).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendImage(chatId);
                        mContentChat.setText("");
                    }
                });
            }
        }


    }

    private void sendImage(final String chatId) {
        if (mImageUri != null) {
            final StorageReference database = mImageChatRef.child(chatId).child(mImageUri.getLastPathSegment() + ".jpg");
            UploadTask uploadTask = database.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    } else
                        return database.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downLoadUri = task.getResult();
                        String imageUrl = downLoadUri.toString();

                        mChatRef.child(chatId).child("image").setValue(imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mLayoutImage.setVisibility(View.GONE);
                            }
                        });

                    } else {
                        String mesasge = task.getException().getMessage();
                        Log.d(TAG, mesasge);
                        Toast.makeText(ChatActivity.this, "Luu anh ko thanh cong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void sendNotification(String userReceiveid, final String username, final String messageSend) {
        Query query = mTokenRef.orderByKey().equalTo(userReceiveid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Token token = item.getValue(Token.class);
                    Data data = new Data(mCurrentUserId, R.mipmap.ic_launcher, username + " : " + messageSend, "Tin nhắn mới", mUserReceiveid);
                    Sender sender = new Sender(data, token.getToken());

                    mAPIService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                            if (response.code() == 200) {
                                if (response.body().mSuccess == 1) {
                                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
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

    private void status(String status) {
        if (mManagerId == null) {
            mUserRef.child(mCurrentUserId).child("status").setValue(status);
        }
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
