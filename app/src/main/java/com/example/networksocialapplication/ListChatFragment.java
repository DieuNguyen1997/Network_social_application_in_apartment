package com.example.networksocialapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.networksocialapplication.adapters.ChatAdapter;
import com.example.networksocialapplication.adapters.UserAdapter;
import com.example.networksocialapplication.database.MessageDatabase;
import com.example.networksocialapplication.models.Message;
import com.example.networksocialapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;


public class ListChatFragment extends Fragment {
    private final static String TAG = "chat";
    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;
    private List<User> mUsers;
    private List<String> mUserIdList;

    private String mCurrentUserId;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUserDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_chat, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view_list_chat);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        mUsers = new ArrayList<>();
        mUserIdList = new ArrayList<>();
        initFirebase();
        displayListChat();
        return view;
    }

    private void initFirebase() {
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Chats");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void displayListChat() {
        mMessageDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (final DataSnapshot data : dataSnapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    if (message.getSenderId().equals(mCurrentUserId)) {
                        mUserIdList.add(message.getReceivedId());
                    }
                    if (message.getReceivedId().equals(mCurrentUserId)) {
                        mUserIdList.add(message.getSenderId());
                    }
                }
                readChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void readChat() {
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //get all usser
                    User user = data.getValue(User.class);

                    for (String userid : mUserIdList) {
                        //check user cos id trong lisst mUserIdList
                        if (user.getUserID().equals(userid)){

                            if (mUsers.size() != 0){
                                for (int i  = 0; i < mUsers.size(); i++){
                                    User userCheck = mUsers.get(i);
                                    if (!userCheck.getUserID().equals(user.getUserID())){
                                        mUsers.add(user);
                                    }
                                }
                            } else {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                mChatAdapter = new ChatAdapter(getActivity(), mUsers,true);
                mChatAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mChatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
