package com.example.networksocialapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.database.UserDatabase;
import com.example.networksocialapplication.models.Notification;
import com.example.networksocialapplication.models.Post;
import com.example.networksocialapplication.resident.homeapp.profile_other_user.ProfileOtherUserActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notification> mNotifications;


    public NotificationAdapter(Context context, List<Notification> notifications) {
        mContext = context;
        mNotifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_notification_from_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (mNotifications !=  null){
            final Notification notification = mNotifications.get(position);

            UserDatabase userDatabase = new UserDatabase(mContext);
            userDatabase.getAvatarAndUsernameUser(notification.getUserID(), holder.mTxtUsername, holder.mImgAvatar);
            holder.mCommentNotify.setText(notification.getText());

            if (notification.isPost()){
                holder.mImgPost.setVisibility(View.INVISIBLE);
                displayImagePost(notification.getPostID(), holder.mImgPost);
            }else {
                holder.mImgPost.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notification.isPost()){
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("save", Context.MODE_PRIVATE).edit();
                        editor.putString("postid", notification.getPostID());
                        editor.apply();

                        Intent intent = new Intent(mContext, ProfileOtherUserActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }else {
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("save", Context.MODE_PRIVATE).edit();
                        editor.putString("profileid", notification.getUserID());
                        editor.apply();

                        Intent intent = new Intent(mContext, ProfileOtherUserActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                }
            });

        }
    }

    private void displayImagePost(final String postID, final ImageView imgPost) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Post").child(postID);
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    Post post = dataSnapshot.getValue(Post.class);
                    Glide.with(mContext).load(post.getImagePost()).into(imgPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView mImgAvatar;
        public TextView mTxtUsername;
        public TextView mCommentNotify;
        public ImageView mImgPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mImgAvatar = itemView.findViewById(R.id.img_avatar_notify_friend);
            mTxtUsername = itemView.findViewById(R.id.txt_username_item_notification);
            mCommentNotify = itemView.findViewById(R.id.txt_comment_item_notification);
            mImgPost = itemView.findViewById(R.id.img_post_image_item_notification);
        }
    }
}
