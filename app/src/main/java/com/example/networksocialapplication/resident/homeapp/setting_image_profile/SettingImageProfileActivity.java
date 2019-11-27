package com.example.networksocialapplication.resident.homeapp.setting_image_profile;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.networksocialapplication.R;
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

public class SettingImageProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_PICK_AVATAR= 111;
    private static final int GALLERY_PICK_COVER = 112;
    private ImageView mImgCoverPhoto;
    private CircleImageView mImgAvatar;
    private EditText mEdtUsername;
    private EditText mEdtDes;
    private Button mBtnNext;
    private ImageView mImgBack;
    private ImageButton mBtnCameraCoverPhoto;
    private ImageButton mBtnCameraAvatar;
    private ProgressDialog mProgessBar;

    private StorageReference mAvatarDatabase;
    private StorageReference mCoverPhotoDatabase;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference mUserDatabase;
    private Uri mImageUri;
    private StorageTask mUploadTask;

    public SettingImageProfileActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_image_profile);

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
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        mAvatarDatabase = FirebaseStorage.getInstance().getReference().child("Avatar");
        mCoverPhotoDatabase = FirebaseStorage.getInstance().getReference().child("CoverPhoto");
    }

    private void initView() {

        mProgessBar = new ProgressDialog(this);
        mImgAvatar = findViewById(R.id.img_avatar_profile);
        mImgCoverPhoto = findViewById(R.id.img_cover_photo_setting_profile);
        mEdtUsername = findViewById(R.id.edt_username_setting_profile);
        mEdtDes = findViewById(R.id.edt_des_setting_profile);
        mBtnNext = findViewById(R.id.btn_next_setting_profile);
        mImgBack = findViewById(R.id.btn_back_setting_profile);
        mBtnCameraCoverPhoto = findViewById(R.id.btn_camera_cover_photo);
        mBtnCameraAvatar = findViewById(R.id.btn_camera_avatar);

        mBtnNext.setOnClickListener(this);
        mImgBack.setOnClickListener(this);
        mBtnCameraAvatar.setOnClickListener(this);
        mBtnCameraCoverPhoto.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back_setting_profile:
                break;
            case R.id.btn_next_setting_profile:
                saveInformationBasicToFirebase();
                break;
            case R.id.btn_camera_cover_photo:
                chooseCoverPhotoFromGallery();
                break;
            case R.id.btn_camera_avatar:
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
            String userName = mEdtUsername.getText().toString();
            String des = mEdtDes.getText().toString();

            if (TextUtils.isEmpty(userName)) {
                mEdtUsername.setError("Không nhập tên là không ai biết bạn là ai đâu nhé.");
            }
            if (TextUtils.isEmpty(des)) {
                mEdtDes.setError("Nhập vài dòng mô tả về bạn đi nào!!!");
            }

            else {
                mProgessBar.setTitle("Lưu thông tin cá nhân");
                mProgessBar.setMessage("Loading....");
                mProgessBar.show();
                mProgessBar.setCanceledOnTouchOutside(true);

                HashMap<String , Object> hashMap = new HashMap<>();
                hashMap.put("username", userName);
                hashMap.put("des", des);
                hashMap.put("residentId", currentUserID);

                mUserDatabase.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mProgessBar.dismiss();
                            Toast.makeText(getApplicationContext(), "Save basic information success", Toast.LENGTH_SHORT).show();
                            intentSettingInfomationProfile();
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
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
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
//            try {
//                InputStream imageStream = getContentResolver().openInputStream(mImageUri);
//                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                mImgAvatar.setImageBitmap(selectedImage);
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
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



