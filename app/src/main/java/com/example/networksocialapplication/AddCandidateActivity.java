package com.example.networksocialapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.networksocialapplication.admin.create_election.CreateElectionActivity;
import com.example.networksocialapplication.models.Candidate;
import com.example.networksocialapplication.time_current.DatePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class AddCandidateActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_CHOOSE_AVATAR = 111;
    private ImageView mAvatar;
    private ImageButton mCamera;
    private EditText mName;
    private EditText mAddressApartment;
    private EditText mDateBirth;
    private EditText mJob;
    private EditText mYearStart;
    private EditText mSlogan;
    private Button mButtonAdd;
    private Toolbar mToolbar;
    private Uri mImageUri;

    private DatePicker mDatePicker;
    private DatabaseReference mCandidateRef;
    private StorageReference mImageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_candidate);

        initToolbar();
        initFirebase();
        initView();
    }

    private void initFirebase() {
        mCandidateRef = FirebaseDatabase.getInstance().getReference().child("Candidates");
        mImageRef = FirebaseStorage.getInstance().getReference().child("Image_Candidates");
    }

    private void initView() {
        mDatePicker = new DatePicker(AddCandidateActivity.this);
        mAvatar = findViewById(R.id.img_avatar_create_cadicate);
        mCamera = findViewById(R.id.btn_camera_create_cadicate);
        mName = findViewById(R.id.edt_name_create_candicate);
        mAddressApartment = findViewById(R.id.edt_address_apartment_create_cadicate);
        mDateBirth = findViewById(R.id.edt_datebirth_create_candicate);
        mJob = findViewById(R.id.edt_job_create_cadicate);
        mYearStart = findViewById(R.id.edt_year_start_create_cadicate);
        mSlogan = findViewById(R.id.edt_slogan_create_cadicate);
        mButtonAdd = findViewById(R.id.btn_add_candidae);

        mCamera.setOnClickListener(this);
        mButtonAdd.setOnClickListener(this);
        mDateBirth.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT_TIME_START = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mDateBirth.getRight() - mDateBirth.getCompoundDrawables()[DRAWABLE_RIGHT_TIME_START].getBounds().width())) {
                        mDatePicker.showDatePicker(mDateBirth);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Thêm ứng cử viên");
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera_create_cadicate:
                chooseAvatar();
                break;
            case R.id.btn_add_candidae:
                checkAddCandidate();
                break;

        }
    }

    private void checkAddCandidate() {
        Calendar calendar = Calendar.getInstance();
        int currentyear = calendar.get(Calendar.YEAR);
        final String electionId = getIntent().getStringExtra("electionId");
        String name = mName.getText().toString();
        String job = mJob.getText().toString();
        String address = mAddressApartment.getText().toString();
        String birthday = mDateBirth.getText().toString();
        String yearStar = mYearStart.getText().toString();
        String slogan = mSlogan.getText().toString();

        if (TextUtils.isEmpty(name)) {
            mName.setError("Hãy nhập tên");
        }
        if (TextUtils.isEmpty(job)) {
            mJob.setError("Hãy nhập nghề nghiệp hiện tại");
        }
        if (TextUtils.isEmpty(address)) {
            mAddressApartment.setError("Hãy nhập địa chỉ phòng");
        }
        if (TextUtils.isEmpty(birthday)) {
            mDateBirth.setError("Hãy nhập ngày sinh");
        }
        if (TextUtils.isEmpty(yearStar)) {
            mYearStart.setError("Hãy nhập năm bắt đầu sống tại khu chung cư");
        }
        if (TextUtils.isEmpty(slogan)) {
            mSlogan.setError("Hãy nhập tiêu chí hành động sau khi được bầu");
        }
        if (mImageUri == null) {
            Toast.makeText(getApplicationContext(), "Hãy chon ảnh sự kiện", Toast.LENGTH_SHORT).show();
        }
        if (Integer.parseInt(yearStar) >= currentyear){
            mYearStart.setError("Năm bắt đầu ở chung cư phải nhỏ hơn năm hiện tại");
        }
        else {
            String candateId = mCandidateRef.push().getKey();
            Candidate candidate = new Candidate(candateId, electionId, name, birthday, address, job, yearStar, slogan);
            addCandidate(candidate);
        }
    }

    private void addCandidate(final Candidate candidate) {
        final String candidateId = candidate.getCadiadateId();
        final String electionId = getIntent().getStringExtra("electionId");
        mCandidateRef.child(electionId).child(candidateId).setValue(candidate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            saveImage(electionId, candidateId);
                            Toast.makeText(AddCandidateActivity.this, "Tạo ứng viên thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddCandidateActivity.this, CreateElectionActivity.class);

                            startActivity(intent);
                        } else {
                            Toast.makeText(AddCandidateActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveImage(final String electionId, final String candidateId) {
        if (mImageUri != null) {
            final StorageReference filePath = mImageRef.child(electionId).child(candidateId);
            UploadTask uploadTask = filePath.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    } else {
                        return filePath.getDownloadUrl();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        String imageUrl = uri.toString();

                        mCandidateRef.child(electionId).child(candidateId).child("image").setValue(imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        }Toast.makeText(getApplicationContext(), "hãy chọn ảnh sự kiện", Toast.LENGTH_SHORT).show();
    }

    private void chooseAvatar() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_AVATAR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_AVATAR && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            mAvatar.setImageURI(mImageUri);
        }
    }
}
