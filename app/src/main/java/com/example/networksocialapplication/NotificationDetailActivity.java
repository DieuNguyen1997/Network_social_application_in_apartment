package com.example.networksocialapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.NotificationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.models.NotificationFromManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationDetailActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mTxtTitle;
    private TextView mContent;
    private TextView mTime;
    private ImageView mImage;
    private String mNotificationId;

    private DatabaseReference mNotificationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        initToolbar();
        initView();
        displayNotificationDetail();
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chi tiết thông báo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void displayNotificationDetail() {
        mNotificationId = getIntent().getStringExtra("notificationId");
        mNotificationRef = FirebaseDatabase.getInstance().getReference().child("NotificationFromManager");
        mNotificationRef.child(mNotificationId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NotificationFromManager notificationManager = dataSnapshot.getValue(NotificationFromManager.class);
                    mTime.setText("Thông báo ngày " + notificationManager.getDatePosted() + "lúc " + notificationManager.getTimePosted());
                    String image = notificationManager.getImagePost();
                    if (image == null) {
                        mImage.setVisibility(View.GONE);
                    } else {
                        mImage.setVisibility(View.VISIBLE);
                        Glide.with(getApplicationContext()).load(image).into(mImage);
                    }
                    mTxtTitle.setText(notificationManager.getTitle());
                    mContent.setText(notificationManager.getContentPost());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initView() {
        mTime = findViewById(R.id.txt_time_notification_detail);
        mImage = findViewById(R.id.img_notification_detail);
        mTxtTitle = findViewById(R.id.txt_title_notification_detail);
        mContent = findViewById(R.id.txt_content_notification_detail);
    }
}
