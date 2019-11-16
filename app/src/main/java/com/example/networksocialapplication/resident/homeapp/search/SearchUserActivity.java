package com.example.networksocialapplication.resident.homeapp.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.UserAdapter;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;
    private List<Resident> mUsers;

    private SearchView mSearchView;
    private DatabaseReference mUserData;
    private FirebaseAuth mAuth;

    private String mCurrentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user2);

        initFirebase();
        initRecyclerview();
        initView();
    }

    private void initView() {
        mSearchView = findViewById(R.id.search_view);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query.trim())) {
                    searchUsers(query);
                } else {
                    displayDisplaySearch();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText.trim())) {
                    searchUsers(newText);
                } else {
                    displayDisplaySearch();
                }
                return false;
            }
        });
    }

    private void displayDisplaySearch() {
    }

    private void searchUsers(final String query) {
//        Query query1 = mUserData.orderByChild("username").startAt(query).endAt(query + "\uf8ff");
        mUserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Resident user = snapshot.getValue(Resident.class);
                    Log.d("search", user.toString());
                    if (!user.getResidentId().equals(mCurrentUserID)) {
                        if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                            mUsers.add(user);
                        }
                    }
                }
                mUserAdapter = new UserAdapter(SearchUserActivity.this, mUsers);
                mUserAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Không tìm thấy người dùng này", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRecyclerview() {
        mRecyclerView = findViewById(R.id.recycler_view_list_user_search);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        mUsers = new ArrayList<>();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserID = mAuth.getCurrentUser().getUid();
        mUserData = FirebaseDatabase.getInstance().getReference().child("Users");
    }

}
