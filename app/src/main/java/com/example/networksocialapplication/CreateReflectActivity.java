package com.example.networksocialapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.networksocialapplication.models.Reflect;
import com.example.networksocialapplication.time_current.Time;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class CreateReflectActivity extends AppCompatActivity implements View.OnClickListener,StatusReflect {

    private static final int REQUEST_CODE_CHOOSE_IMAGE = 116;
    private LinearLayout mRootAddImage;
    private ImageView mImage;
    private Button mBtnAll;
    private Button mBtnProtect;
    private Button mBtnTechnology;
    private Button mBtnToire;
    private TextInputEditText mEdtTitle;
    private TextInputEditText mEdtContent;
    private Button mBtnSend;
    private Toolbar mToolbar;

    private String mFieldReflect;

    private DatabaseReference mReflectRef;
    private String mCurrentUserId;
    private Uri mImageUri;
    private StorageReference mImageRef;
    private DatabaseReference mManagerRef;
    private Time mTime;
    private UploadTask mUploadTask;
    private String mManagerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_reflect);

        initToolbar();
        initFirebase();
//        initView();
        initView();

    }

    private void initView() {
        mTime = new Time();
        mRootAddImage = findViewById(R.id.root_add_image_create_reflect);
        mImage = findViewById(R.id.img_create_reflect);
        mBtnAll = findViewById(R.id.btn_field_all);
        mBtnProtect = findViewById(R.id.btn_field_protect);
        mBtnTechnology = findViewById(R.id.btn_field_techno);
        mBtnToire = findViewById(R.id.btn_field_toire);
        mEdtTitle = findViewById(R.id.edt_title_create_reflect);
        mEdtContent = findViewById(R.id.edt_content_reflect);
        mBtnSend = findViewById(R.id.btn_send_reflect);

        mBtnAll.setOnClickListener(this);
        mBtnProtect.setOnClickListener(this);
        mBtnTechnology.setOnClickListener(this);
        mBtnToire.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
        mRootAddImage.setOnClickListener(this);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tạo phản ánh");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initFirebase() {
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mReflectRef = FirebaseDatabase.getInstance().getReference().child("Reflect");
        mManagerRef = FirebaseDatabase.getInstance().getReference().child("Manager");
        mImageRef = FirebaseStorage.getInstance().getReference().child("Image_Reflect");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_field_all:
                mFieldReflect = mBtnAll.getText().toString();
                mBtnAll.setBackgroundResource(R.drawable.bg_btn);
                mBtnProtect.setBackgroundResource(R.drawable.bg_btn_reflect);
                mBtnToire.setBackgroundResource(R.drawable.bg_btn_reflect);
                mBtnTechnology.setBackgroundResource(R.drawable.bg_btn_reflect);
                break;
            case R.id.btn_field_protect:
                mFieldReflect = mBtnProtect.getText().toString();
                mBtnProtect.setBackgroundResource(R.drawable.bg_btn);
                mBtnAll.setBackgroundResource(R.drawable.bg_btn_reflect);
                mBtnToire.setBackgroundResource(R.drawable.bg_btn_reflect);
                mBtnTechnology.setBackgroundResource(R.drawable.bg_btn_reflect);
                break;
            case R.id.btn_field_techno:
                mFieldReflect = mBtnTechnology.getText().toString();
                mBtnTechnology.setBackgroundResource(R.drawable.bg_btn);
                mBtnProtect.setBackgroundResource(R.drawable.bg_btn_reflect);
                mBtnToire.setBackgroundResource(R.drawable.bg_btn_reflect);
                mBtnAll.setBackgroundResource(R.drawable.bg_btn_reflect);
                break;
            case R.id.btn_field_toire:
                mFieldReflect = mBtnToire.getText().toString();
                mBtnToire.setBackgroundResource(R.drawable.bg_btn);
                mBtnProtect.setBackgroundResource(R.drawable.bg_btn_reflect);
                mBtnAll.setBackgroundResource(R.drawable.bg_btn_reflect);
                mBtnTechnology.setBackgroundResource(R.drawable.bg_btn_reflect);
                break;
            case R.id.btn_send_reflect:
                sendReflect();
                break;
            case R.id.root_add_image_create_reflect:
                chooseImageFromGallery();
                break;
        }
    }

    private void sendReflect() {
        final String title = mEdtTitle.getText().toString();
        final String content = mEdtContent.getText().toString();
        final String idReflect = mReflectRef.push().getKey();

        saveImageToFirebase(idReflect);

        if (TextUtils.isEmpty(title)) {
            mEdtTitle.setError("Tiêu đề không được bỏ trống");
        }
        if (TextUtils.isEmpty(content)) {
            mEdtContent.setError("Nội dung phản ánh không được bỏ trống");
        }
        if (mFieldReflect == null) {
            Toast.makeText(this, "Hãy chọn một lĩnh vực cần phản ánh", Toast.LENGTH_SHORT).show();
        } else {

            Reflect reflect = new Reflect(mCurrentUserId, mTime.getDateCurrent(), mTime.getTimeHourCurrent(), content, title, mFieldReflect, idReflect,WAIT_PROGRESS);
            mReflectRef.child(idReflect).setValue(reflect).addOnSuccessListener(new OnSuccessListener<Void>() {

                //            mReflectRef.child(idReflect).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Tạo phản ánh thành công. Chờ ban quản lý phê duyệt", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(getApplicationContext(), ReflectActivity.class));
                }
            });

            addNotificationToManager(idReflect);
        }
    }

    private void addNotificationToManager(String reflectId) {
        DatabaseReference notiRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(reflectId);
        String notifyId = notiRef.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userID", mCurrentUserId);
        hashMap.put("text", "Đã like bài viết của bạn");
        hashMap.put("postID", reflectId);
        hashMap.put("isPost", true);
        hashMap.put("notifyID", notifyId);
        notiRef.child(notifyId).setValue(hashMap);
    }


    private void chooseImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            mImage.setImageURI(mImageUri);
        }
    }

    private void saveImageToFirebase(final String idReflect) {

        if (mImageUri != null) {
            mImageRef.child(idReflect).putFile(mImageUri);
            mUploadTask = mImageRef.putFile(mImageUri);
            mUploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    } else {
                        return mImageRef.getDownloadUrl();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        String imageUrl = uri.toString();

                        mReflectRef.child(idReflect).child("imagePost").setValue(imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Lưu ảnh thành công", Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
