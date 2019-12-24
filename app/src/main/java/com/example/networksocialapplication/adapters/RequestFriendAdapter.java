package com.example.networksocialapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.models.User;
import com.example.networksocialapplication.resident.homeapp.profile_other_user.ProfileOtherUserActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestFriendAdapter extends RecyclerView.Adapter<RequestFriendAdapter.ViewHolder> {
    private Context mContext;
    private List<Resident> mUsers;

    private DatabaseReference mFriendRequestRef;
    private DatabaseReference mFriendRef;
    private String mCurrentUserId;

    public RequestFriendAdapter(Context context, List<Resident> users) {
        mContext = context;
        mUsers = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.item_request_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (mUsers != null) {
            initFirebase();
            Resident user = mUsers.get(position);
            holder.mUsername.setText(user.getUsername());
            Glide.with(mContext).load(user.getAvatar()).into(holder.mAvatarRquest);

            final String sendId = user.getResidentId();

            holder.mBtnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptRequestFriend(sendId, holder.mRootResponse, holder.mTxtConfirmFriend);
                }
            });

            holder.mBtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteRequestFriend(sendId);
                }
            });

            holder.mLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentToProfileOther(sendId);
                }
            });
        }
        else {
            Toast.makeText(mContext,"Không có lời mời kết bạn nào.Đừng chờ họ kết bạn với mình. Hãy chủ động nhé!", Toast.LENGTH_SHORT).show();

        }
    }

    private void intentToProfileOther(String sendId) {

        Intent intent = new Intent(mContext, ProfileOtherUserActivity.class);
        intent.putExtra("userID", sendId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private void deleteRequestFriend(final String sendId) {
        mFriendRequestRef.child(mCurrentUserId).child(sendId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFriendRequestRef.child(sendId).child(mCurrentUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void acceptRequestFriend(final String sendId, final LinearLayout rootRespone, final TextView confirmFriend) {
        mFriendRef.child(mCurrentUserId).child(sendId).setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFriendRef.child(sendId).child(mCurrentUserId).setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFriendRequestRef.child(mCurrentUserId).child(sendId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFriendRequestRef.child(sendId).child(mCurrentUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        confirmFriend.setVisibility(View.VISIBLE);
                                        rootRespone.setVisibility(View.GONE);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void initFirebase() {
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequest");
        mFriendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView mAvatarRquest;
        public TextView mUsername;
        public Button mBtnAccept;
        public Button mBtnDelete;
        public LinearLayout mRootResponse;
        public TextView mTxtConfirmFriend;
        public RelativeLayout mLine;
        public CircleImageView getAvatarRquest() {
            return mAvatarRquest;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLine = itemView.findViewById(R.id.item_user_request);
            mTxtConfirmFriend = itemView.findViewById(R.id.txt_result_friend);
            mAvatarRquest = itemView.findViewById(R.id.img_avatar_item_request_friend);
            mUsername = itemView.findViewById(R.id.txt_username_item_request_friend);
            mBtnAccept = itemView.findViewById(R.id.btn_confirm_item_request_friend);
            mBtnDelete = itemView.findViewById(R.id.btn_delete_item_request_friend);
            mRootResponse = itemView.findViewById(R.id.layout_root_response);
        }
    }
}
