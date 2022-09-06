package com.leon.aicenter.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.leon.aicenter.fragment.DevicesFragment;

import java.util.List;

public class DevicePagerAdapter extends FragmentStateAdapter {
    List<DevicesFragment> fragmentList;

    public DevicePagerAdapter(@NonNull FragmentActivity fragmentActivity, List<DevicesFragment> fragmentList) {
        super(fragmentActivity);
        this.fragmentList = fragmentList;
    }

    @Override
    public boolean containsItem(long itemId) {
        return itemId >= 0 && itemId < getItemCount();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
