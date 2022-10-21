package com.anurag.instagram.Adapter;


import com.anurag.instagram.Fragments.HomeFragment;
import com.anurag.instagram.Fragments.MyPostFragment;
import com.anurag.instagram.Fragments.ProfileFragment;
import com.anurag.instagram.Fragments.SavedPostFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabLayoutAdapter extends FragmentStateAdapter {

    public TabLayoutAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MyPostFragment();
            case 1:
                return new SavedPostFragment();
            default:
                return new ProfileFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
