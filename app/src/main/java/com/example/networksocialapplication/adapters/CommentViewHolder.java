package com.example.networksocialapplication.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.networksocialapplication.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout mLayoutCountLike;
    public CircleImageView mAvatar;
    public TextView mUsername;
    public TextView mContentComment;
    public TextView mLike;
    public TextView mReply;
    public TextView mTime;
    public View mView;
    public TextView mCountLike;
    public TextView mDelete;
    public ImageView mImage;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        mImage = mView.findViewById(R.id.img_item_comment);
        mDelete = mView.findViewById(R.id.txt_delete_item_comment);
        mLayoutCountLike = mView.findViewById(R.id.layout_count_like);
        mAvatar = mView.findViewById(R.id.img_avatar_item_comment);
        mUsername = mView.findViewById(R.id.txt_username_item_comment);
        mContentComment = mView.findViewById(R.id.txt_content_item_comment);
        mLike = mView.findViewById(R.id.txt_like_item_comment);
        mReply = mView.findViewById(R.id.txt_reply_item_comment);
        mTime = mView.findViewById(R.id.txt_time_item_comment);
        mCountLike = mView.findViewById(R.id.txt_count_like_item_comment);
    }

}
