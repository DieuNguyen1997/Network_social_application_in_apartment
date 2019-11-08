package com.example.networksocialapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.Message;
import com.example.networksocialapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserStatusAdapter extends RecyclerView.Adapter<UserStatusAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUsers;

    private DatabaseReference mUserRef;
    private DatabaseReference mChatRef;
    private String mLastMessage;
    private String mCurrentUserId;

    public UserStatusAdapter(Context context, List<User> users) {
        mContext = context;
        mUsers = users;
    }

    @NonNull
    @Override
    public UserStatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_chat_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserStatusAdapter.ViewHolder holder, int position) {
        if (mUsers != null) {
            initFirebase();
            final User user = mUsers.get(position);
            getDataInformationUser(user.getUserID(), holder.mAvatarChat, holder.mUsername);
            if (user.getStatus().equals("online")) {
                holder.mImgOn.setVisibility(View.VISIBLE);
            } else {
                mUsers.remove(user);
            }
        }
    }

    private void initFirebase() {
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mChatRef = FirebaseDatabase.getInstance().getReference().child("Chats");
    }

    private void getDataInformationUser(String userId, final CircleImageView avatar, final TextView username) {
        mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    Glide.with(mContext).load(user.getAvatar()).into(avatar);
                    username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLastMessage(final String userId, final TextView lastmessage) {
        mChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    if (message.getReceivedId().equals(mCurrentUserId) && message.getSenderId().equals(userId) ||
                            message.getReceivedId().equals(userId) && message.getSenderId().equals(mCurrentUserId)) {
                        mLastMessage = message.getContentChat();
                        lastmessage.setText(mLastMessage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView mAvatarChat;
        public TextView mUsername;
        public ImageView mImgOn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAvatarChat = itemView.findViewById(R.id.img_avatar_item_user_status);
            mUsername = itemView.findViewById(R.id.txt_username_item_user_status);
            mImgOn = itemView.findViewById(R.id.img_status_on_item_user_status);
        }
    }

}
