package com.example.networksocialapplication.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.activities.HomeFragment;
import com.example.networksocialapplication.comment.CommentActivity;
import com.example.networksocialapplication.homeapp.HomeActivity;
import com.example.networksocialapplication.models.Comment;
import com.example.networksocialapplication.models.Post;
import com.example.networksocialapplication.profile.ProfileFragment;
import com.example.networksocialapplication.profile_other_user.ProfileOtherUserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mComments;

    private String mCurrentUserID;
    private DatabaseReference mLikeRef;
    private DatabaseReference mCommentRef;
    private DatabaseReference mPostRef;

    public CommentAdapter(Context context, List<Comment> comments) {
        mContext = context;
        mComments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.item_comment,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mLikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        mCommentRef = FirebaseDatabase.getInstance().getReference().child("Comments");
        mPostRef = FirebaseDatabase.getInstance().getReference().child("Post");
        holder.mLayoutCountLike.setVisibility(View.GONE);
        holder.mDelete.setVisibility(View.GONE);
        mCurrentUserID = firebaseUser.getUid();
        if (mComments != null) {
            final Comment comment = mComments.get(position);
            Log.d("adapter", comment.toString());
            final String userIdComment = comment.getUserID();
            displayInforUser(holder.mAvatar, holder.mUsername, userIdComment);
            holder.mTime.setText(comment.getTimeComment());
            holder.mContentComment.setText(comment.getContent());
            Glide.with(mContext).load(comment.getImageComment()).into(holder.mImage);

            final String commentId = comment.getCommentID();
            final String postId = comment.getPostID();
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


            mPostRef.child(postId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userIdPost = dataSnapshot.child("userID").getValue().toString();
                    if (userIdPost.equals(mCurrentUserID)) {
                        holder.mDelete.setVisibility(View.VISIBLE);
                        holder.mDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                deleteComment(postId, commentId);
                            }
                        });
                    } else {
                        if (userIdComment.equals(mCurrentUserID)) {
                            holder.mDelete.setVisibility(View.VISIBLE);
                            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    deleteComment(postId, commentId);
                                }
                            });
                        } else {
                            holder.mDelete.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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

        public ViewHolder(@NonNull View itemView) {
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

    private void displayInforUser(final CircleImageView mImgAvatar, final TextView txtUsername, String userId) {
        DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String avatar = dataSnapshot.child("avatar").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();

                    Glide.with(mContext).load(avatar).into(mImgAvatar);
                    txtUsername.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    public void deleteComment(final String postId, final String commentId) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle("Xóa bình luận");
//        builder.setIcon(R.drawable.ic_delete_bin);
//        builder.setMessage("Bạn có chắc chắn muốn xóa bình luận");
//        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
                mCommentRef.child(postId).child(commentId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mCommentRef.child(postId).child(commentId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mLikeRef.child(commentId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(mContext, CommentActivity.class);
                                        intent.putExtra("postId",postId);
                                        intent.putExtra("currentUserId",mCurrentUserID);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mContext.startActivity(intent);
                                    }
                                });

                            }
                        });

                    }
                });
//            }
//        });
//        builder.setPositiveButton("Không", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(mContext, CommentActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("postId",postId);
//                intent.putExtra("currentUserId",mCurrentUserID);
//                mContext.startActivity(intent);
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
    }

}
