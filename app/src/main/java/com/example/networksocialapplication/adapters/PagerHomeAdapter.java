package com.example.networksocialapplication.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.networksocialapplication.admin.event_manager_fragment.EventMangerFragment;
import com.example.networksocialapplication.admin.home_manager_fragment.HomeManagerFragment;
import com.example.networksocialapplication.admin.notification_manager_fragment.NotificationManagerFragment;
import com.example.networksocialapplication.admin.profile_manager.ProfileManagerFragment;


public class PagerHomeAdapter extends FragmentStatePagerAdapter {

    private int mNoOfTab;

    public PagerHomeAdapter(@NonNull FragmentManager fm, int numberOfTab) {
        super(fm);
        this.mNoOfTab = numberOfTab;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                HomeManagerFragment homeManagerFragment = new HomeManagerFragment();
                return homeManagerFragment;
            case 1:
                EventMangerFragment eventMangerFragment = new EventMangerFragment();
                return eventMangerFragment;
            case 2:
                NotificationManagerFragment notificationManagerFragment = new NotificationManagerFragment();
                return notificationManagerFragment;
            case 3:
                ProfileManagerFragment profileManagerFragment = new ProfileManagerFragment();
                return profileManagerFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTab;
    }
}

