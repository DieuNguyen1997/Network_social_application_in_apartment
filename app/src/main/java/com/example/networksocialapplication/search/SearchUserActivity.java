package com.example.networksocialapplication.search;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.UserAdapter;
import com.example.networksocialapplication.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;
    private List<User> mUsers;

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

                    User user = snapshot.getValue(User.class);
                    Log.d("search", user.toString());
                    if (!user.getUserID().equals(mCurrentUserID)) {
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

//    private void firebaseSearch(String s) {
//        Query query = mUserData.orderByChild("username").startAt(s).endAt(s + "\uf8ff");
//        FirebaseRecyclerOptions<User> options =
//                new FirebaseRecyclerOptions.Builder<User>()
//                        .setQuery(query, User.class)
//                        .build();
//        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
//                holder.setDetails(getApplicationContext(), model.getAvatar(), model.getUsername());
//            }
//
//            @NonNull
//            @Override
//            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//                View view = inflater.inflate(R.layout.item_list_search_user, parent, false);
//                return new UserViewHolder(view);
//            }
//        };
//        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
//    }

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

//    public static class UserViewHolder extends RecyclerView.ViewHolder {
//
//        View mView;
//
//        public UserViewHolder(@NonNull View itemView) {
//
//            super(itemView);
//            mView = itemView;
//        }
//
//        public void setDetails(Context context, String avatarUrl, String username) {
//            CircleImageView avatar = mView.findViewById(R.id.img_avatar_item_user_search);
//            TextView user_name = mView.findViewById(R.id.txt_username_item_search);
//            TextView name_room = mView.findViewById(R.id.txt_name_room_item_search);
//
//            Glide.with(context).load(avatarUrl).into(avatar);
//            user_name.setText(username);
//        }
//    }


}
