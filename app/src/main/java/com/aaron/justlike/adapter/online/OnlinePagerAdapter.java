package com.aaron.justlike.adapter.online;

import com.aaron.justlike.fragment.CuratedFragment;
import com.aaron.justlike.fragment.RecommendFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class OnlinePagerAdapter extends FragmentPagerAdapter {

    private static final String[] TITLES = {"推荐", "精选"};

    public OnlinePagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new CuratedFragment();
        }
        return new RecommendFragment();
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }
}
