package com.leon.aicenter.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.leon.aicenter.fragment.DevicesFragment;
import com.leon.aicenter.fragment.ScenesFragment;

public class RoomPagerAdapter extends FragmentStateAdapter {
    DevicesFragment devicesFragment;
    ScenesFragment scenesFragment;

    public RoomPagerAdapter(@NonNull FragmentActivity fragmentActivity, DevicesFragment devicesFragment, ScenesFragment scenesFragment) {
        super(fragmentActivity);
        this.devicesFragment = devicesFragment;
        this.scenesFragment = scenesFragment;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? devicesFragment : scenesFragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
