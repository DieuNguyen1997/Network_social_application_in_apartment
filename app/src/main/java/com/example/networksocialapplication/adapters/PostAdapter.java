package com.example.networksocialapplication.adapters;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.Post;
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

        final String postKey = holder.postDatabase.push().getKey();
        Log.d("adapter", postKey);
        if (mListPost != null) {
            Post post = mListPost.get(position);
            Picasso.with(mContext).load(post.getAvatar()).fit().into(holder.mAvatar);
            holder.mUserName.setText(post.getUsername());
            holder.mTimePosted.setText(post.getTimePosted());
            holder.mContentPost.setText(post.getContentPost());
            holder.mDatePosted.setText(post.getDatePosted());
            Glide.with(mContext).load(post.getImagePost()).into(holder.mImagePost);
            holder.setLikeButtonStatus(postKey);
            holder.mLayoutLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.checkLike = true;
                    holder.likeDataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (holder.checkLike == true) {
                                if (dataSnapshot.child(postKey).hasChild(holder.mCurrentUserID)) {
                                    holder.likeDataRef.child(postKey).child(holder.mCurrentUserID).removeValue();
                                    holder.checkLike = false;
                                } else {
                                    holder.likeDataRef.child(postKey).child(holder.mCurrentUserID).setValue(true);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
            holder.mLayoutComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.mTxtMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(mContext, holder.mTxtMenu);
                    popupMenu.inflate(R.menu.menu_item_post);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.item_delete_post:
                                    deletePost();
                                    break;
                                case R.id.item_edit_post:
                                    editPost();
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
        }
    }

    private void deletePost() {
    }
    private void editPost() {
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
        int mCountLike;
        private boolean checkLike = false;
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

        public void setUserName(String userName) {
            mUserName.setText(userName);
        }

        public void setTimePosted(String timePosted) {
            mTimePosted.setText(timePosted);
        }

        public void setDatePosted(String datePosted) {
            mDatePosted.setText(datePosted);
        }

        public void setContentPost(String contentPost) {
            mContentPost.setText(contentPost);
        }

        public void setImagePost(String imagePost) {
            Uri uri = Uri.parse(imagePost);
            mImagePost.setImageURI(uri);
        }

        public void setLikeButtonStatus(final String postKey) {
            likeDataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postKey).hasChild(mCurrentUserID)) {
                        mCountLike = (int) dataSnapshot.child(postKey).getChildrenCount();
                        mImgLike.setImageResource(R.drawable.ic_love_red);
                        mTxtCountLike.setText(Integer.toString(mCountLike));
                    } else {
                        mCountLike = (int) dataSnapshot.child(postKey).getChildrenCount();
                        mImgLike.setImageResource(R.drawable.ic_love_white);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
