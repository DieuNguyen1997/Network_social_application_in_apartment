package com.example.networksocialapplication.admin.create_election;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.networksocialapplication.AddCandidateActivity;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.VoteCandidateOfResidentActivity;
import com.example.networksocialapplication.adapters.VoteCandidateAdapter;
import com.example.networksocialapplication.models.Candidate;
import com.example.networksocialapplication.models.Election;
import com.example.networksocialapplication.models.NotificationFromManager;
import com.example.networksocialapplication.time_current.Time;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateElectionActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private View mCreateListCandidate;
    private Button mCreateNotify;
    private TextView mCount;
    private Time mTime;
    private String mYear;

    private View mlayoutCreateCandidate;
    private View mLayoutFollow;
    private RecyclerView mRecyclerView;
    private Button mBtnStop;

    private DatabaseReference mElectionRef;
    private DatabaseReference mCandidateRef;
    private List<Candidate> mCandidates;
    private FollowVoteCandidateAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_election);
        initToolbar();
        iniFirebase();
        initRecylerview();
        setUpLayout();
        initView();

    }

    private void initRecylerview() {
        mCandidates = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recycler_view_list_follow_vote_candidate);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
    }

    private void iniFirebase() {
        mElectionRef= FirebaseDatabase.getInstance().getReference().child("Elections");
        mCandidateRef = FirebaseDatabase.getInstance().getReference().child("Candidates");
    }

    private void setUpLayout() {
        mlayoutCreateCandidate = findViewById(R.id.layout_create_candidate);
        mLayoutFollow = findViewById(R.id.layout_follow_vote_candidate);

        mElectionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(mYear).exists()){
                    mLayoutFollow.setVisibility(View.VISIBLE);
                    displayVoteCandidate();
                }else {
                   mlayoutCreateCandidate.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayVoteCandidate() {
        
        mCandidateRef.child(mYear).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Candidate candidate = data.getValue(Candidate.class);
                    mCandidates.add(candidate);
                }
                mAdapter = new FollowVoteCandidateAdapter(CreateElectionActivity.this, mCandidates);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void displayCountCandidate() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Candidates").child(String.valueOf(mYear));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mCount.setText(dataSnapshot.getChildrenCount() + "");
                    if (mCount.getText().toString().equals("10")) {
                        mCreateListCandidate.setVisibility(View.GONE);
                        mCreateNotify.setVisibility(View.VISIBLE);
                    }
                } else {
                    mCount.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initToolbar() {
        Calendar calendar = Calendar.getInstance();
        mYear = String.valueOf(calendar.get(Calendar.YEAR));
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Bầu cử năm " + mYear);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        mTime = new Time();
        mCreateNotify = findViewById(R.id.btn_create_notify);
        mCreateListCandidate = findViewById(R.id.btn_create_list_candidate);
        mCount = findViewById(R.id.txt_count_candidate);
        mBtnStop = findViewById(R.id.btn_stop_vote_candidate);
        displayCountCandidate();

        mCreateListCandidate.setOnClickListener(this);
        mCreateNotify.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
    }

    private void createElection() {
        DatabaseReference electionRef = FirebaseDatabase.getInstance().getReference().child("Elections");
        Election election = new Election(mYear, mYear);
        electionRef.child(mYear).setValue(election).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    addNotificaiton();
                }
            }
        });

    }

    public void addNotificaiton() {
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("NotificationFromManager");
        String notificationId = data.push().getKey();
        String content = "Mỗi người là một cư dân hãy bầu ra 5 người xuất sắc nhất để làm ban quản lý khu chung cư.Vào trang bầu cử ở tài khoản của mình để bình chọn. Trân trọng";
        String title = "Bầu ban quản lý khu chung cư năm " + mYear;
        NotificationFromManager notification = new NotificationFromManager(mTime.getDateCurrent(), mTime.getTimeHourCurrent(), content, notificationId, title);
        data.child(notificationId).setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(CreateElectionActivity.this, "Đã gửi bầu cử đến ban quản lý", Toast.LENGTH_SHORT).show();
            }
        });
        mlayoutCreateCandidate.setVisibility(View.GONE);
        mLayoutFollow.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_create_list_candidate:
                Intent intent = new Intent(getApplicationContext(), AddCandidateActivity.class);
                intent.putExtra("electionId", mYear);
                startActivity(intent);
                break;
            case R.id.btn_create_notify:
                createElection();
                break;
            case R.id.btn_stop_vote_candidate:
                addNotificaitonStop();
                break;
        }
    }

    private void addNotificaitonStop() {
        DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("NotificationFromManager");
        String notificationId = data.push().getKey();
        String content = "Hạn bầu cử ban quản lý khu chung cư năm "+mYear+" đã chính thức khép lại. Xin chân thành cảm ơn các cư dân đã tham gia, đóng góp ý kiến của mình để chúng tôi có thể dựa vào để tìm ra ban quản lý mới của khu chung cư";
        String title = "Bầu ban quản lý khu chung cư năm " + mYear;
        NotificationFromManager notification = new NotificationFromManager(mTime.getDateCurrent(), mTime.getTimeHourCurrent(), content, notificationId, title);
        data.child(notificationId).setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(CreateElectionActivity.this, "Đã gửi bầu cử đến ban quản lý", Toast.LENGTH_SHORT).show();
            }
        });
        mlayoutCreateCandidate.setVisibility(View.GONE);
        mLayoutFollow.setVisibility(View.VISIBLE);
    }

}
