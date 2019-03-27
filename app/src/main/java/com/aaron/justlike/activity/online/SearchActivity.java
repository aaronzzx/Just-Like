package com.aaron.justlike.activity.online;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.online.OnlinePagerAdapter;
import com.aaron.justlike.common.ThemeManager;
import com.aaron.justlike.fragment.online.PhotoFragment;
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

public class SearchActivity extends AppCompatActivity implements View.OnClickListener,
        EditText.OnEditorActionListener {

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private EditText mEditText;
    private TextView mTextView;

    private FragmentManager mFragmentManager;
    private ActionBar mActionBar;
    private Drawable mIconBack;

    private int mColorPrimary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideSoftKeyboard();
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
                mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccentWhite));
                mTextView.setTextColor(getResources().getColor(R.color.colorAccentWhite));
                mActionBar.setHomeAsUpIndicator(mIconBack);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return super.onSupportNavigateUp();
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
            case R.id.tv_search:
                String text = mEditText.getText().toString();
                Log.i("SearchActivity", "text: " + text);
                break;
        }
    }

    /**
     * 回调搜索框输入的内容
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        Log.i("SearchActivity", "v: " + v.getText().toString());
        Log.i("SearchActivity", "actionId: " + actionId);
        return true;
    }

    /**
     * Called by fragment.
     */
    public int getColorPrimary() {
        return mColorPrimary;
    }

    private void initView() {
        mToolbar = findViewById(R.id.activity_search_toolbar);
        mTabLayout = findViewById(R.id.tab_search);
        mViewPager = findViewById(R.id.view_pager);
        mEditText = findViewById(R.id.edit_text);
        mTextView = findViewById(R.id.tv_search);

        mToolbar.setOnClickListener(this);
        mEditText.setOnEditorActionListener(this);
        mTextView.setOnClickListener(this);

        initTheme();
        initIconColor();
        initToolbar();
//        initTabLayout();
        showSoftKeyboard();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    private void initIconColor() {
        mIconBack = getResources().getDrawable(R.drawable.ic_back);
        Drawable iconSearch = getResources().getDrawable(R.drawable.ic_search);
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            mTabLayout.setTabTextColors(getResources().getColor(R.color.colorAccentWhite), getResources().getColor(R.color.colorAccentWhite));
            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(iconSearch, getResources().getColor(R.color.colorAccentWhite));
        } else {
            mTabLayout.setTabTextColors(getResources().getColor(R.color.colorPrimaryWhite), getResources().getColor(R.color.colorPrimaryWhite));
            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimaryWhite));
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorPrimaryWhite));
            DrawableCompat.setTint(iconSearch, getResources().getColor(R.color.colorPrimaryWhite));
        }
    }

    private void initTheme() {
        Resources resources = getResources();
        ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
        if (theme == null) {
            mColorPrimary = resources.getColor(R.color.colorAccentWhite);
            return;
        }
        switch (theme) {
            case JUST_LIKE:
                mColorPrimary = resources.getColor(R.color.colorPrimary);
                break;
            case WHITE:
                mColorPrimary = resources.getColor(R.color.colorAccentWhite);
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
                mColorPrimary = resources.getColor(R.color.colorPrimaryPink);
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
        FragmentPagerAdapter pagerAdapter = new OnlinePagerAdapter(mFragmentManager);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void showSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
