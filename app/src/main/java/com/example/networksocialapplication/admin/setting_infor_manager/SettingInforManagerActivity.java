package com.example.networksocialapplication.admin.setting_infor_manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networksocialapplication.R;
import com.example.networksocialapplication.admin.HomeManagerActivity;
import com.example.networksocialapplication.models.Manager;
import com.example.networksocialapplication.resident.homeapp.setting_info_profile.SettingInformationProfileActivity;
import com.example.networksocialapplication.resident.homeapp.HomeActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingInforManagerActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int GALLERY_PICK_AVATAR= 113;
    private static final int GALLERY_PICK_COVER = 114;

    private ImageView mImgCoverPhoto;
    private CircleImageView mImgAvatar;

    private Button mBtnSave;
    private ImageButton mBtnCameraCoverPhoto;
    private ImageButton mBtnCameraAvatar;

    private TextView mTxtUsername;
    private EditText mEdtHotline;
    private EditText mEdtLocation;
    private EditText mEdtDes;
    private ProgressDialog mProgessBar;

    private StorageReference mAvatarDatabase;
    private StorageReference mCoverPhotoDatabase;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference mUserDatabase;
    private Uri mImageUri;
    private StorageTask mUploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_infor_manager);

        initFirebase();
        initView();

    }

    public void intentSettingInfomationProfile() {
        Intent intent = new Intent(getApplicationContext(), SettingInformationProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Manager").child(currentUserID);
        mAvatarDatabase = FirebaseStorage.getInstance().getReference().child("Avatar");
        mCoverPhotoDatabase = FirebaseStorage.getInstance().getReference().child("CoverPhoto");
    }

    private void initView() {
        mTxtUsername = findViewById(R.id.txt_username_setting_profile_manager);
        mEdtDes = findViewById(R.id.edt_des_setting_profile_manager);
        mEdtHotline = findViewById(R.id.edt_hotline_setting_infor_manager);
        mEdtLocation = findViewById(R.id.edt_location_setting_infor_manager);
        mProgessBar = new ProgressDialog(this);
        mImgAvatar = findViewById(R.id.img_avatar_setting_profile_manager);
        mImgCoverPhoto = findViewById(R.id.img_cover_photo_setting_profile_manager);
        mBtnSave = findViewById(R.id.btn_save_setting_profile_manaager);
        mBtnCameraCoverPhoto = findViewById(R.id.btn_camera_avatar_setting_profile_manager);
        mBtnCameraAvatar = findViewById(R.id.btn_camera_cover_photo_setting_profile_manager);

        mBtnSave.setOnClickListener(this);
        mBtnCameraAvatar.setOnClickListener(this);
        mBtnCameraCoverPhoto.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_save_setting_profile_manaager:
                saveInformationBasicToFirebase();
                break;
            case R.id.btn_camera_cover_photo_setting_profile_manager:
                chooseCoverPhotoFromGallery();
                break;
            case R.id.btn_camera_avatar_setting_profile_manager:
                chooseAvatarPhotoFromGallery();
                break;
        }
    }

    private void chooseCoverPhotoFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,GALLERY_PICK_COVER);
    }

    private void saveAvatarProfileToFirebase() {
        if (mImageUri != null) {
            mAvatarDatabase.child(currentUserID).putFile(mImageUri);
            mUploadTask = mAvatarDatabase.putFile(mImageUri);
            mUploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    } else {
                        return mAvatarDatabase.getDownloadUrl();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    String avatarUrl = downloadUri.toString();
                    mUserDatabase.child("avatar").setValue(avatarUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Lưu url anh vao user thanh cong", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(getApplicationContext(), "không thành công", Toast.LENGTH_SHORT).show();
                                Log.d("error", message);
                            }
                        }
                    });
                }
            });

        }
    }

    private void saveInformationBasicToFirebase() {
        String hotline = mEdtHotline.getText().toString();
        String location = mEdtLocation.getText().toString();
        String des = mEdtDes.getText().toString();
        String userName = mTxtUsername.getText().toString();

        if (TextUtils.isEmpty(hotline)) {
            mEdtHotline.setError("Nhập số điện thoại di động của bản quản lý");
        }
        if (TextUtils.isEmpty(des)) {
            mEdtDes.setError("Nhập vài dòng mô tả về bạn đi nào!!!");
        }
        if (TextUtils.isEmpty(location)) {
            mEdtLocation.setError("Nhập địa chỉ phòng ban quản lý của khu chung cư");
        }
        else {
            mProgessBar.setTitle("Lưu thông tin cá nhân");
            mProgessBar.setMessage("Loading....");
            mProgessBar.show();
            mProgessBar.setCanceledOnTouchOutside(true);

            HashMap<String , Object> hashMap = new HashMap<>();
            hashMap.put("username", userName);
            hashMap.put("des", des);
            hashMap.put("hotline", hotline);
            hashMap.put("location", location);
            hashMap.put("userId", currentUserID);

//            Manager manager = new Manager(userName,des,currentUserID,hotline,location);
            mUserDatabase.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mProgessBar.dismiss();
                        Toast.makeText(getApplicationContext(), "Save basic information success", Toast.LENGTH_SHORT).show();
                        intentHomeActivity();
                    } else {
                        mProgessBar.dismiss();
                        String message = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        Log.d("basic", message);
                    }
                }
            });
        }
    }

    private void intentHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), HomeManagerActivity.class);
        startActivity(intent);
        finish();
    }


    private void chooseAvatarPhotoFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PICK_AVATAR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_AVATAR && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            mImgAvatar.setImageURI(mImageUri);
            saveAvatarProfileToFirebase();
        }
        else if (requestCode == GALLERY_PICK_COVER && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            mImgCoverPhoto.setImageURI(mImageUri);
            saveCoverProfileToFirebase();
        }
    }

    private void saveCoverProfileToFirebase() {
        if (mImageUri != null) {
            mCoverPhotoDatabase.child(currentUserID).putFile(mImageUri);
            mUploadTask = mCoverPhotoDatabase.putFile(mImageUri);
            mUploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    } else {
                        return mCoverPhotoDatabase.getDownloadUrl();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    String coverUrl = downloadUri.toString();
                    mUserDatabase.child("coverPhoto").setValue(coverUrl);
                }
            });

        }
    }
}
