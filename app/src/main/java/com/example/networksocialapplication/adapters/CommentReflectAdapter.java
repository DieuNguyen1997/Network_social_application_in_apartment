package com.example.networksocialapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.Comment;
import com.example.networksocialapplication.models.Manager;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.resident.homeapp.profile_other_user.ProfileOtherUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentReflectAdapter extends RecyclerView.Adapter<CommentViewHolder> {
    private Context mContext;
    private List<Comment> mComments;

    private String mCurrentUserID;
    private DatabaseReference mLikeRef;
    private DatabaseReference mCommentRef;
    private DatabaseReference mPostRef;

    public CommentReflectAdapter(Context context, List<Comment> comments) {
        mContext = context;
        mComments = comments;
    }

    public List<Comment> getComments() {
        return mComments;
    }

    public void setComments(List<Comment> comments) {
        mComments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.item_comment, null);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder holder, int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mLikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        mCommentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        holder.mLayoutCountLike.setVisibility(View.GONE);
        holder.mDelete.setVisibility(View.GONE);
        holder.mDelete.setVisibility(View.GONE);
        mCurrentUserID = firebaseUser.getUid();

        if (mComments != null) {
            final Comment comment = mComments.get(position);
            Log.d("adapter", comment.toString());
            final String userIdComment = comment.getUserID();
            displayInforUser(holder.mAvatar, holder.mUsername, userIdComment);
            holder.mTime.setText(comment.getTimeComment());
            holder.mContentComment.setText(comment.getContent());
            if (comment.getImageComment() != null) {
                holder.mImage.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(comment.getImageComment()).into(holder.mImage);
            }

            final String commentId = comment.getCommentID();
            isLikeComment(commentId, holder.mLike);
            displayLikeComment(commentId, holder.mCountLike, holder.mLayoutCountLike);
            holder.mUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!userIdComment.equals(mCurrentUserID)) {
                        Intent intent = new Intent(mContext, ProfileOtherUserActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("userID", userIdComment);
                        mContext.startActivity(intent);

                    }
                }
            });
            holder.mLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.mLike.getTag().equals("like")) {
                        mLikeRef.child(commentId).child(mCurrentUserID).setValue(true);
                    } else {
                        mLikeRef.child(commentId).child(mCurrentUserID).removeValue();
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }


    private void displayInforUser(final CircleImageView mImgAvatar, final TextView txtUsername, final String userId) {
        DatabaseReference managerRef = FirebaseDatabase.getInstance().getReference().child("Manager");
        managerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    Manager manager= data.getValue(Manager.class);
                    if (manager.getManagerId().equals(userId)){
                        if (isValidContextForGlide(mContext)) {
                            // Load image via Glide lib using context
                            Glide.with(mContext).load(manager.getAvatar()).into(mImgAvatar);
                        }
                        txtUsername.setText(manager.getUsername());
                    }else {
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                        userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String avatar = dataSnapshot.child("avatar").getValue().toString();
                                    String username = dataSnapshot.child("username").getValue().toString();
                                    if (isValidContextForGlide(mContext)) {
                                        // Load image via Glide lib using context
                                        Glide.with(mContext).load(avatar).into(mImgAvatar);
                                    }
                                    txtUsername.setText(username);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                    }
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

    private void isLikeComment(String commmentId, final TextView txtLike) {
        mLikeRef.child(commmentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(mCurrentUserID).exists()) {
                    txtLike.setTextColor(Color.RED);
                    txtLike.setTag("liked");
                } else {
                    txtLike.setTextColor(Color.GRAY);
                    txtLike.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayLikeComment(String commentId, final TextView mCountLike, final LinearLayout layout) {
        mLikeRef.child(commentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCountLike.setText(dataSnapshot.getChildrenCount() + "");
                if (Integer.parseInt(mCountLike.getText().toString()) == 0) {
                    layout.setVisibility(View.INVISIBLE);
                } else {
                    layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

