package com.example.networksocialapplication.resident.homeapp.setting_info_profile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.networksocialapplication.models.Room;
import com.example.networksocialapplication.resident.homeapp.HomeActivity;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SettingInformationProfileActivity extends AppCompatActivity {

    private Button mBtnSave;
    private EditText mEdtPhoneNumber;
    private TextInputEditText mTxtBirthDay;
    private ImageButton mBtnCalender;
    private Spinner mSpinNameRoom;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private RadioButton mMale, mFemale, mOther;
    private RadioGroup mRadioGroup;

    private DatePicker mDatePicker;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private DatabaseReference mRoomRef;
    private String mCurrentUserID;
    private Toolbar mToolbar;
    private List<String> mRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_information_profile);

        initToolbar();
        inirFirebase();
        initView();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Cài đặt thông tin cá nhân");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void inirFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserID);
        mRoomRef = FirebaseDatabase.getInstance().getReference().child("Rooms");
    }

    private void initView() {
        mRooms = new ArrayList<>();
        mDatePicker = new DatePicker(this);
        mRadioGroup = findViewById(R.id.radio_group_gender);
        mEdtPhoneNumber = findViewById(R.id.edt_phone_setting_information);
        mTxtBirthDay = findViewById(R.id.setting_information_txt_birthday);
        mSpinNameRoom = findViewById(R.id.spinner_name_room_setting_information);
        mBtnSave = findViewById(R.id.btn_save_setting_information);
        mMale = findViewById(R.id.radio_btn_male);
        mFemale = findViewById(R.id.radio_btn_female);
        mOther = findViewById(R.id.radio_btn_other);

        setDataToSpinner();
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatUser();

            }
        });
        mTxtBirthDay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT_TIME_FINISH = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mTxtBirthDay.getRight() - mTxtBirthDay.getCompoundDrawables()[DRAWABLE_RIGHT_TIME_FINISH].getBounds().width())) {
                        mDatePicker.showDatePicker(mTxtBirthDay);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void setDataToSpinner() {
        mRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Room room = data.getValue(Room.class);
                    String nameRoom = room.getName();
                    mRooms.add(nameRoom);

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, mRooms);
                mSpinNameRoom.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void creatUser() {
        //get phone number
        String phoneNumber = mEdtPhoneNumber.getText().toString();
        int idChecked = mRadioGroup.getCheckedRadioButtonId();
        View radioButton = findViewById(idChecked);
        int index = mRadioGroup.indexOfChild(radioButton);
        RadioButton r = (RadioButton) mRadioGroup.getChildAt(index);
        String gender = "";
        //get dat of birth
        String dateOfBirth = mTxtBirthDay.getText().toString();
        //get name room
        String room = mSpinNameRoom.getSelectedItem().toString();

        if (!TextUtils.isEmpty(phoneNumber)) {
            if (!TextUtils.isEmpty(dateOfBirth)) {
                if (r != null) {
                    gender = r.getText().toString();
//            User user = new User(phoneNumber,gender,dateOfBirth);
                    mUserRef.child("phoneNumber").setValue(phoneNumber);
                    mUserRef.child("gender").setValue(gender);
                    mUserRef.child("status").setValue("offline");
                    mUserRef.child("room").setValue(room);
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
                } else
                    Toast.makeText(getApplicationContext(), "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();

            } else
                Toast.makeText(getApplicationContext(), "Vui lòng chọn ngày sinh", Toast.LENGTH_SHORT).show();

        } else mEdtPhoneNumber.setError("Vui lòng nhập vào số điện thoại của bạn");

    }
}
