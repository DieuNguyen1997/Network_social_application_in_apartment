package com.example.networksocialapplication.admin.home_manager_fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.networksocialapplication.R;
import com.example.networksocialapplication.ReflectProgressedFragment;
import com.example.networksocialapplication.ReflectReceivedFragment;
import com.example.networksocialapplication.ReflectWaitProgressFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeManagerFragment extends Fragment {
    private BottomNavigationView mBottomNav;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_manager, container, false);
        initBottomNavigationView(view);
        return view;
    }

    private void initBottomNavigationView(View view) {
        mBottomNav = view.findViewById(R.id.bottom_nav_home_fragment_manager);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                switch (menuItem.getItemId()){
                    case R.id.item_bottom_reflect_wait:
                        fragment = new ReflectWaitProgressFragment();
                        break;
                    case R.id.item_bottom_reflect_received:
                        fragment = new ReflectReceivedFragment();
                        break;
                    case R.id.item_bottom_reflect_progressed:
                        fragment = new ReflectProgressedFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.container_home_fragment_manager, fragment).commit();
                return true;
            }
        });
        mBottomNav.setSelectedItemId(R.id.item_bottom_reflect_wait);
    }


}
