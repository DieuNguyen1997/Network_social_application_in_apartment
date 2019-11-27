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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPR_RIGHT = 1;

    private Context mContext;
    private List<Message> mMessages;
    private String mImgUrl;
    private String mCurrentUserID;

    public MessageAdapter(Context context, List<Message> messages, String imgUrl) {
        mContext = context;
        mMessages = messages;
        mImgUrl = imgUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPR_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mMessages != null) {
            Message message = mMessages.get(position);
            holder.mContentChat.setText(message.getContentChat());
            Glide.with(mContext).load(mImgUrl).into(holder.mImgAvatar);
            holder.mTimeChat.setText(message.getTimeChat());
            if (message.getImage() != null) {
                holder.mImageChat.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(message.getImage()).into(holder.mImageChat);
            } else {
                holder.mImageChat.setVisibility(View.GONE);
            }
            if (position == mMessages.size() - 1) {
                if (message.isSeen()) {
                    holder.mSeen.setText("Đã xem");
                } else
                    holder.mSeen.setText("Đã gửi");
            } else holder.mSeen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView mImgAvatar;
        public TextView mContentChat;
        public TextView mTimeChat;
        public TextView mSeen;
        public ImageView mImageChat;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImgAvatar = itemView.findViewById(R.id.img_avatar_item_chat);
            mContentChat = itemView.findViewById(R.id.txt_content_chat);
            mTimeChat = itemView.findViewById(R.id.txt_time_item_chat);
            mSeen = itemView.findViewById(R.id.txt_seen);
            mImageChat = itemView.findViewById(R.id.img_item_chat);
        }
    }

    @Override
    public int getItemViewType(int position) {
        mCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (mMessages.get(position).getSenderId().equals(mCurrentUserID)) {
            return MSG_TYPR_RIGHT;
        } else
            return MSG_TYPE_LEFT;
    }
}
