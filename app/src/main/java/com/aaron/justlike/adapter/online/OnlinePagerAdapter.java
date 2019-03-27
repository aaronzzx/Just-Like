package com.aaron.justlike.adapter.online;

import com.aaron.justlike.fragment.online.CuratedFragment;
import com.aaron.justlike.fragment.online.RandomFragment;
import com.aaron.justlike.fragment.online.RecommendFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class OnlinePagerAdapter extends FragmentPagerAdapter {

    private static final String[] TITLES = {"推荐", "精选", "随机"};

    public OnlinePagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return new CuratedFragment();
        } else if (position == 2) {
            return new RandomFragment();
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
