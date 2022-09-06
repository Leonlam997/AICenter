package com.leon.aicenter.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.leon.aicenter.fragment.ScenesFragment;

import java.util.List;

public class ScenePagerAdapter extends FragmentStateAdapter {
    List<ScenesFragment> fragmentList;

    public ScenePagerAdapter(@NonNull FragmentActivity fragmentActivity, List<ScenesFragment> fragmentList) {
        super(fragmentActivity);
        this.fragmentList = fragmentList;
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
