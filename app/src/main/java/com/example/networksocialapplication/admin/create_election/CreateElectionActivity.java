package com.example.networksocialapplication.admin.create_election;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.example.networksocialapplication.R;

public class CreateElectionActivity extends AppCompatActivity {

    private EditText mEditTextYear;
    private Toolbar mToolbar;
    private ImageView mAvatar;
    private ImageButton mCamera;
    private EditText mName;
    private EditText mAddressApartment;
    private EditText mDateBirth;
    private EditText mJob;
    private EditText mYearStart;
    private EditText mSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_election);
        initToolbar();
        initView();
    }

    private void initToolbar() {

    }

    private void initView() {
//        mEditTextYear = findViewById(R.id.edt)
    }
}
