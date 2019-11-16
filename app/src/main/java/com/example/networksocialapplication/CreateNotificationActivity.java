package com.example.networksocialapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networksocialapplication.admin.home_manager_fragment.HomeManagerFragment;
import com.example.networksocialapplication.admin.notification_manager_fragment.NotificationManagerFragment;
import com.example.networksocialapplication.models.NotificationFromManager;
import com.example.networksocialapplication.time_current.Time;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CreateNotificationActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE_IMAGE = 123 ;
    private Toolbar mToolbar;
    private ImageView mImage;
    private Button mChooseImage;
    private EditText mTitle;
    private EditText mContent;
    private Button mBtnCreate;

    private DatabaseReference mNotificationRef;
    private StorageReference mImageRef;

    private Time mTime;
    private Uri mImageUri;
    private UploadTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notification);
        
        initToolbar();
        initFirebase();
        initView();
    }

    private void initFirebase() {
        mNotificationRef = FirebaseDatabase.getInstance().getReference().child("NotificationFromManager");
        mImageRef = FirebaseStorage.getInstance().getReference().child("Image_Notify_From_Manager");
    }

    private void initView() {
        mTime = new Time();
        mImage = findViewById(R.id.img_create_notify);
        mChooseImage = findViewById(R.id.btn_choose_image_create_notify);
        mTitle = findViewById(R.id.edt_title_create_notify);
        mContent = findViewById(R.id.edt_content_create_notify);
        mBtnCreate =findViewById(R.id.btn_create_notify);

        mChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
            }
        });

        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification();
            }
        });
    }

    private void createNotification() {
        String title = mTitle.getText().toString();
        String content = mContent.getText().toString();
        String notifyId =  mNotificationRef.push().getKey();
        String date = mTime.getDateCurrent();
        String time = mTime.getTimeHourCurrent();

        saveImageToFirebase(notifyId);

        if (TextUtils.isEmpty(title)){
            mTitle.setError("Hãy nhập tiêu đề!!!");
        }
        if (TextUtils.isEmpty(content)){
            mContent.setError("Hãy nhập tiêu đề!!!");
        }
        else {
            NotificationFromManager notification = new NotificationFromManager(date,time,content,notifyId,title);
            mNotificationRef.child(notifyId).setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                   Toast.makeText(getApplicationContext(), "Tao thông báo thành công", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveImageToFirebase(final String notifyId) {
        if (mImageUri != null) {
            mImageRef.child(notifyId).putFile(mImageUri);
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

                        mNotificationRef.child(notifyId).child("imagePost").setValue(imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void chooseImageFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tạo thông báo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE && resultCode == RESULT_OK && data != null){
            mImageUri = data.getData();
            mImage.setImageURI(mImageUri);
        }
    }
}
