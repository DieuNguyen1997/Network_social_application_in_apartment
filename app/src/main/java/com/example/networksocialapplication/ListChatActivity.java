package com.example.networksocialapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.database.UserDatabase;
import com.example.networksocialapplication.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private BottomNavigationView mBottomNavigationView;
    private CircleImageView mAvatarChat;

    private String mCurrentUserId;
    private UserDatabase mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat);

        initToolbar();
        initBottonnav();
        initView();

    }

    private void initView() {
        mAvatarChat = findViewById(R.id.img_avatar_list_chat);
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserInfor();
    }

    private void getUserInfor() {
        mUserRef = new UserDatabase(getApplicationContext());
        mUserRef.setUpUserRef();
        mUserRef.getAvatarUser(mCurrentUserId, mAvatarChat);

    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_list_chat);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initBottonnav() {
        mBottomNavigationView = findViewById(R.id.bottom_nav_chat);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item_bottom_chat:
                        ListChatFragment fragment1 = new ListChatFragment();
                        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                        transaction1.replace(R.id.container_list_chat, fragment1);
                        transaction1.commit();
                        return true;
                    case R.id.item_bottom_operating_status:
                        OveratStatusFragment fragment = new OveratStatusFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container_list_chat, fragment);
                        transaction.commit();
                        return true;
                }
                return false;
            }
        });
    }
}
