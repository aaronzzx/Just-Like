package com.aaron.justlike.activity.online;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.online.OnlinePagerAdapter;
import com.aaron.justlike.common.ThemeManager;
import com.aaron.justlike.fragment.PhotoFragment;
import com.aaron.justlike.util.SystemUtils;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class OnlineActivity extends AppCompatActivity implements View.OnClickListener {

    private ThemeManager.Theme mCurrentTheme;

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private TabLayout.Tab mTab1;
    private TabLayout.Tab mTab2;
    private ViewPager mViewPager;

    private FragmentPagerAdapter mPagerAdapter;
    private FragmentManager mFragmentManager;
    private ActionBar mActionBar;
    private Drawable mIconBack;
    private Drawable mIconSearch;
    private Drawable mIconFilter;

    private int mColorPrimary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        mCurrentTheme = ThemeManager.getInstance().getCurrentTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        initView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = getWindow();
        View decorView = window.getDecorView();
        if (hasFocus) {
            ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
            if (theme == null || theme == ThemeManager.Theme.WHITE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    window.setStatusBarColor(getResources().getColor(R.color.status_bar_background));
                }
                mToolbar.setTitleTextColor(getResources().getColor(R.color.colorGreyText));
                mActionBar.setHomeAsUpIndicator(mIconBack);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_online_menu, menu);
        SystemUtils.setIconEnable(menu, true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_online_toolbar:
                List<Fragment> fragments = mFragmentManager.getFragments();
                for (Fragment fragment : fragments) {
                    if (fragment.getUserVisibleHint()) {
                        ((PhotoFragment) fragment).backToTop();
                        return;
                    }
                }
                break;
        }
    }

    /**
     * Called by fragment.
     */
    public int getColorPrimary() {
        return mColorPrimary;
    }

    private void initView() {
        mToolbar = findViewById(R.id.activity_online_toolbar);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);

        mToolbar.setOnClickListener(this);

        initTheme();
        initIconColor();
        initToolbar();
        initTabLayout();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initIconColor() {
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            mTabLayout.setTabTextColors(getResources().getColor(R.color.colorAccentWhite), getResources().getColor(R.color.colorAccentWhite));
            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccentWhite));
            mIconBack = getResources().getDrawable(R.drawable.ic_back);
            mIconSearch = getResources().getDrawable(R.drawable.ic_search);
            mIconFilter = getResources().getDrawable(R.drawable.ic_filter);
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconSearch, getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconFilter, getResources().getColor(R.color.colorAccentWhite));
        }
    }

    private void initTheme() {
        Resources resources = getResources();
        ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
        if (theme == null) {
            mColorPrimary = resources.getColor(R.color.colorPrimaryBlack);
            return;
        }
        switch (theme) {
            case JUST_LIKE:
                mColorPrimary = resources.getColor(R.color.colorPrimary);
                break;
            case WHITE:
                mColorPrimary = resources.getColor(R.color.colorPrimaryBlack);
                break;
            case BLACK:
                mColorPrimary = resources.getColor(R.color.colorPrimaryBlack);
                break;
            case GREY:
                mColorPrimary = resources.getColor(R.color.colorPrimaryGrey);
                break;
            case GREEN:
                mColorPrimary = resources.getColor(R.color.colorPrimaryGreen);
                break;
            case RED:
                mColorPrimary = resources.getColor(R.color.colorPrimaryRed);
                break;
            case PINK:
                mColorPrimary = resources.getColor(R.color.colorPrimaryRed);
                break;
            case BLUE:
                mColorPrimary = resources.getColor(R.color.colorPrimaryBlue);
                break;
            case PURPLE:
                mColorPrimary = resources.getColor(R.color.colorPrimaryPurple);
                break;
            case ORANGE:
                mColorPrimary = resources.getColor(R.color.colorPrimaryOrange);
                break;
        }
    }

    private void initTabLayout() {
        mFragmentManager = getSupportFragmentManager();
        mPagerAdapter = new OnlinePagerAdapter(mFragmentManager);
        mViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTab1 = mTabLayout.getTabAt(0);
        mTab2 = mTabLayout.getTabAt(1);
    }
}
