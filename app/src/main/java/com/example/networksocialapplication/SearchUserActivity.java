package com.example.networksocialapplication;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.networksocialapplication.adapters.UserAdapter;
import com.example.networksocialapplication.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;
    private List<User> mUsers;

    private EditText mEdtInput;
    private DatabaseReference mUserData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user2);

        initFirebase();
        initRecyclerview();
        initView();
    }

    private void initView() {
        mEdtInput = findViewById(R.id.edt_search_user);
        mEdtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firebaseSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void firebaseSearch() {
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(mUserData, User.class)
                        .build();
        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
                holder.setDetails(getApplicationContext(),model.getAvatarUrl(), model.getUserName(), model.getNameRoom());
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.activity_search_user_item_list_user, parent, false);
                return new UserViewHolder(view);
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void initRecyclerview() {
        mRecyclerView = findViewById(R.id.recycler_view_list_user_search);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        mUsers = new ArrayList<>();
    }

    private void initFirebase() {
        mUserData = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UserViewHolder(@NonNull View itemView) {

            super(itemView);
            mView = itemView;
        }

        public void setDetails(Context context,String avatarUrl, String username, String nameroom){
            CircleImageView avatar = mView.findViewById(R.id.img_avatar_item_user_search);
            TextView user_name = mView.findViewById(R.id.txt_username_item_search);
            TextView name_room = mView.findViewById(R.id.txt_name_room_item_search);

            Glide.with(context).load(avatarUrl).into(avatar);
            user_name.setText(username);
            name_room.setText(nameroom);
        }
    }


}
