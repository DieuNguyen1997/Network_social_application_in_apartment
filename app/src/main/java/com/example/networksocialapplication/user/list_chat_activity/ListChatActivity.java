package com.example.networksocialapplication.user.list_chat_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.networksocialapplication.user.list_chat_fragment.ListChatFragment;
import com.example.networksocialapplication.user.list_overate_status.OveratStatusFragment;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.database.UserDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

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
        mToolbar = findViewById(R.id.toolbar_list_chat_activity);
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
                Fragment fragment = null ;
                FragmentManager fragmentManager = getSupportFragmentManager();
                switch (menuItem.getItemId()) {
                    case R.id.item_bottom_chat:
                        fragment = new ListChatFragment();
                        break;
                    case R.id.item_bottom_operating_status:
                      fragment = new OveratStatusFragment();
                        break;
//                    default:
//                        fragment = new ListChatFragment();
//                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.container_list_chat, fragment).commit();
                return true;
            }
        });
        mBottomNavigationView.setSelectedItemId(R.id.item_bottom_chat);
    }

}
