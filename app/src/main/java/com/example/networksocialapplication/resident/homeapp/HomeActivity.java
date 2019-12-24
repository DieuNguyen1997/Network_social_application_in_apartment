package com.example.networksocialapplication.resident.homeapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.CreateReflectActivity;
import com.example.networksocialapplication.HotlineActivity;
import com.example.networksocialapplication.ReflectActivity;
import com.example.networksocialapplication.VoteCandidateOfResidentActivity;
import com.example.networksocialapplication.admin.profile_manager.ProfileManagerFragment;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.resident.homeapp.setting_image_profile.SettingImageProfileActivity;
import com.example.networksocialapplication.resident.homeapp.setting_info_profile.SettingInformationProfileActivity;
import com.example.networksocialapplication.resident.homeapp.view_profile_manager.ProfileManagerFromUserActivity;
import com.example.networksocialapplication.user.list_chat_activity.ListChatActivity;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.resident.homeapp.event.EventFragment;
import com.example.networksocialapplication.resident.homeapp.home.HomeFragment;
import com.example.networksocialapplication.resident.homeapp.notifications.NotificationFragment;
import com.example.networksocialapplication.resident.homeapp.search.SearchUserActivity;
import com.example.networksocialapplication.adapters.PagerAdapter;
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


public class HomeActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener {

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
        setContentView(R.layout.activity_home);

        initFirebase();
        checkUser();
        initToolbar();
        initNavigationView();
        initViewPager();

    }



    private void checkUser() {
        mUserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Resident resident = dataSnapshot.getValue(Resident.class);
                String avatar = resident.getAvatar();
                String coverPhoto = resident.getCoverPhoto();
                String des = resident.getDes();
                String dateBirth = resident.getDateOfBirth();
                String gender = resident.getGender();
                String phoneNumber = resident.getPhoneNumber();
                String username = resident.getUsername();

                if (TextUtils.isEmpty(avatar) || TextUtils.isEmpty(coverPhoto) || TextUtils.isEmpty(username) || TextUtils.isEmpty(des) || TextUtils.isEmpty(resident.getResidentId())) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa cài đặt thông tin cá nhân", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), SettingImageProfileActivity.class));
                } else if (TextUtils.isEmpty(dateBirth) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(des)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa cài đặt thông tin cá nhân", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), SettingInformationProfileActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mUserData = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserId);
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
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigationview);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.nav_item_home:
                        HomeFragment fragment = new HomeFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container_home, fragment);
                        transaction.commit();
                        break;
                    case R.id.nav_item_apartment:
                        startActivity(new Intent(getApplicationContext(), VoteCandidateOfResidentActivity.class));
                        break;
                    case R.id.nav_item_event:
                        EventFragment eventFragment = new EventFragment();
                        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
                        transaction1.replace(R.id.container_home, eventFragment);
                        transaction1.commit();
                        break;
                    case R.id.nav_item_hotline:
                        startActivity(new Intent(getApplicationContext(), HotlineActivity.class));
                        break;
                    case R.id.nav_item_logout:
                        mAuth.signOut();
                        updateUI(null);
                        break;
                    case R.id.nav_item_manager:
                        startActivity(new Intent(getApplicationContext(), ProfileManagerFromUserActivity.class));
                        break;
                    case R.id.nav_item_notification:
                        NotificationFragment notificationFragment = new NotificationFragment();
                        FragmentTransaction notifyFragment = getSupportFragmentManager().beginTransaction();
                        notifyFragment.replace(R.id.container_home, notificationFragment);
                        notifyFragment.commit();
                        break;
                    case R.id.nav_item_reflect:
                        startActivity(new Intent(getApplicationContext(), ReflectActivity.class));
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
                if (dataSnapshot.exists()) {
                    Resident resident = dataSnapshot.getValue(Resident.class);
                    String username = resident.getUsername();
                    String avatar = resident.getAvatar();
                    if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(avatar)) {
                        mTxtUsername.setText(username);
                        if (isValidContextForGlide(HomeActivity.this)) {
                            Glide.with(HomeActivity.this).load(avatar).into(mAvatar);
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Không có thông tin của người dùng này", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_home);
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
        TabLayout tabLayout = findViewById(R.id.taplayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_supervisor_account_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_event_background_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_notifications_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_person_black_24dp));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.viewpager);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_home_toolbar_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_search:
                Intent intent = new Intent(HomeActivity.this, SearchUserActivity.class);
                startActivity(intent);
                return true;
            case R.id.item_reflect:
                Intent intentChat = new Intent(HomeActivity.this, ListChatActivity.class);
                startActivity(intentChat);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void status(String status) {
        mUserData.child("status").setValue(status);
    }

    @Override
    protected void onStart() {
        super.onStart();
        status("online");
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
