package com.example.networksocialapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.networksocialapplication.models.Event;
import com.example.networksocialapplication.models.NotificationFromManager;
import com.example.networksocialapplication.models.Reflect;
import com.example.networksocialapplication.time_current.DatePicker;
import com.example.networksocialapplication.time_current.Time;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private static final int REQUEST_CODE_CHOOSE_IMAGE_EVENT = 145;
    private Toolbar mToolbar;
    private ImageView mImage;
    private Button mBtnChooseImage;
    private TextInputEditText mNameEvent;
    private TextInputEditText mLocation;
    private TextInputEditText mDes;
    private TextInputEditText mDateStart;
    private TextInputEditText mDateFinish;
    private TextInputEditText mTimeStart;
    private TextInputEditText mTimeFinish;
    private Button mBtnCreate;

    private DatePicker mDatePicker;
    private Uri mImageUri;

    private DatabaseReference mEventRef;
    private StorageReference mImageRef;
    private UploadTask mUploadTask;
    private Time mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        initToolbar();
        initView();
        initFirebase();
    }

    private void initFirebase() {
        mEventRef = FirebaseDatabase.getInstance().getReference().child("Events");
        mImageRef = FirebaseStorage.getInstance().getReference().child("Image_Events");
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Tạo sự kiện");
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
        mDatePicker = new DatePicker(this);
        mImage = findViewById(R.id.img_create_event);
        mBtnChooseImage = findViewById(R.id.btn_choose_image_create_event);
        mNameEvent = findViewById(R.id.edt_name_create_event);
        mLocation = findViewById(R.id.edt_location_create_event);
        mDes = findViewById(R.id.edt_des_create_event);
        mDateFinish = findViewById(R.id.edt_date_finish_create_event);
        mTimeStart = findViewById(R.id.edt_time_start_create_event);
        mTimeFinish = findViewById(R.id.edt_time_finish_create_event);
        mDateStart = findViewById(R.id.edt_date_start_create_event);
        mBtnCreate = findViewById(R.id.btn_create_event);


        mDateStart.setOnTouchListener(this);
        mDateFinish.setOnTouchListener(this);
        mTimeStart.setOnTouchListener(this);
        mTimeFinish.setOnTouchListener(this);

        mBtnChooseImage.setOnClickListener(this);
        mBtnCreate.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_choose_image_create_event:
                chooseImage();
                break;
            case R.id.btn_create_event:
                createEvent();
                break;
        }
    }

    private void createEvent() {
        final String name = mNameEvent.getText().toString();
        final String location = mLocation.getText().toString();
        final String des = mDes.getText().toString();
        final String datePost = mTime.getDateCurrent();
        final String timePost = mTime.getTimeHourCurrent();
        final String dateStart = mDateStart.getText().toString();
        final String timeStart = mTimeStart.getText().toString();
        final String dateFinish = mDateFinish.getText().toString();
        final String timeFinish = mTimeFinish.getText().toString();
        final String idEvent = mEventRef.push().getKey();
        saveImageToFirebase(idEvent);

        if (TextUtils.isEmpty(name)) {
            mNameEvent.setError("Tên sự kiện không được bỏ trống");
        }
        if (TextUtils.isEmpty(location)) {
            mLocation.setError("Địa điểm tổ chức sự kiện không được bỏ trống");
        }
        if (TextUtils.isEmpty(des)) {
            mDes.setError("Mô tả sự kiện không được bỏ trống");
        } else {
            Event notification = new Event(datePost,timePost,des,idEvent,name,location,dateStart,timeStart,dateFinish,timeFinish);
            mEventRef.child(idEvent).setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Tao sự kiện thành công", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE_EVENT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE_EVENT && resultCode == RESULT_OK && data != null){
            mImageUri = data.getData();
            mImage.setImageURI(mImageUri);
            mBtnChooseImage.setVisibility(View.INVISIBLE);
        }
    }
    private void saveImageToFirebase(final String eventId) {
        if (mImageUri != null) {
            mImageRef.child(eventId).putFile(mImageUri);
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

                        mEventRef.child(eventId).child("imagePost").setValue(imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.edt_time_start_create_event:
                final int DRAWABLE_RIGHT_TIME_START = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mTimeStart.getRight() - mTimeStart.getCompoundDrawables()[DRAWABLE_RIGHT_TIME_START].getBounds().width())) {
                        mDatePicker.showTimePicker(mTimeStart);
                        return true;
                    }
                }
                return false;
            case R.id.edt_date_start_create_event:
                final int DRAWABLE_RIGHT_DATE_START = 2 ;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mDateStart.getRight() - mDateStart.getCompoundDrawables()[DRAWABLE_RIGHT_DATE_START].getBounds().width())) {
                        mDatePicker.showDatePicker(mDateStart);
                        return true;
                    }
                }
                return false;
            case R.id.edt_date_finish_create_event:
                final int DRAWABLE_RIGHT_DATE_FINISH= 2 ;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mDateFinish.getRight() - mDateFinish.getCompoundDrawables()[DRAWABLE_RIGHT_DATE_FINISH].getBounds().width())) {
                        mDatePicker.showDatePicker(mDateFinish);
                        return true;
                    }
                }
                return false;
            case R.id.edt_time_finish_create_event:
                final int DRAWABLE_RIGHT_TIME_FINISH = 2 ;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mTimeFinish.getRight() - mTimeFinish.getCompoundDrawables()[DRAWABLE_RIGHT_TIME_FINISH].getBounds().width())) {
                        mDatePicker.showTimePicker(mTimeFinish);
                        return true;
                    }
                }
                return false;
        }
        return false;
    }
}
