package com.example.networksocialapplication.resident.homeapp.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.resident.homeapp.update_profile.UpdateProfileActivity;
import com.example.networksocialapplication.adapters.PostAdapter;
import com.example.networksocialapplication.models.Post;
import com.example.networksocialapplication.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment implements View.OnClickListener{

    private static final int GALLERY_PICK_COVER = 144;
    private static final int GALLERY_PICK_AVATAR = 143;

    private RecyclerView mRecyclerView;
    private PostAdapter mPostAdapter;
    private ArrayList<Post> mListPost;

    private static final String TAG = "profile";
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mPostDatabase;
    private String mCurrentUserId;

    private CircleImageView mAvatar;
    private TextView mTxtUsername;
    private TextView mTxtDes;
    private FloatingActionButton mFAB;
    private ImageView mCoverPhoto;

    private TextView mTxtPhoneNumber;
    private TextView mTxtGender;
    private TextView mTxtDateBirth;

    private EditText edtCurrentPass;
    private EditText edtNewPass;
    private EditText edtRePass;
    private Button mBtnSave;

    private Uri mImageUri;
    private StorageReference mImageAvatarDatabase;
    private StorageReference mImageCoverDatabase;
    private UploadTask mUploadTask;
    private boolean mIsAttach;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)   {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initView(view);
        initRecyclerview(view);
        initFirebase();
        displayInformationBasic();
        displayListPost();
        return view;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if (!getActivity().isDestroyed()){
//            displayInformationBasic();
//            displayListPost();
//        }
//
//    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
         mIsAttach = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mIsAttach =  false;
    }

    private void initView(View view) {
        mTxtPhoneNumber = view.findViewById(R.id.txt_phone_number_profile_fragment);
        mTxtDateBirth = view.findViewById(R.id.txt_date_birth_profile_fragment);
        mTxtGender = view.findViewById(R.id.txt_gender_profile_fragment);

        mCoverPhoto = view.findViewById(R.id.img_cover_photo_profile);
        mAvatar = view.findViewById(R.id.img_avatar_profile_fragment);
        mTxtUsername = view.findViewById(R.id.txt_username_profile);
        mTxtDes = view.findViewById(R.id.txt_des_profile);
        mFAB = view.findViewById(R.id.fab_edit);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
                createDialogChooseEdit(view);
                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void createDialogChooseEdit(View view) {
        Button mBtnEditProfile = view.findViewById(R.id.btn_edit_profile_dialog);
        Button mBtnChangePassword = view.findViewById(R.id.btn_change_password_dialog);
        Button mBtnUpdateAvatar = view.findViewById(R.id.btn_update_avatar_dialog);
        Button mBtnUpdateCoverPhoto = view.findViewById(R.id.btn_update_cover_photo);

        mBtnChangePassword.setOnClickListener(this);
        mBtnEditProfile.setOnClickListener(this);
        mBtnUpdateAvatar.setOnClickListener(this);
        mBtnUpdateCoverPhoto.setOnClickListener(this);
    }

    private void creatDialogChangePassword(View viewChange) {
        edtCurrentPass = viewChange.findViewById(R.id.dialog_changepw_edt_pass_current);
        edtNewPass = viewChange.findViewById(R.id.dialog_changepw_edt_pass_new);
        edtRePass = viewChange.findViewById(R.id.dialog_changepw_edt_repass_new);
        mBtnSave = viewChange.findViewById(R.id.dialog_changepw_btn_save);

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChangePassword();
            }
        });
    }

    private void saveChangePassword() {
        String currentPass = edtCurrentPass.getText().toString();
        final String newPass = edtNewPass.getText().toString();
        String rePass = edtRePass.getText().toString();

        if (TextUtils.isEmpty(currentPass)) {
            edtCurrentPass.setError("Hãy nhập mật khẩu hiện tại của bạn!");
        }
        if (TextUtils.isEmpty(currentPass)) {
            edtNewPass.setError("Hãy nhập mật khẩu mới!");
        }
        if (TextUtils.isEmpty(currentPass)) {
            edtRePass.setError("Hãy nhập mật lại mật khẩu mới!");
        }
        if (newPass.length() < 6) {
            edtRePass.setError("Mật khẩu phải dài hơn 6 ký tự!");
        }
        if (!newPass.equals(rePass)) {
            edtRePass.setError("Mật khẩu không trùng khớp!");
        } else {
            //get current user
            final FirebaseUser user = mAuth.getCurrentUser();

// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), currentPass);

// Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(getContext(), "Thay đổi mật khẩu không thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Error auth failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void displayInformationBasic() {
        mUserDatabase.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Resident user = dataSnapshot.getValue(Resident.class);
                    mTxtUsername.setText(user.getUsername());
                    mTxtDes.setText(user.getDes());
                    mTxtPhoneNumber.setText(user.getPhoneNumber());
                    mTxtGender.setText(user.getGender());
                    mTxtDateBirth.setText(user.getDateOfBirth());
                    if (mIsAttach){
                        Glide.with(getActivity()).load(user.getAvatar()).error(R.drawable.ic_load_image_erroe).into(mAvatar);
                        Glide.with(getActivity()).load(user.getCoverPhoto()).error(R.drawable.ic_load_image_erroe).into(mCoverPhoto);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean checkUserId(Post post) {
        if (post.getUserID().equals(mCurrentUserId)) {
            return true;
        }
        return false;
    }

    private void displayListPost() {
        mPostDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Post post = item.getValue(Post.class);
                    if (checkUserId(post)) {
                        mListPost.add(post);
                    }
                }
                Log.d(TAG, mListPost.toString());
                mPostAdapter = new PostAdapter(getContext(), mListPost);
                mRecyclerView.setAdapter(mPostAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("Post");
        mImageAvatarDatabase = FirebaseStorage.getInstance().getReference().child("Avatar").child(mCurrentUserId);
        mImageCoverDatabase = FirebaseStorage.getInstance().getReference().child("CoverPhoto").child(mCurrentUserId);
    }


    private void initRecyclerview(View view) {
        mRecyclerView = view.findViewById(R.id.profile_list_post);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mListPost = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_edit_profile_dialog:
                Intent intent = new Intent(getContext(), UpdateProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_change_password_dialog:
                AlertDialog.Builder builderChange = new AlertDialog.Builder(getContext());
                View viewChange = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
                creatDialogChangePassword(viewChange);
                builderChange.setView(viewChange);
                AlertDialog dialog = builderChange.create();
                dialog.show();
                break;
            case R.id.btn_update_avatar_dialog:
                chooseAvatarFromGallery();

                break;
            case  R.id.btn_update_cover_photo:
                chooseCoverPhotoFromGallery();
                break;
        }

    }
    private void chooseAvatarFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PICK_AVATAR);
    }

    private void chooseCoverPhotoFromGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PICK_COVER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_AVATAR && resultCode == getActivity().RESULT_OK && data != null){
            mImageUri = data.getData();
            updateAvatarProfileToFirebase();
        }else if (requestCode == GALLERY_PICK_COVER && resultCode == getActivity().RESULT_OK && data != null){
            mImageUri = data.getData();
            updateCoverProfileToFirebase();
        }
    }

    private void updateCoverProfileToFirebase() {
        if (mImageUri != null) {
            mImageCoverDatabase.putFile(mImageUri);
            mUploadTask = mImageCoverDatabase.putFile(mImageUri);
            mUploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    } else {
                        return mImageCoverDatabase.getDownloadUrl();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    String avatarUrl = downloadUri.toString();


                    mUserDatabase.child(mCurrentUserId).child("coverPhoto").setValue(avatarUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void updateAvatarProfileToFirebase() {
        if (mImageUri != null) {
            mImageAvatarDatabase.putFile(mImageUri);
            mUploadTask = mImageAvatarDatabase.putFile(mImageUri);
            mUploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    } else {
                        return mImageAvatarDatabase.getDownloadUrl();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    String avatarUrl = downloadUri.toString();

                    mUserDatabase.child(mCurrentUserId).child("avatar").setValue(avatarUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
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
