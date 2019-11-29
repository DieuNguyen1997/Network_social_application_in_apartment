package com.example.networksocialapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.adapters.CommentAdapter;
import com.example.networksocialapplication.adapters.CommentEventAdapter;
import com.example.networksocialapplication.models.Comment;
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


public class CommentEventFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "comment";
    private static final int REQUEST_CODE_CHOOSE_PHOTO_COMMENT = 112;
    private CircleImageView mAvatar;
    private ImageView mSend;
    private ImageView mChoosePhoto;
    private EditText mContentComment;
    private RecyclerView mRecyclerView;
    private List<Comment> mComments;
    private ImageView mImgComment;
    private ImageButton mDelete;
    private CoordinatorLayout mLayout;

    private String mCurrentUserId;
    private DatabaseReference mCommentRef;
    private DatabaseReference mUserRef;
    private StorageReference mImageCommentRef;

    private String mEventId;
    private CommentEventAdapter mCommentAdapter;
    private Uri mImageUri;
    private Time mTime;
    private boolean mIsAttached;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment_event, container, false);
        initView(view);
        initRecyclerview(view);
        initFirebase();
        displayAvatar();
        displayListComment();
        return view;
    }

    private void initRecyclerview(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_list_comment_event);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mComments = new ArrayList<>();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mIsAttached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mIsAttached = false;
    }

    private void displayAvatar() {
        mUserRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String avatar = dataSnapshot.child("avatar").getValue().toString();
                    if (mIsAttached){
                        Glide.with(getActivity()).load(avatar).into(mAvatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initView(View view) {
        mEventId = getActivity().getIntent().getStringExtra("eventId");
        mTime = new Time();
        mLayout = view.findViewById(R.id.layout_image_comment_event);
        mDelete = view.findViewById(R.id.btn_delete_image_comment_event);
        mImgComment = view.findViewById(R.id.image_comment_event);
        mAvatar = view.findViewById(R.id.img_avatar_comment_event);
        mContentComment = view.findViewById(R.id.edt_content_comment_event);
        mChoosePhoto = view.findViewById(R.id.img_choose_photo_comment_event);
        mSend = view.findViewById(R.id.img_send_comment_event);

        mChoosePhoto.setOnClickListener(this);
        mSend.setOnClickListener(this);
        mDelete.setOnClickListener(this);

        if (mImageUri == null) {
            mLayout.setVisibility(View.GONE);
        }
    }

    private void initFirebase() {
        mCurrentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mCommentRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(mEventId);
        mImageCommentRef = FirebaseStorage.getInstance().getReference().child("Image_Comments");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_choose_photo_comment_event:
                chooseImageFromGallery();
                break;
            case R.id.img_send_comment_event:
                sendComment();
                break;
            case R.id.btn_delete_image_comment_event:
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO_COMMENT && resultCode == getActivity().RESULT_OK && data != null) {
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
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Comment comment = data.getValue(Comment.class);
                    Log.d(TAG, comment.toString());
                    mComments.add(comment);
                }
                mCommentAdapter = new CommentEventAdapter(getActivity(), mComments);
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
            Comment comment = new Comment(commentId, mCurrentUserId, content, commentTime, mEventId);
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
                            Toast.makeText(getActivity(), "Luu anh ko thanh cong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }
    }


}
