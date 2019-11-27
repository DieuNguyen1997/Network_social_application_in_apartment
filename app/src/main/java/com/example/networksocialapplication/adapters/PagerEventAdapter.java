package com.example.networksocialapplication.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.networksocialapplication.CommentEventFragment;
import com.example.networksocialapplication.DesEventFragment;

public class PagerEventAdapter extends FragmentStatePagerAdapter {
    private int mNoOfTap;

    public PagerEventAdapter(@NonNull FragmentManager fm, int numberOfTab) {
        super(fm);
        this.mNoOfTap = numberOfTab;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                DesEventFragment desEventFragment = new DesEventFragment();
                return desEventFragment;
            case 1:
                CommentEventFragment commentEventFragment = new CommentEventFragment();
                return commentEventFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTap;
    }
}
