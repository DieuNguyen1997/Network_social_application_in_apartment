package com.example.networksocialapplication.admin.profile_manager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.CreateEventActivity;
import com.example.networksocialapplication.CreateNotificationActivity;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.UpdateManagerActivity;
import com.example.networksocialapplication.adapters.ReflectAdapter;
import com.example.networksocialapplication.adapters.ReflectInManagerAdapter;
import com.example.networksocialapplication.models.Manager;
import com.example.networksocialapplication.models.Reflect;
import com.example.networksocialapplication.models.User;
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
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileManagerFragment extends Fragment implements View.OnClickListener {

    private static final int GALLERY_PICK_AVATAR = 140 ;
    private static final int GALLERY_PICK_COVER_PHOTO = 142;
    private ImageView mCoverPhoto;
    private CircleImageView mAvatar;
    private TextView mTxtUsername;
    private TextView mDes;
    private LinearLayout mLayout_create_notify;
    private LinearLayout mLayout_create_event;
    private RecyclerView mRecyclerView;
    private ImageButton mImgUpdateCoverPhoto;
    private ImageButton mImgUpdateAvatar;
    private ImageView mEditProfile;

    private TextView mTxtHotline;
    private TextView mTxtLocation;

    private DatabaseReference mManagerDatabase;
    private DatabaseReference mReflectRef;
    private String mCurrentUserId;
    private List<Reflect> mReflects;
    private ReflectInManagerAdapter mReflectAdapter;
    private Uri mImageUri;
    private StorageReference mAvatarDatabase;
    private StorageReference mCoverPhotoDatabase;
    private UploadTask mUploadTask;
    private boolean isAttached;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_manager, container, false);
        initView(view);
        initFirebase();
        initRecyclerview(view);
        displayInformationBasic();
//        displayListReflectFromResident();
        return view;
    }

    private void initRecyclerview(View view) {
        mReflects = new ArrayList<>();
        mRecyclerView = view.findViewById(R.id.recycler_view_list_reflect_profile_manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
    }

//    private void displayListReflectFromResident() {
//        mReflectRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot item : dataSnapshot.getChildren()) {
//                    Reflect reflect = item.getValue(Reflect.class);
//                    mReflects.add(reflect);
//                    mReflectAdapter = new ReflectInManagerAdapter(getActivity(), mReflects);
//                    mReflectAdapter.notifyDataSetChanged();
//                    mRecyclerView.setAdapter(mReflectAdapter);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void initView(View view) {
        mLayout_create_notify = view.findViewById(R.id.root_create_notification);
        mLayout_create_event = view.findViewById(R.id.root_create_event);
        mTxtHotline = view.findViewById(R.id.txt_hotline_profile_manager);
        mTxtLocation = view.findViewById(R.id.txt_location_profile_manager);

        mCoverPhoto = view.findViewById(R.id.img_cover_photo_profile_manager);
        mAvatar = view.findViewById(R.id.img_avatar_profile_manager);
        mTxtUsername = view.findViewById(R.id.txt_username_profile_manager);
        mDes = view.findViewById(R.id.txt_des_profile_manager);

        mImgUpdateAvatar = view.findViewById(R.id.btn_choose_avatar_profile_manager);
        mImgUpdateCoverPhoto = view.findViewById(R.id.btn_choose_cover_profile_manager);
        mEditProfile = view.findViewById(R.id.img_edit_profile_manager);

        mLayout_create_event.setOnClickListener(this);
        mLayout_create_notify.setOnClickListener(this);
        mImgUpdateAvatar.setOnClickListener(this);
        mImgUpdateCoverPhoto.setOnClickListener(this);
        mEditProfile.setOnClickListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        isAttached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttached = false;
    }

    private void displayInformationBasic() {
        mManagerDatabase.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Manager manager = dataSnapshot.getValue(Manager.class);
                    String manaderId = manager.getManagerId();
                    Log.d("profile", manaderId);
                    mTxtUsername.setText(manager.getUsername());
                    mDes.setText(manager.getDes());
                    mTxtHotline.setText(manager.getPhoneNumber());
                    mTxtLocation.setText(manager.getLocation());
                    if (isAttached){
                        Glide.with(getActivity()).load(manager.getAvatar()).error(R.drawable.ic_load_image_erroe).into(mAvatar);
                        Glide.with(getActivity()).load(manager.getCoverPhoto()).error(R.drawable.ic_load_image_erroe).into(mCoverPhoto);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initFirebase() {
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mManagerDatabase = FirebaseDatabase.getInstance().getReference().child("Manager");
        mReflectRef = FirebaseDatabase.getInstance().getReference().child("Reflect");
        mAvatarDatabase = FirebaseStorage.getInstance().getReference().child("Avatar");
        mCoverPhotoDatabase = FirebaseStorage.getInstance().getReference().child("CoverPhoto");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root_create_notification:
                startActivity(new Intent(getActivity(), CreateNotificationActivity.class));
                break;
            case R.id.root_create_event:
                startActivity(new Intent(getActivity(), CreateEventActivity.class));
                break;
            case R.id.btn_choose_avatar_profile_manager:
                chooseAvatar();
                break;
            case R.id.btn_choose_cover_profile_manager:
                chooseCoverPhoto();
                break;
            case R.id.img_edit_profile_manager:
                startActivity(new Intent(getActivity(), UpdateManagerActivity.class));
                break;
        }
    }

    private void chooseCoverPhoto() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PICK_COVER_PHOTO);
    }

    private void chooseAvatar() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PICK_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == GALLERY_PICK_AVATAR && resultCode == getActivity().RESULT_OK && data != null){
           mImageUri = data.getData();
           mAvatar.setImageURI(mImageUri);
           updateAvatarProfileToFirebase();
       }else if (requestCode == GALLERY_PICK_COVER_PHOTO && resultCode == getActivity().RESULT_OK && data != null){
           mImageUri = data.getData();
           mCoverPhoto.setImageURI(mImageUri);
           updateCoverProfileToFirebase();
       }
    }

    private void updateAvatarProfileToFirebase() {
        if (mImageUri != null) {
            mAvatarDatabase.putFile(mImageUri);
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

                    mManagerDatabase.child(mCurrentUserId).child("avatar").setValue(avatarUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Lưu url anh vao user thanh cong", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(getActivity(), "không thành công", Toast.LENGTH_SHORT).show();
                                Log.d("error", message);
                            }
                        }
                    });
                }
            });

        }
    }

    private void updateCoverProfileToFirebase() {
        if (mImageUri != null) {
            mCoverPhotoDatabase.putFile(mImageUri);
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
                    String avatarUrl = downloadUri.toString();

                    mManagerDatabase.child(mCurrentUserId).child("coverPhoto").setValue(avatarUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Lưu url anh vao user thanh cong", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(getActivity(), "không thành công", Toast.LENGTH_SHORT).show();
                                Log.d("error", message);
                            }
                        }
                    });
                }
            });

        }
    }
}
