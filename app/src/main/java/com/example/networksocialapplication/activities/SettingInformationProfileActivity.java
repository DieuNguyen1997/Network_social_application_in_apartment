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

import com.example.networksocialapplication.R;
import com.example.networksocialapplication.homeapp.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class SettingInformationProfileActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_setting_information_profile);

        inirFirebase();
        initView();
    }

    private void inirFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserID);
    }

    private void initView() {

        mRadioGroup = findViewById(R.id.radio_group_gender);
        mEdtPhoneNumber = findViewById(R.id.edt_phone_setting_information);
        mTxtBirthDay = findViewById(R.id.setting_information_txt_birthday);
        mBtnCalender = findViewById(R.id.btn_calender_setting_information);
        mSpinNameRoom = findViewById(R.id.spinner_name_room_setting_information);
        mBtnSave = findViewById(R.id.btn_save_setting_information);
        mMale = findViewById(R.id.radio_btn_male);
        mFemale = findViewById(R.id.radio_btn_female);
        mOther = findViewById(R.id.radio_btn_other);

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(SettingInformationProfileActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, moth, day);
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

    public void creatUser() {
        //get phone number
        String phoneNumber = mEdtPhoneNumber.getText().toString();
        int idChecked = mRadioGroup.getCheckedRadioButtonId();
        View radioButton = findViewById(idChecked);
        int index = mRadioGroup.indexOfChild(radioButton);
        RadioButton r = (RadioButton) mRadioGroup.getChildAt(index);
        String gender = r.getText().toString();
        //get dat of birth
        String dateOfBirth = mTxtBirthDay.getText().toString();
        //get name room
//        String nameRoom = mSpinNameRoom.getSelectedItem().toString();

        if (TextUtils.isEmpty(phoneNumber)) {
            mEdtPhoneNumber.setError("Vui lòng nhập vào số điện thoại của bạn");
        }
        if (TextUtils.isEmpty(dateOfBirth)) {
            Toast.makeText(getApplicationContext(), "Vui lòng chọn ngày sinh", Toast.LENGTH_SHORT).show();
        } else {

//            User user = new User(phoneNumber,gender,dateOfBirth);
            mUserRef.child("phoneNumber").setValue(phoneNumber);
            mUserRef.child("gender").setValue(gender);
            mUserRef.child("status").setValue("offline");
            mUserRef.child("dateOfBirth").setValue(dateOfBirth).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Lưu thông tin thành công!!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String messgae = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), messgae, Toast.LENGTH_SHORT).show();
                        Log.d("basic", messgae);
                    }
                }
            });
        }
    }
}
