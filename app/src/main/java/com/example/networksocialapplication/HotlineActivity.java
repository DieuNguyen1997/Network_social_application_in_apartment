package com.example.networksocialapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.models.Manager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HotlineActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mPhoneNumber;
    private TextView mRoom;

    private DatabaseReference mManagerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotline);

        initToolbar();
        displayInforBasic();
    }

    private void displayInforBasic() {
        mPhoneNumber = findViewById(R.id.txt_phonenumber_hotline);
        mRoom = findViewById(R.id.tzt_room_hotline);
        DatabaseReference mManagerDatabase = FirebaseDatabase.getInstance().getReference().child("Manager");
        mManagerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Manager user = data.getValue(Manager.class);
                    mPhoneNumber.setText(user.getPhoneNumber());
                    mRoom.setText(user.getLocation());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Hotline");
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

