package com.example.networksocialapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.models.Manager;
import com.example.networksocialapplication.time_current.DatePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UpdateManagerActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText mEdtDes;
    private Button mBtnSave;
    private EditText mEdtPhoneNumber;
    private EditText mEdtLocation;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private String mCurrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_manager);

        initToolbar();
        initFirebase();
        getManager();
        initView();
    }

    private void getManager() {
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String des = dataSnapshot.child("des").getValue().toString();
                String phone = dataSnapshot.child("phoneNumber").getValue().toString();
                String location = dataSnapshot.child("location").getValue().toString();

                mEdtDes.setText(des);
                mEdtPhoneNumber.setText(phone);
                mEdtLocation.setText(location);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initView() {
        mEdtPhoneNumber = findViewById(R.id.edt_phone_update_profile_manager);
        mBtnSave = findViewById(R.id.btn_save_update_profile_manager);
        mEdtLocation = findViewById(R.id.edt_location_update_profile_manager);
        mEdtDes = findViewById(R.id.edt_des_update_profile_manager);

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateManager();
            }
        });

    }

    private void updateManager() {
        String des = mEdtDes.getText().toString();
        final String phoneNumber = mEdtPhoneNumber.getText().toString();
        String location = mEdtLocation.getText().toString();
        if (TextUtils.isEmpty(location)) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(des)) {
            Toast.makeText(getApplicationContext(), "Vui lòng chọn mô tả về bạn", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            mEdtPhoneNumber.setError("Vui lòng nhập vào số điện thoại của bạn");
        } else {

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("des", des);
            hashMap.put("phoneNumber", phoneNumber);
            hashMap.put("location", location);

            mUserRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Lưu thông tin thành công!!!", Toast.LENGTH_SHORT).show();
                    } else {
                        String messgae = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), messgae, Toast.LENGTH_SHORT).show();
                        Log.d("basic", messgae);
                    }
                }
            });
        }
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Manager").child(mCurrentUserID);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_toolbar_update_profile);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
