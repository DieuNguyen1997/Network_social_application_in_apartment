package com.example.networksocialapplication.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.networksocialapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class UpdateProfileActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mEdtUsername;
    private EditText mEdtDes;
    private Button mBtnSave;
    private EditText mEdtPhoneNumber;
    private TextView mTxtBirthDay;
    private ImageButton mBtnCalender;
    private Spinner mSpinNameRoom;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private RadioButton mMale, mFemale, mOther;
    private RadioGroup mRadioGroup;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private String mCurrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        initToolbar();
        initFirebase();
        displayInfomation();
        initView() ;

    }

    private void displayInfomation() {
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue().toString();
                String des = dataSnapshot.child("des").getValue().toString();
                String phone = dataSnapshot.child("phoneNumber").getValue().toString();
                String dateOfBirth = dataSnapshot.child("dateOfBirth").getValue().toString();
                String gender = dataSnapshot.child("gender").getValue().toString();

                mEdtUsername.setText(username);
                mEdtDes.setText(des);
                mEdtPhoneNumber.setText(phone);
                mTxtBirthDay.setText(dateOfBirth);
                if (mMale.getText().toString().equals(gender)){
                    mMale.setChecked(true);
                }else if (mFemale.getText().toString().equals(gender)){
                    mFemale.setChecked(true);
                }else{
                    mOther.setChecked(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initView() {
        mRadioGroup = findViewById(R.id.radio_group_gender_update_profile);
        mEdtPhoneNumber = findViewById(R.id.edt_phone_update_profile);
        mTxtBirthDay = findViewById(R.id.txt_birthday_update_profile);
        mBtnCalender = findViewById(R.id.btn_calender_update_profile);
        mBtnSave = findViewById(R.id.btn_save_update_profile);
        mMale = findViewById(R.id.radio_btn_male_update_profile);
        mFemale = findViewById(R.id.radio_btn_female_update_profile);
        mOther = findViewById(R.id.radio_btn_other_update_profile);
        mEdtDes = findViewById(R.id.edt_des__update_profile);
        mEdtUsername = findViewById(R.id.edt_username__update_profile);

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatUser();
            }
        });
        mBtnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int moth = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateProfileActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, moth, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String dateOfBirth = dayOfMonth + "/" + month + "/" + year;
                mTxtBirthDay.setText(dateOfBirth);
            }
        };

    }

    private void creatUser() {
        //get phone number
        String username = mEdtUsername.getText().toString();
        String des = mEdtDes.getText().toString();
        final String phoneNumber = mEdtPhoneNumber.getText().toString();
        int idChecked = mRadioGroup.getCheckedRadioButtonId();
        View radioButton = findViewById(idChecked);
        int index = mRadioGroup.indexOfChild(radioButton);
        RadioButton r = (RadioButton) mRadioGroup.getChildAt(index);
        String gender = r.getText().toString();
        //get dat of birth
        String dateOfBirth = mTxtBirthDay.getText().toString();
        //get name room
//        String nameRoom = mSpinNameRoom.getSelectedItem().toString();
        if (TextUtils.isEmpty(gender)) {
            Toast.makeText(this,"Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(username)) {
            mEdtPhoneNumber.setError("Vui lòng nhập vào tên người dùng");
        }
        if (TextUtils.isEmpty(des)) {
            Toast.makeText(getApplicationContext(), "Vui lòng chọn mô tả về bạn", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            mEdtPhoneNumber.setError("Vui lòng nhập vào số điện thoại của bạn");
        }
        if (TextUtils.isEmpty(dateOfBirth)) {
            Toast.makeText(getApplicationContext(), "Vui lòng chọn ngày sinh", Toast.LENGTH_SHORT).show();
        } else {

//            User user = new User(phoneNumber,gender,dateOfBirth);
//            HashMap<String, User> hashMap = new HashMap<>();
//            hashMap.put("information",user);
//            HashMap hashMap = new HashMap();
//            hashMap.put("phoneNumber", phoneNumber);
//            hashMap.put("gender", gender);
//            hashMap.put("dateOfBirth", dateOfBirth);

            mUserRef.child("username").setValue(username);
            mUserRef.child("des").setValue(des);
            mUserRef.child("phoneNumber").setValue(phoneNumber);
            mUserRef.child("gender").setValue(gender);
            mUserRef.child("dateOfBirth").setValue(dateOfBirth).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Lưu thông tin thành công!!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
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
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserID);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_layout);
        mToolbar.setTitle(R.string.title_toolbar_update_profile);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        setSupportActionBar(mToolbar);

    }
}
