package com.example.networksocialapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.networksocialapplication.R;

public class SettingImageProfileActivity extends AppCompatActivity {


    private Button mBtnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_image_profile);

        initView();

    }

    private void initView() {

        mBtnNext = findViewById(R.id.btn_next_setting_profile);

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingInformationProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}



