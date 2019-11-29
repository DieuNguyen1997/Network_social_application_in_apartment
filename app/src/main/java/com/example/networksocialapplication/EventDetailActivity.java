package com.example.networksocialapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.adapters.PagerAdapter;
import com.example.networksocialapplication.adapters.PagerEventAdapter;
import com.example.networksocialapplication.adapters.UserAdapter;
import com.example.networksocialapplication.database.UserDatabase;
import com.example.networksocialapplication.models.Event;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.models.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventDetailActivity extends AppCompatActivity {

    private ImageView mImage;
    private TextView mName;
    private TextView mLocation;
    private TextView mTime;
    private TextView mCountInterest;
    private TextView mCountJoin;
    private Button mBtntCare;
    private Toolbar mToolbar;
    private Button mBtnJoin;

    private String mEventId;

    private DatabaseReference mCareEventRef;
    private DatabaseReference mJoinEventf;
    private DatabaseReference mEventRef;
    private DatabaseReference mUserRef;
    private String mCurrentUserId;

    private List<Resident> mCares;
    private List<Resident> mJoins;
    private UserAdapter mAdapter;
    private Animation mAnim_slide_up;
    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        initView();
        initToolbar();
        initFirebase();
        initViewPager();
        getStatusEvent();
        getCountCareEvent();
        getCountJointEvent();
        getEvent();
    }

    private void getCountJointEvent() {
        mJoinEventf.child(mEventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCountJoin.setText(dataSnapshot.getChildrenCount() + " người tham gia");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chi tiết sự kiện");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initFirebase() {
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mEventRef = FirebaseDatabase.getInstance().getReference().child("Events").child(mEventId);
        mCareEventRef = FirebaseDatabase.getInstance().getReference().child("CareEvent");
        mJoinEventf = FirebaseDatabase.getInstance().getReference().child("JoinEvent");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void getCountCareEvent() {
        mCareEventRef.child(mEventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mCountInterest.setText(dataSnapshot.getChildrenCount() + " người quan tâm");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getStatusEvent() {
        mCareEventRef.child(mEventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(mCurrentUserId).exists()) {
                    mBtntCare.setText("Đã quan tâm");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mJoinEventf.child(mEventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(mCurrentUserId).exists()) {
                    mBtnJoin.setText("Đã tham gia");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getEvent() {
        mEventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Event event = dataSnapshot.getValue(Event.class);
                    displayEvent(event);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayEvent(Event event) {
        Glide.with(getApplicationContext()).load(event.getImagePost()).into(mImage);
        mName.setText(event.getNameEvent());
        mTime.setText("Bắt đầu: " + event.getDateStart() + " : " + event.getTimeStar() + " - " + "Kết thúc: " + event.getDateFinish() + " : " + event.getTimeFinish());
        mLocation.setText(event.getLocation());
    }

    private void initView() {
        mLayout = findViewById(R.id.layout_comment_event_detail);
        mAnim_slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_slide_up);
        mEventId = getIntent().getStringExtra("eventId");
        mImage = findViewById(R.id.img_item_event_detail);
        mName = findViewById(R.id.txt_name_item_event_detail);
        mLocation = findViewById(R.id.txt_location_item_event_detail);
        mTime = findViewById(R.id.txt_location_item_event_detail);
        mCountInterest = findViewById(R.id.txt_count_interest_item_event_detail);
        mCountJoin = findViewById(R.id.txt_count_join_item_event_detail);
        mBtntCare = findViewById(R.id.btn_care_item_event_detail);
        mBtnJoin = findViewById(R.id.btn_join_item_event_detail);

        mBtntCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCareEventRef.child(mEventId).child(mCurrentUserId).setValue(true);
                mBtntCare.setText("Đã quan tâm");
            }
        });
        mBtnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJoinEventf.child(mEventId).child(mCurrentUserId).setValue(true);
                mBtnJoin.setText("Đã tham gia");
            }
        });
        mCountInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                View view = getLayoutInflater().inflate(R.layout.dialog_list_care_event, null);
                displayListCareEvent(view);
                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        mCountJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventDetailActivity.this);
                View view = getLayoutInflater().inflate(R.layout.dialog_list_join_event, null);
                displayListJoinEvent(view);
                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void displayListJoinEvent(View view) {
        mJoins = new ArrayList<>();
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view_list_person_join_event);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        mJoinEventf.child(mEventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot data : dataSnapshot.getChildren()){
                    String userId = data.getKey();
                    mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Resident resident = dataSnapshot.getValue(Resident.class);
                            mJoins.add(resident);
                            mAdapter = new UserAdapter(getApplicationContext(),mJoins);
                            mAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(mAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayListCareEvent(View view) {
        mCares = new ArrayList<>();
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view_list_person_care_event);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false));
        mCareEventRef.child(mEventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot data : dataSnapshot.getChildren()){
                    String userId = data.getKey();
                    mUserRef.child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Resident resident = dataSnapshot.getValue(Resident.class);
                            mCares.add(resident);
                            mAdapter = new UserAdapter(getApplicationContext(),mCares);
                            mAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(mAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViewPager() {
        TabLayout tabLayout = findViewById(R.id.taplayout_event_detail);
        tabLayout.addTab(tabLayout.newTab().setText("Giới thiệu"));
        tabLayout.addTab(tabLayout.newTab().setText("Thảo luận"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.viewpager_event_detail);
        PagerEventAdapter adapter = new PagerEventAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
//                mLayout.startAnimation(mAnim_slide_up);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}
