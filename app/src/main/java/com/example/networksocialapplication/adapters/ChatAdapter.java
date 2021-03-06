package com.example.networksocialapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.user.chat.ChatActivity;
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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context mContext;
    private List<Resident> mUsers;
    private boolean mIsChat;

    private DatabaseReference mUserRef;
    private DatabaseReference mChatRef;
    private String mLastMessage;
    private String mCurrentUserId;

    public ChatAdapter(Context context, List<Resident> users, boolean isChat) {
        mContext = context;
        mUsers = users;
        mIsChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mUsers != null) {
            initFirebase();
            final Resident user = mUsers.get(position);
            getDataInformationUser(user.getResidentId(), holder.mAvatarChat, holder.mUsername);
            final String userOtherId = user.getResidentId();

            if (mIsChat){
                getLastMessage(userOtherId, holder.mTxtLastMessage);
            }
            if (mIsChat) {
                if (user.getStatus().equals("online")) {
                    holder.mImgOn.setVisibility(View.VISIBLE);
                    holder.mImgOff.setVisibility(View.GONE);
                } else {
                    holder.mImgOn.setVisibility(View.GONE);
                    holder.mImgOff.setVisibility(View.VISIBLE);
                }
            } else {
                holder.mImgOn.setVisibility(View.GONE);
                holder.mImgOff.setVisibility(View.GONE);
            }

            holder.mLineChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("otherUserId", userOtherId);
                    mContext.startActivity(intent);
                }
            });
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
                    Resident user = dataSnapshot.getValue(Resident.class);
                    if (isValidContextForGlide(mContext)){
                        Glide.with(mContext).load(user.getAvatar()).into(avatar);

                    }
                    username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
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

        public RelativeLayout mLineChat;
        public CircleImageView mAvatarChat;
        public TextView mUsername;
        public TextView mTxtLastMessage;
        public ImageView mImgOn;
        public ImageView mImgOff;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mAvatarChat = itemView.findViewById(R.id.img_avatar_item_list_chat);
            mUsername = itemView.findViewById(R.id.txt_username_item_list_chat);
            mTxtLastMessage = itemView.findViewById(R.id.txt_last_message_item_list_chat);
            mImgOn = itemView.findViewById(R.id.img_status_on_item_list_chat);
            mImgOff = itemView.findViewById(R.id.img_status_off_item_list_chat);
            mLineChat = itemView.findViewById(R.id.line_item_list_chat);
        }
    }
}
