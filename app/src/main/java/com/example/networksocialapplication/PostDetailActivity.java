package com.example.networksocialapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.adapters.CommentAdapter;
import com.example.networksocialapplication.models.Comment;
import com.example.networksocialapplication.models.Post;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.time_current.Time;
import com.example.networksocialapplication.user.comment.CommentActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity implements OnClickListener {

    private String mPostId;

    private LinearLayout mLayoutLike;
    private ImageView mImgLike;
    private ImageView mAvatar;
    private TextView mUserName;
    private TextView mTimePosted;
    private TextView mContentPost;
    private ImageView mImagePost;
    private TextView mTxtCountLike;
    private TextView mTxtCountComment;

    private static final String TAG = "comment";
    private static final int REQUEST_CODE_CHOOSE_PHOTO_COMMENT = 112;
    private ImageView mSend;
    private ImageView mChoosePhoto;
    private ImageView mChooseSticker;
    private EditText mContentComment;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private List<Comment> mComments;
    private ImageView mImgComment;
    private ImageButton mDelete;
    private CoordinatorLayout mLayout;

    private DatabaseReference mPostRef;
    private DatabaseReference mCommentRef;
    private DatabaseReference mUserRef;
    private StorageReference mImageCommentRef;
    private DatabaseReference likeDataRef;
    private FirebaseAuth mAuth;
    private String mCurrentUserID;

    private CommentAdapter mCommentAdapter;
    private Uri mImageUri;
    private Time mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        initView();
        initFirebase();
        initToolbar();
        initRecyclerview();
        displayPost();
        displayListComment();
    }

    private void displayPost() {
        mPostRef.child(mPostId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                mTimePosted.setText("Đã đăng bài viết" + post.getDatePosted() + " lúc " + post.getTimePosted());
                mContentPost.setText(post.getContentPost());
                Glide.with(getApplicationContext()).load(post.getImagePost()).into(mImagePost);
                displayInforUser(post.getUserID());
                isLike(mPostId);
                numberLike(mPostId);
                displayNumberComment(mPostId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    public void displayNumberComment(String postKey) {
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(postKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTxtCountComment.setText(dataSnapshot.getChildrenCount() + " Bình luận");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void displayInforUser(String userId) {
        mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Resident user = dataSnapshot.getValue(Resident.class);
                    mUserName.setText(user.getUsername());
                    Glide.with(getApplicationContext()).load(user.getAvatar()).into(mAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addNotificationLike(String userID, String postID) {
        DatabaseReference notiRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(userID);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userID", userID);
        hashMap.put("text", "Đã like bài viết của bạn");
        hashMap.put("postID", postID);
        hashMap.put("isPost", true);
        notiRef.push().setValue(hashMap);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chi tiết bài đăng");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initFirebase() {
        mPostRef = FirebaseDatabase.getInstance().getReference().child("Post");
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();
        likeDataRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mCommentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(mPostId);
        mImageCommentRef = FirebaseStorage.getInstance().getReference().child("Image_Comments");
    }

    private void initView() {
        mPostId = getIntent().getStringExtra("postId");

        mTxtCountComment = findViewById(R.id.txt_count_comment_post_detail);
        mTxtCountLike = findViewById(R.id.txt_count_like_post_detail);
        mImgLike = findViewById(R.id.img_icon_like_post_detail);
        mLayoutLike = findViewById(R.id.layout_like_post_detail);
        mContentPost = findViewById(R.id.txt_content_post_detail);
        mAvatar = findViewById(R.id.img_avatar_post_detail);
        mUserName = findViewById(R.id.txt_username_post_detail);
        mTimePosted = findViewById(R.id.txt_time_post_detail);
        mContentPost = findViewById(R.id.txt_content_post_detail);
        mImagePost = findViewById(R.id.img_post_post_detail);

        mTime = new Time();
        mLayout = findViewById(R.id.layout_image_comment_post_detail);
        mDelete = findViewById(R.id.btn_delete_image_comment_post_detail);
        mImgComment = findViewById(R.id.image_comment_post_detail);
        mContentComment = findViewById(R.id.edt_content_comment_post_detail);
        mChooseSticker = findViewById(R.id.img_choose_sticker_comment_post_detail);
        mChoosePhoto = findViewById(R.id.img_choose_photo_comment_post_detail);
        mSend = findViewById(R.id.img_send_comment_post_detail);
        mChoosePhoto.setOnClickListener(this);
        mChooseSticker.setOnClickListener(this);
        mSend.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        mLayoutLike.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_choose_photo_comment_post_detail:
                chooseImageFromGallery();
                break;
            case R.id.img_choose_sticker_comment_post_detail:
                break;
            case R.id.img_send_comment_post_detail:
                sendComment();
                break;
            case R.id.btn_delete_image_comment_post_detail:
                deleteImage();
                break;
            case R.id.layout_like_post_detail:
                likePost();
                break;

        }
    }

    private void likePost() {
        if (mImgLike.getTag().equals("like")) {
            likeDataRef.child(mPostId).child(mCurrentUserID).setValue("true");
            addNotificationLike(mCurrentUserID, mPostId);
        } else {
            likeDataRef.child(mPostId).child(mCurrentUserID).removeValue();
        }
    }

    private void deleteImage() {
        mImageUri = null;
        mLayout.setVisibility(View.GONE);
    }

    private void chooseImageFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_PHOTO_COMMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO_COMMENT && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            mImgComment.setImageURI(mImageUri);
            mDelete.setVisibility(View.VISIBLE);
            mLayout.setVisibility(View.VISIBLE);
        }
    }
    private void initRecyclerview() {
        mRecyclerView = findViewById(R.id.recycler_view_list_comment_post_detail);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mComments = new ArrayList<>();
    }
    private void displayListComment() {
        Log.d(TAG, "displayListComment: ");
        mCommentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Comment comment = data.getValue(Comment.class);
                    Log.d(TAG, comment.toString());
                    mComments.add(comment);
                }
                mCommentAdapter = new CommentAdapter(getApplicationContext(), mComments);
                mCommentAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mCommentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendComment() {
        String content = mContentComment.getText().toString();
        if (TextUtils.isEmpty(content)) {
            mContentComment.setError("Nhập nội dung bình luận!!!");
        } else {
            String commentTime = mTime.getTimeCurrent();
            //Lưu ảnh vào trong storage
            final String commentId = mCommentRef.push().getKey();
            Comment comment = new Comment(commentId, mCurrentUserID, content, commentTime, mPostId);
            mCommentRef.child(commentId).setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mContentComment.setText("");
                    mLayout.setVisibility(View.GONE);
                    Log.d(TAG, "Add comment success");
                }
            });

            if (mImageUri != null) {
                mImageCommentRef.child(commentId).child(mImageUri.getLastPathSegment() + ".jpg").putFile(mImageUri);

                UploadTask uploadTask = mImageCommentRef.putFile(mImageUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        } else
                            return mImageCommentRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Uri downLoadUri = task.getResult();
                            String imageUrl = downLoadUri.toString();

                            mCommentRef.child(commentId).child("imageComment").setValue(imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mLayout.setVisibility(View.GONE);
                                }
                            });

                        } else {
                            String mesasge = task.getException().getMessage();
                            Log.d(TAG, mesasge);
                            Toast.makeText(PostDetailActivity.this, "Luu anh ko thanh cong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            addNotification(mCurrentUserID, mPostId);


        }
    }

    public void addNotification(String userID, String postID) {
        DatabaseReference notiRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(userID);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userID", userID);
        hashMap.put("text", "Đã bình luận bài viết của bạn");
        hashMap.put("postID", postID);
        hashMap.put("isPost", true);
        notiRef.push().setValue(hashMap);
    }

}
