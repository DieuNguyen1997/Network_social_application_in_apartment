package com.example.networksocialapplication.resident.homeapp.add_post;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.ImageAdapter;
import com.example.networksocialapplication.resident.homeapp.HomeActivity;
import com.example.networksocialapplication.models.Post;
import com.example.networksocialapplication.time_current.Time;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_CHOOSE_PHOTO = 111;
    private static final int REQUEST_IMAGE_CAPTURE = 123;
    private TextView mUsername;
    private CircleImageView mAvatar;
    private EditText mEdtContent;
    private Button mBtnChooseImage;
    private Button mBtnTakeImage;
    private Button mBtnPost;
    private Button mBtnChooseBackground;
    private RecyclerView mRecyclerView;
    private ImageAdapter mImageAdapter;
    private ArrayList<Bitmap> mListImage;

    private DatabaseReference mPostDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private StorageReference mPostImageReference;
    private StorageTask uploadTask;

    private Uri mUriImage;
    private static final String TAG = "AddPostActivity";
    private Time mTime;
    private String mSaveCurrentDate, mSaveCurrentTime, mPostRandomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        initFirebase();
        initToobar();
        initRecyclerview();
        initView();
        displayData();
    }

    private void displayData() {
        mUserDatabase.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue().toString();
                String avatar = dataSnapshot.child("avatar").getValue().toString();
                mUsername.setText(username);
                Picasso.with(AddPostActivity.this).load(avatar).fit().into(mAvatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initToobar() {
        Toolbar toolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_toolbar_add_post);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initRecyclerview() {
        mListImage = new ArrayList<>();
        mRecyclerView = findViewById(R.id.activity_add_post_list_image);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    }

    private void initView() {
        mTime = new Time();
        mAvatar = findViewById(R.id.img_avatar_add_post);
        mUsername = findViewById(R.id.txt_username_add_post);
        mEdtContent = findViewById(R.id.activity_add_post_edt_content);
        mBtnChooseImage = findViewById(R.id.btn_choose_image_add_post);
        mBtnTakeImage = findViewById(R.id.btn_take_image_add_post);
        mBtnPost = findViewById(R.id.btn_post_add_post);
        mBtnChooseBackground = findViewById(R.id.btn_choose_background_add_post);

        mBtnChooseImage.setOnClickListener(this);
        mBtnTakeImage.setOnClickListener(this);
        mBtnPost.setOnClickListener(this);
        mBtnChooseBackground.setOnClickListener(this);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("Post");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mPostImageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_image_add_post:
                chooseImageFromGallery();
                break;
            case R.id.btn_take_image_add_post:
                takePhoto();
                break;
            case R.id.btn_choose_background_add_post:
                setBackgroundForPost();
                break;
            case R.id.btn_post_add_post:
                addPostToFirebase();
                break;
        }
    }

    private void addPostToFirebase() {
        //luwu anh trong firebase
        storeImageToFirebseStorage();
        //luwu thoong tin bai viet
        saveInformationToDatabase();
    }

    private void saveInformationToDatabase() {
        final String content = mEdtContent.getText().toString();
        Post post = new Post(mCurrentUserId, mSaveCurrentDate, mSaveCurrentTime, content);
        mPostDatabase.child(mCurrentUserId + " " + mPostRandomName).setValue(post).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Luu noi dung bai viet thanh cong");
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                } else {
                    String mesasge = task.getException().getMessage();
                    Log.d(TAG, mesasge);
                }
            }
        });

        //get key post
        mPostDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String postKey = data.getKey();
                    mPostDatabase.child(postKey).child("postId").setValue(postKey).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "id success");
                            } else
                                Toast.makeText(AddPostActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void storeImageToFirebseStorage() {

        mSaveCurrentDate = mTime.getDateCurrent();
        mSaveCurrentTime = mTime.getTimeHourCurrent();

        mPostRandomName = mTime.getTimeCurrent();

        final StorageReference filePath = mPostImageReference.child("Post_Images").child(mCurrentUserId).child("Time" + mPostRandomName);

//        for (int i = 0; i < mListImage.size(); i++) {
//            Bitmap item = mListImage.get(i);
//            Uri uri = getImageUri(getApplicationContext(),item);
//            if (uri != null) {
//                filePath.child(uri.getLastPathSegment() + mPostRandomName + ".jpg").putFile(uri);
//                uploadTask = filePath.putFile(uri);
//                uploadTask.continueWithTask(new Continuation() {
//                    @Override
//                    public Object then(@NonNull Task task) throws Exception {
//                        if (!task.isSuccessful()) {
//                            throw task.getException();
//                        } else
//                            return filePath.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if (task.isSuccessful()) {
//
//                            Uri downLoadUri = task.getResult();
//                            String postUrl = downLoadUri.toString();
//
//                            mPostDatabase.child(mCurrentUserId + " " + mPostRandomName).child("imagePost").setValue(postUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(AddPostActivity.this, "Luu anh vafo post thanh cong", Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        String mesasge = task.getException().getMessage();
//                                        Log.d(TAG, mesasge);
//                                        Toast.makeText(AddPostActivity.this, "Luu anh ko thanh cong", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        } else {
//                            String mesasge = task.getException().getMessage();
//                            Log.d(TAG, mesasge);
//                            Toast.makeText(AddPostActivity.this, "Luu anh ko thanh cong", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        }

        if (mUriImage != null) {
            filePath.child(mUriImage.getLastPathSegment() + mPostRandomName + ".jpg").putFile(mUriImage);
            uploadTask = filePath.putFile(mUriImage);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    } else
                        return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downLoadUri = task.getResult();
                        String postUrl = downLoadUri.toString();

                        mPostDatabase.child(mCurrentUserId + " " + mPostRandomName).child("imagePost").setValue(postUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AddPostActivity.this, "Luu anh vafo post thanh cong", Toast.LENGTH_SHORT).show();
                                } else {
                                    String mesasge = task.getException().getMessage();
                                    Log.d(TAG, mesasge);
                                    Toast.makeText(AddPostActivity.this, "Luu anh ko thanh cong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        String mesasge = task.getException().getMessage();
                        Log.d(TAG, mesasge);
                        Toast.makeText(AddPostActivity.this, "Luu anh ko thanh cong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }


    private void setBackgroundForPost() {

    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void chooseImageFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_PHOTO);
    }

    //lay hinh anh vaf hien thi no trong IMageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
                mUriImage = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(mUriImage);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    mListImage.add(bitmap);
                    mImageAdapter = new ImageAdapter(AddPostActivity.this, mListImage);
                    mRecyclerView.setAdapter(mImageAdapter);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
//                mUriImage = getImageUri(getApplicationContext(), bitmap);
                mListImage.add(bitmap);
                mImageAdapter = new ImageAdapter(AddPostActivity.this, mListImage);
                mRecyclerView.setAdapter(mImageAdapter);
            }
        }
    }
}
