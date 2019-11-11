package com.example.networksocialapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.networksocialapplication.adapters.ReflectAdapter;
import com.example.networksocialapplication.models.Reflect;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReflectActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private ReflectAdapter mReflectAdapter;
    private List<Reflect> mReflects;

    private DatabaseReference mReflectRef;
    private String mCurrentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflect);

        initToolbar();
        initFirebase();
        initRecyclerview();
//        displayListReflect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar_reflect_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add_reflect:
                Intent intent = new Intent(getApplicationContext(), CreateReflectActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    private void displayListReflect() {
        mReflectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String userID = data.child("userId").getValue().toString();
                    Reflect reflect = data.getValue(Reflect.class);
                    if (userID.equals(mCurrentUserId)) {
                        mReflects.add(reflect);
                        mReflectAdapter = new ReflectAdapter(getApplicationContext(), mReflects);
                        mReflectAdapter.notifyDataSetChanged();
                        mRecyclerView.setAdapter(mReflectAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initFirebase() {
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mReflectRef = FirebaseDatabase.getInstance().getReference().child("Reflect");
    }

    private void initRecyclerview() {
        mReflects = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recycler_view_list_reflect_of_user);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Phản ánh của bạn");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
