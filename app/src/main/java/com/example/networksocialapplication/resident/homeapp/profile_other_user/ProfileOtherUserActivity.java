package com.example.networksocialapplication.resident.homeapp.profile_other_user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.PostAdapter;
import com.example.networksocialapplication.user.chat.ChatActivity;
import com.example.networksocialapplication.models.Post;
import com.example.networksocialapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileOtherUserActivity extends AppCompatActivity {

    private TextView mTxtPhoneNumber;
    private TextView mTxtGender;
    private TextView mTxtDateBirth;

    private RecyclerView mRecyclerView;
    private PostAdapter mPostAdapter;
    private ArrayList<Post> mListPost;

    private static final String TAG = "profile";
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mPostDatabase;
    private DatabaseReference mFriendRequestData;
    private DatabaseReference mFriendData;

    private CircleImageView mAvatar;
    private TextView mTxtUsername;
    private TextView mTxtDes;
    private LinearLayout mLayoutRequestFriend;
    private LinearLayout mLayoutCancelRequest;
    private TextView mTxtSendRequest;
    private ImageView mCoverPhoto;

    private String mSenderUserID;
    private String mReceiverUserID;
    private String CURRENT_STATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_other_user);

        initComponents();

    }

    private void initComponents() {
        initFirebase();
        initUser();
        initView();
        initRecyclerview();
        displayInformationBasic();
        displayListPost();
        controlRequesFriend();
    }

    private void controlRequesFriend() {
        mLayoutCancelRequest.setVisibility(View.INVISIBLE);
        mLayoutCancelRequest.setEnabled(false);

        if (!mSenderUserID.equals(mReceiverUserID)) {
            mLayoutRequestFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLayoutRequestFriend.setEnabled(false);
                    if (CURRENT_STATE.equals("not_friend")) {
                        sendRequestFriend();
                    } else if (CURRENT_STATE.equals("request_sent")) {
                        cancelRequestFriend();
                    } else if (CURRENT_STATE.equals("request_received")) {
                        acceptRequestFriend();
                    } else if (CURRENT_STATE.equals("friend")) {
                        unfriend();
                    }
                }
            });
        } else {
            mLayoutCancelRequest.setVisibility(View.INVISIBLE);
            mLayoutRequestFriend.setVisibility(View.INVISIBLE);
        }
    }

    private void unfriend() {
        mFriendData.child(mSenderUserID).child(mReceiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFriendData.child(mReceiverUserID).child(mSenderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mLayoutRequestFriend.setEnabled(true);
                                CURRENT_STATE = "not_friend";
                                mTxtSendRequest.setText("Kết bạn");

                                mLayoutCancelRequest.setVisibility(View.INVISIBLE);
                                mLayoutCancelRequest.setEnabled(false);
                            } else {
                                Toast.makeText(getApplicationContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void acceptRequestFriend() {
        mFriendData.child(mSenderUserID).child(mReceiverUserID).setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFriendData.child(mReceiverUserID).child(mSenderUserID).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mFriendRequestData.child(mSenderUserID).child(mReceiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mFriendRequestData.child(mReceiverUserID).child(mSenderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mLayoutRequestFriend.setEnabled(true);
                                                        CURRENT_STATE = "friend";
                                                        mTxtSendRequest.setText("Hủy kết bạn");

                                                        mLayoutCancelRequest.setVisibility(View.INVISIBLE);
                                                        mLayoutCancelRequest.setEnabled(false);

                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cancelRequestFriend() {
        mFriendRequestData.child(mSenderUserID).child(mReceiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFriendRequestData.child(mReceiverUserID).child(mSenderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mLayoutRequestFriend.setEnabled(true);
                                CURRENT_STATE = "not_friend";
                                mTxtSendRequest.setText("Kết bạn");

                                mLayoutCancelRequest.setVisibility(View.INVISIBLE);
                                mLayoutCancelRequest.setEnabled(false);

                            } else {
                                Toast.makeText(getApplicationContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initUser() {
        Intent intent = getIntent();
        mReceiverUserID = intent.getStringExtra("userID");
        mSenderUserID = mAuth.getCurrentUser().getUid();
    }

    private void displayListPost() {
    }

    private void displayInformationBasic() {
        mUserDatabase.child(mReceiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    mTxtUsername.setText(user.getUsername());
                    mTxtDes.setText(user.getDes());
                    mTxtPhoneNumber.setText(user.getDes());
                    mTxtGender.setText(user.getGender());
                    mTxtDateBirth.setText(user.getDateOfBirth());
                    Glide.with(getApplicationContext()).load(user.getAvatar()).error(R.drawable.ic_load_image_erroe).into(mAvatar);
                    Glide.with(getApplicationContext()).load(user.getCoverPhoto()).error(R.drawable.ic_load_image_erroe).into(mCoverPhoto);

                    saveStatusRequest();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveStatusRequest() {
        mFriendRequestData.child(mSenderUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String request_type = dataSnapshot.child(mReceiverUserID).child("request_type").getValue().toString();
                    if (request_type.equals("Sent")) {
                        CURRENT_STATE = "request_sent";
                        mTxtSendRequest.setText("Hủy yêu cầu kết bạn");
                        mLayoutCancelRequest.setVisibility(View.INVISIBLE);
                        mLayoutCancelRequest.setEnabled(false);
                    } else if (request_type.equals("Received")) {
                        CURRENT_STATE = "request_received";
                        mTxtSendRequest.setText("Chấp nhận yêu cầu kết bạn");
                        mLayoutCancelRequest.setVisibility(View.VISIBLE);
                        mLayoutCancelRequest.setEnabled(true);
                        mLayoutCancelRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelRequestFriend();
                            }
                        });
                    }
                } else {
                    mFriendData.child(mSenderUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(mReceiverUserID)) {
                                CURRENT_STATE = "friend";
                                mTxtSendRequest.setText("Hủy kết bạn");
                                mLayoutCancelRequest.setVisibility(View.INVISIBLE);
                                mLayoutCancelRequest.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerview() {
        mLayoutRequestFriend = findViewById(R.id.root_request_friend);
        mLayoutCancelRequest = findViewById(R.id.root_cancel_request_friend);
        mRecyclerView = findViewById(R.id.profile_other_list_post);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void initView() {
        mTxtPhoneNumber = findViewById(R.id.txt_phone_number_profile_user_other);
        mTxtDateBirth = findViewById(R.id.txt_date_birth_user_other);
        mTxtGender = findViewById(R.id.txt_gender_user_other);
        mTxtSendRequest = findViewById(R.id.txt_request_friend);
        mAvatar = findViewById(R.id.img_avatar_profile_other);
        mCoverPhoto = findViewById(R.id.img_cover_photo_profile_other);
        mTxtUsername = findViewById(R.id.txt_username_profile_other);
        mTxtDes = findViewById(R.id.txt_des_profile_other);
        CURRENT_STATE = "not_friend";
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("Post");
        mFriendRequestData = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        mFriendData = FirebaseDatabase.getInstance().getReference().child("Friends");
    }


    private void sendRequestFriend() {
        mFriendRequestData.child(mSenderUserID).child(mReceiverUserID).child("request_type").setValue("Sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFriendRequestData.child(mReceiverUserID).child(mSenderUserID).child("request_type").setValue("Received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mLayoutRequestFriend.setEnabled(true);
                                CURRENT_STATE = "request_sent";
                                mTxtSendRequest.setText("Hủy yêu cầu kết bạn");

                                mLayoutCancelRequest.setVisibility(View.INVISIBLE);
                                mLayoutCancelRequest.setEnabled(false);

                            } else {
                                Toast.makeText(getApplicationContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onClickRootChatprofileOther(View view) {
        Intent intent = new Intent(ProfileOtherUserActivity.this, ChatActivity.class);
        intent.putExtra("otherUserId", mReceiverUserID);
        intent.putExtra("currentUserId", mSenderUserID);
        startActivity(intent);
        finish();
    }


}
