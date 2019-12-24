package com.example.networksocialapplication.user.comment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.CommentAdapter;
import com.example.networksocialapplication.models.Comment;
import com.example.networksocialapplication.time_current.Time;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class CommentActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "comment";
    private static final int REQUEST_CODE_CHOOSE_PHOTO_COMMENT = 112;
    private CircleImageView mAvatar;
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

    private String mPostId;
    private String mCurrentUserId;
    private CommentAdapter mCommentAdapter;
    private Uri mImageUri;
    private Time mTime;
    private String mUserPost;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initToolbar();
        initView();
        initRecyclerview();
        initFirebase();
        displayAvatar();
        displayListComment();
    }

    private void initRecyclerview() {
        mRecyclerView = findViewById(R.id.recycler_view_list_comment);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mComments = new ArrayList<>();
    }

    private void displayAvatar() {
        mUserRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String avatar = dataSnapshot.child("avatar").getValue().toString();
                    Glide.with(getApplicationContext()).load(avatar).into(mAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Bình luận");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        mTime = new Time();
        mLayout = findViewById(R.id.layout_image_comment);
        mDelete = findViewById(R.id.btn_delete_image_comment);
        mImgComment = findViewById(R.id.image_comment);
        mAvatar = findViewById(R.id.img_avatar_comment_activity);
        mContentComment = findViewById(R.id.edt_content_comment);
        mChooseSticker = findViewById(R.id.img_choose_sticker_comment_activity);
        mChoosePhoto = findViewById(R.id.img_choose_photo_comment_activity);
        mSend = findViewById(R.id.img_send_comment);

        mChoosePhoto.setOnClickListener(this);
        mChooseSticker.setOnClickListener(this);
        mSend.setOnClickListener(this);
        mDelete.setOnClickListener(this);

        Intent intent = getIntent();

        mPostId = intent.getStringExtra("postId");
        mCurrentUserId = intent.getStringExtra("currentUserId");
        mUserPost = intent.getStringExtra("userPost");
//
        if (mImageUri == null){
            mLayout.setVisibility(View.GONE);
        }
    }

    private void initFirebase() {
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mCommentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(mPostId);
        mImageCommentRef = FirebaseStorage.getInstance().getReference().child("Image_Comments");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_choose_photo_comment_activity:
                chooseImageFromGallery();
                break;
            case R.id.img_choose_sticker_comment_activity:
                break;
            case R.id.img_send_comment:
                sendComment();
                break;
            case R.id.btn_delete_image_comment:
                deleteImage();
                break;
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
        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO_COMMENT && resultCode == RESULT_OK && data!= null){
            mImageUri = data.getData();
            mImgComment.setImageURI(mImageUri);
            mLayout.setVisibility(View.VISIBLE);
            mDelete.setVisibility(View.VISIBLE);
        }
    }

    private void displayListComment() {
        Log.d(TAG, "displayListComment: ");
        mCommentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()){
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
        if (TextUtils.isEmpty(content)){
            mContentComment.setError("Nhập nội dung bình luận!!!");
        }else {
            String commentTime = mTime.getTimeCurrent();
            //Lưu ảnh vào trong storage
            final String commentId = mCommentRef.push().getKey();
            Comment comment = new Comment(commentId,mCurrentUserId,content,commentTime, mPostId);
            mCommentRef.child(commentId).setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mContentComment.setText("");
                    saveImageInFirebase(commentId);
                    mLayout.setVisibility(View.GONE);
                    Log.d(TAG, "Add comment success");
                }
            });
            if (!mCurrentUserId.equals(mUserPost)){
                addNotification(mCurrentUserId, mPostId, mUserPost);
            }
        }
    }
    private  void saveImageInFirebase(final String commentId){
        if (mImageUri != null){
            final StorageReference databasse = mImageCommentRef.child(commentId).child(mImageUri.getLastPathSegment()+".jpg");

            UploadTask uploadTask = databasse.putFile(mImageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    } else
                        return databasse.getDownloadUrl();
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
                        Toast.makeText(CommentActivity.this, "Luu anh ko thanh cong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    public void addNotification(String userID, String postID, String userPost){
        if (userPost!= null && !mCurrentUserId.equals(userPost)){
            DatabaseReference notiRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(userPost);
            String notifyId = notiRef.push().getKey();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userID", userID);
            hashMap.put("text", "Đã bình luận bài viết của bạn");
            hashMap.put("postID", postID);
            hashMap.put("isPost", true);
            hashMap.put("notifyID", notifyId);
            notiRef.child(notifyId).setValue(hashMap);
        }

    }
}
