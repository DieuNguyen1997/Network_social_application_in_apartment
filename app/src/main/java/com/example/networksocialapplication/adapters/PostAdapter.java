package com.example.networksocialapplication.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.comment.CommentActivity;
import com.example.networksocialapplication.homeapp.HomeActivity;
import com.example.networksocialapplication.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    public Context mContext;
    private ArrayList<Post> mListPost;

    public PostAdapter(Context context, ArrayList<Post> listPost) {
        mContext = context;
        mListPost = listPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.fragment_home_item_list_post, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        if (mListPost != null) {
            final Post post = mListPost.get(position);
            holder.mUserName.setText(post.getUsername());
            holder.mTimePosted.setText(post.getTimePosted());
            holder.mContentPost.setText(post.getContentPost());
            holder.mDatePosted.setText(post.getDatePosted());
            Glide.with(mContext).load(post.getImagePost()).into(holder.mImagePost);
            Glide.with(mContext).load(post.getAvatar()).into(holder.mAvatar);

            final String postKey = post.getPostId();
            holder.isLike(postKey);
            holder.numberLike(postKey);
            holder.displayNumberComment(postKey);

//            holder.setLikeButtonStatus(postKey);

            holder.mLayoutLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.mImgLike.getTag().equals("like")) {
                        holder.likeDataRef.child(postKey).child(holder.mCurrentUserID).setValue("true");
                    } else {
                        holder.likeDataRef.child(postKey).child(holder.mCurrentUserID).removeValue();
                    }
                }
            });
            holder.mLayoutComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CommentActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("postId",postKey);
                    intent.putExtra("currentUserId", holder.mCurrentUserID);
                    mContext.startActivity(intent);
                }
            });
            if (post.getUserID().equals(holder.mCurrentUserID)) {
                holder.mTxtMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PopupMenu popupMenu = new PopupMenu(mContext, holder.mTxtMenu);
                        popupMenu.inflate(R.menu.menu_item_post);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.item_delete_post:
                                        holder.deletePost(mContext, post);
                                        break;
                                    case R.id.item_edit_post:
                                        holder.editPost(post);
                                        break;
                                    default:
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            } else {
                holder.mTxtMenu.setVisibility(View.INVISIBLE);
            }

        }
    }

    @Override
    public int getItemCount() {
        return mListPost.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        DatabaseReference likeDataRef;
        DatabaseReference postDatabase;
        FirebaseAuth mAuth;
        String mCurrentUserID;
        LinearLayout mLayoutLike;
        LinearLayout mLayoutComment;
        ImageView mImgLike;
        ImageView mAvatar;
        TextView mUserName;
        TextView mTimePosted;
        TextView mDatePosted;
        TextView mContentPost;
        ImageView mImagePost;
        TextView mTxtCountLike;
        TextView mTxtCountComment;
        TextView mTxtMenu;

        //        GridView mGridView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            mTxtCountComment = mView.findViewById(R.id.txt_count_comment_item_post);
            mTxtCountLike = mView.findViewById(R.id.txt_count_like_item_post);
            mImgLike = itemView.findViewById(R.id.img_icon_like_item_post);
            mLayoutLike = itemView.findViewById(R.id.layout_like_item_post);
            mLayoutComment = itemView.findViewById(R.id.layout_comment_item_post);
            mContentPost = itemView.findViewById(R.id.txt_content_post_list);
            mAvatar = itemView.findViewById(R.id.avatar_item_post_frag_home);
            mUserName = itemView.findViewById(R.id.txt_username_item_post_frag_home);
            mTimePosted = itemView.findViewById(R.id.txt_time_item_post);
            mDatePosted = itemView.findViewById(R.id.txt_date_item_post);
            mContentPost = itemView.findViewById(R.id.txt_content_post_list);
            mImagePost = itemView.findViewById(R.id.img_post_item_post);
            mTxtMenu = itemView.findViewById(R.id.txt_menu_post);
//            mGridView  = itemView.findViewById(R.id.gridView_list_image_post);
            postDatabase = FirebaseDatabase.getInstance().getReference().child("Post");
            mAuth = FirebaseAuth.getInstance();
            mCurrentUserID = mAuth.getCurrentUser().getUid();
            likeDataRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        }

        public void setAvatar(String avatar) {
            Uri uri = Uri.parse(avatar);
            mAvatar.setImageURI(uri);
        }

        public void deletePost(final Context context, final Post post) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Xóa bài đăng");
            builder.setMessage("Bạn có chắc chắn muốn xóa bài đăng này?");
            builder.setIcon(R.drawable.ic_delete_bin);
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    postDatabase.child(post.getPostId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(context, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        }
                    });

                }
            });
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }

        public void editPost(Post post) {
        }

        public void isLike(String postKey) {
            likeDataRef.child(postKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(mCurrentUserID).exists()) {
                        mImgLike.setImageResource(R.drawable.ic_love_red);
                        mImgLike.setTag("liked");
                    } else {
                        mImgLike.setImageResource(R.drawable.ic_love_white);
                        mImgLike.setTag("like");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        public void numberLike(String postKey) {
            likeDataRef.child(postKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mTxtCountLike.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void displayNumberComment(String postKey){
            DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(postKey);
            commentRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mTxtCountComment.setText(dataSnapshot.getChildrenCount()+" Bình luận");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}
