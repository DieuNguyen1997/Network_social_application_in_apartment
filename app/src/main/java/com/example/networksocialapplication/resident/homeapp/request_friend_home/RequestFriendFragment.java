package com.example.networksocialapplication.resident.homeapp.request_friend_home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.networksocialapplication.R;
import com.example.networksocialapplication.resident.homeapp.list_friend.ListFriendFragment;
import com.example.networksocialapplication.resident.homeapp.list_request_friend.ListRequestFriendFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class RequestFriendFragment extends Fragment {


    private BottomNavigationView mBottomNavigationView;

    public RequestFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_friend, container, false);
        mBottomNavigationView = view.findViewById(R.id.bottom_nav_friends);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.item_list_friend:
                        ListFriendFragment listFriendFragment = new ListFriendFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.layout_contain_request_friend, listFriendFragment);
                        transaction.commit();
                        return true;
                    case R.id.item_list_request_friend:
                        ListRequestFriendFragment requestFriendFragment = new ListRequestFriendFragment();
                        FragmentTransaction transaction1 = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction1.replace(R.id.layout_contain_request_friend, requestFriendFragment);
                        transaction1.commit();
                        return true;
                }
                return false;
            }
        });
        return view;
    }


}
