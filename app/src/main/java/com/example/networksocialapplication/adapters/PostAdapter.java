package com.example.networksocialapplication.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.user.comment.CommentActivity;
import com.example.networksocialapplication.resident.homeapp.HomeActivity;
import com.example.networksocialapplication.models.Post;
import com.example.networksocialapplication.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

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
        View view = layoutInflater.inflate(R.layout.fragment_home_item_list_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        if (mListPost != null) {
            final Post post = mListPost.get(position);
            final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            holder.displayInforUser(mContext, post.getUserID());
            holder.mTimePosted.setText(post.getTimePosted());
            holder.mContentPost.setText(post.getContentPost());
            holder.mDatePosted.setText(post.getDatePosted());
            if (post.getImagePost() != null){
                holder.mImagePost.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(post.getImagePost()).into(holder.mImagePost);
            }
            final String postKey = post.getPostId();
            holder.isLike(postKey);
            holder.numberLike(postKey);
            holder.displayNumberComment(postKey);

            holder.mLayoutLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.mImgLike.getTag().equals("like")) {
                        holder.likeDataRef.child(postKey).child(holder.mCurrentUserID).setValue("true");
                        if (!post.getUserID().equals(currentUser)){
                            holder.addNotification(currentUser,postKey, post.getUserID());
                        }
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
                    intent.putExtra("userPost", post.getUserID());
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
                                        holder.deletePost(mContext, postKey);
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
        else {
            Toast.makeText(mContext,"Bạn chưa có bài đăng nào. Họ không đăng thì mình đăng.", Toast.LENGTH_SHORT).show();
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
        DatabaseReference userRef;
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
            userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        }

        public void setAvatar(String avatar) {
            Uri uri = Uri.parse(avatar);
            mAvatar.setImageURI(uri);
        }

        public void deletePost(final Context context, final String postKey) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Xóa bài đăng");
            builder.setMessage("Bạn có chắc chắn muốn xóa bài đăng này?");
            builder.setIcon(R.drawable.ic_delete_bin);
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (postKey != null){
                        postDatabase.child(postKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

        public void isLike(String postKey) {
            if (postKey != null){
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
        }

        public void numberLike(String postKey) {

            if (postKey != null){
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
        }

        public void displayNumberComment(String postKey){
            if (postKey != null){
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

        public void displayInforUser(final Context context, String userId){
            userRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Resident user = dataSnapshot.getValue(Resident.class);
                        mUserName.setText(user.getUsername());
                        if (isValidContextForGlide(context)){
                            Glide.with(context).load(user.getAvatar()).into(mAvatar);

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

        public void addNotification(String userID, String postID, String userPost){
            DatabaseReference notiRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(userPost);
            String notifyId = notiRef.push().getKey();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userID", userID);
            hashMap.put("text", "Đã like bài viết của bạn");
            hashMap.put("postID", postID);
            hashMap.put("isPost", true);
            hashMap.put("notifyID", notifyId);
            notiRef.child(notifyId).setValue(hashMap);
        }

    }

}
