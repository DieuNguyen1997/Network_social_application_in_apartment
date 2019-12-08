package com.example.networksocialapplication.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.ChatManagerActivity;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.PagerAdapter;
import com.example.networksocialapplication.adapters.PagerHomeAdapter;
import com.example.networksocialapplication.admin.event_manager_fragment.EventMangerFragment;
import com.example.networksocialapplication.admin.home_manager_fragment.HomeManagerFragment;
import com.example.networksocialapplication.admin.notification_manager_fragment.NotificationManagerFragment;
import com.example.networksocialapplication.resident.homeapp.HomeActivity;
import com.example.networksocialapplication.resident.homeapp.event.EventFragment;
import com.example.networksocialapplication.resident.homeapp.home.HomeFragment;
import com.example.networksocialapplication.resident.homeapp.notifications.NotificationFragment;
import com.example.networksocialapplication.resident.homeapp.search.SearchUserActivity;
import com.example.networksocialapplication.user.list_chat_activity.ListChatActivity;
import com.example.networksocialapplication.user.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeManagerActivity extends AppCompatActivity {

    private CircleImageView mAvatar;
    private TextView mTxtUsername;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;


    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private DatabaseReference mUserData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_manager);

        initFirebase();
        initToolbar();
        initNavigationView();
        initViewPager();
    }


    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mUserData = FirebaseDatabase.getInstance().getReference().child("Manager").child(mCurrentUserId);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(getApplicationContext(), "Đã đăng nhập", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void initNavigationView() {
        mDrawerLayout = findViewById(R.id.drawer_layout_home_manager);
        mNavigationView = findViewById(R.id.navigationview_home_manager);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.nav_item_home:
                        HomeManagerFragment fragment = new HomeManagerFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container_home_manager, fragment);
                        transaction.commit();
                        break;
                    case R.id.nav_item_apartment:
                        break;
                    case R.id.nav_item_event:
                        EventMangerFragment eventFragment= new EventMangerFragment();
                        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                        transaction1.replace(R.id.container_home_manager, eventFragment);
                        transaction1.commit();
                        break;
                    case R.id.nav_item_hotline:
                        break;
                    case R.id.nav_item_logout:
                        mAuth.signOut();
                        updateUI(null);
                        break;
                    case R.id.nav_item_manager:
                        break;
                    case R.id.nav_item_notification:
                        NotificationManagerFragment notificationFragment= new NotificationManagerFragment();
                        FragmentTransaction notifyFragment = getSupportFragmentManager().beginTransaction();
                        notifyFragment.replace(R.id.container_home_manager, notificationFragment);
                        notifyFragment.commit();
                        break;
                    case R.id.nav_item_reflect:

                        break;
                }
                return false;
            }
        });
        View navView = mNavigationView.inflateHeaderView(R.layout.activity_home_nav_header);
        mAvatar = navView.findViewById(R.id.img_avatar_nav_header);
        mTxtUsername = navView.findViewById(R.id.txt_username_nav_header);

        mUserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String username = dataSnapshot.child("username").getValue().toString();
                    String avatar = dataSnapshot.child("avatar").getValue().toString();
                    mTxtUsername.setText(username);
                    Glide.with(HomeManagerActivity.this).load(avatar).into(mAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_home_manager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.home_toolbar_title);
        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNavigationView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initViewPager() {
        TabLayout tabLayout = findViewById(R.id.taplayout_home_manager);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_event_background_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_notifications_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_person_black_24dp));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.viewpager_home_manager);
        PagerHomeAdapter adapter = new PagerHomeAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //change color icon in tablayout when select view pager
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.tabSelectedIconColor);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                int tabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.tabUnselectedIconColor);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_home_manager_toolbar_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item_reflect:
                Intent intentChat = new Intent(HomeManagerActivity.this, ChatManagerActivity.class);
                startActivity(intentChat);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void status(String status){
        mUserData.child("status").setValue(status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
