package com.aaron.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import io.reactivex.annotations.Nullable;

public abstract class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitleArr;

    public BaseFragmentPagerAdapter(FragmentManager manager, String[] titleArr) {
        super(manager);
        mTitleArr = titleArr;
    }

    public abstract Fragment getFragment(int position);

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return getFragment(position);
    }

    @Override
    public int getCount() {
        return mTitleArr.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleArr[position];
    }
}
