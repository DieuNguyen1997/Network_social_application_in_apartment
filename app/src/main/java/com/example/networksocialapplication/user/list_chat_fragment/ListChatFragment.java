package com.example.networksocialapplication.user.list_chat_fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.networksocialapplication.R;
import com.example.networksocialapplication.adapters.ChatAdapter;
import com.example.networksocialapplication.adapters.ChatManagerAdapter;
import com.example.networksocialapplication.models.Manager;
import com.example.networksocialapplication.models.Message;
import com.example.networksocialapplication.models.Resident;
import com.example.networksocialapplication.models.User;
import com.example.networksocialapplication.user.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;


public class ListChatFragment extends Fragment {
    private final static String TAG = "chat";
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewManager;
    private ChatAdapter mChatAdapter;
    private List<Resident> mUsers;
    private List<Manager> mManagers;
    private List<String> mUserIdList;

    private String mCurrentUserId;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mTokenRef;
    private DatabaseReference mMangerDatabase;
    private ChatManagerAdapter mChatManagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_chat, container, false);
        mUsers = new ArrayList<>();
        mUserIdList = new ArrayList<>();
        mManagers = new ArrayList<>();
        initRecyclerview(view);
        initRecyclerviewManager(view);
        initFirebase();
        displayListChat();
        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }

    private void initRecyclerviewManager(View view) {
        mRecyclerViewManager = view.findViewById(R.id.recycler_view_list_chat_from_manager);
        mRecyclerViewManager.setHasFixedSize(true);
        mRecyclerViewManager.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
    }

    private void initRecyclerview(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_list_chat_fragment);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));

    }

    private void updateToken(String token) {
        Token token1 = new Token(token);
        mTokenRef.child(mCurrentUserId).setValue(token1);

    }

    private void initFirebase() {
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("Chats");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMangerDatabase = FirebaseDatabase.getInstance().getReference().child("Manager");
        mTokenRef = FirebaseDatabase.getInstance().getReference().child("Tokens");
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
                readChatUser();
//                readChatManager();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void readChatUser() {
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //get all usser
                    Resident user = data.getValue(Resident.class);

                    for (String userid : mUserIdList) {
                        //check user cos id trong lisst mUserIdList
                        if (user.getResidentId().equals(userid)) {

                            if (mUsers.size() != 0) {
                                for (int i = 0; i < mUsers.size(); i++) {
                                    Resident userCheck = mUsers.get(i);
                                    if (!userCheck.getResidentId().equals(user.getResidentId())) {
                                        mUsers.add(user);
                                    }
                                }
                            } else {
                                mUsers.add(user);
                            }
                        }else {
                            readChatManager();
                        }
                    }
                }
                mChatAdapter = new ChatAdapter(getActivity(), mUsers, true);
                mChatAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mChatAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void readChatManager() {
        mMangerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mManagers.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    //get all usser
                    Manager manager = data.getValue(Manager.class);

                    for (String userid : mUserIdList) {
                        //check user cos id trong lisst mUserIdList
                        if (manager.getManagerId().equals(userid)) {

                            if (mManagers.size() != 0) {
                                for (int i = 0; i < mManagers.size(); i++) {
                                    Manager userCheck = mManagers.get(i);
                                    if (!userCheck.getManagerId().equals(manager.getManagerId())) {
                                        mManagers.add(manager);
                                    }
                                }
                            } else {
                                mManagers.add(manager);
                            }
                       }
                    }
                }
                mChatManagerAdapter = new ChatManagerAdapter(getActivity(), mManagers, true);
                mChatManagerAdapter.notifyDataSetChanged();
                mRecyclerViewManager.setAdapter(mChatManagerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
