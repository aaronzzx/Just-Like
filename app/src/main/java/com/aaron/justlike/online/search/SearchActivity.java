package com.aaron.justlike.online.search;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.aaron.base.impl.OnClickListenerImpl;
import com.aaron.base.impl.TextWatcherImpl;
import com.aaron.justlike.R;
import com.aaron.justlike.common.CommonActivity;
import com.aaron.justlike.common.manager.ThemeManager;
import com.github.anzewei.parallaxbacklayout.ParallaxBack;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

@ParallaxBack
public class SearchActivity extends CommonActivity implements View.OnClickListener,
        EditText.OnEditorActionListener {

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private EditText mEditText;
    private ImageButton mIbtnClear;
    private ImageView mImgSearch;

    private FragmentManager mFragmentManager;
    private ActionBar mActionBar;
    private Drawable mIconBack;
    private Drawable mIconSearch;

    private int mColorAccent;

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
                mActionBar.setHomeAsUpIndicator(mIconBack);
//                mImgSearch.setTextColor(getResources().getColor(R.color.colorAccentWhite));
                mImgSearch.setImageDrawable(mIconSearch);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
//        overridePendingTransition(0, R.anim.activity_slide_out);
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        finish();
//        overridePendingTransition(0, R.anim.activity_slide_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search:
                String text = mEditText.getText().toString();
                if (text.equals("")) break;
                hideKeyboard();
                List<Fragment> fragments = mFragmentManager.getFragments();
                for (Fragment fragment : fragments) {
                    if (fragment.getUserVisibleHint()) {
                        ((IFragment) fragment).search(text);
                        break;
                    }
                }
                break;
        }
    }

    /**
     * 回调搜索框输入的内容
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        String text = v.getText().toString();
        if (text.equals("")) return true;
        hideKeyboard();
        List<Fragment> fragments = mFragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment.getUserVisibleHint()) {
                ((IFragment) fragment).search(text);
                break;
            }
        }
        return true;
    }

    private void initView() {
        mToolbar = findViewById(R.id.activity_search_toolbar);
        mTabLayout = findViewById(R.id.tab_search);
        mViewPager = findViewById(R.id.view_pager);
        mEditText = findViewById(R.id.edit_text);
        mIbtnClear = findViewById(R.id.app_ibtn_clear);
        mImgSearch = findViewById(R.id.tv_search);

        mToolbar.setOnClickListener(this);
        mEditText.setOnEditorActionListener(this);
        mImgSearch.setOnClickListener(this);
        mIbtnClear.setOnClickListener(new OnClickListenerImpl() {
            @Override
            public void onViewClick(View v, long interval) {
                mEditText.setText("");
            }
        });
        mEditText.addTextChangedListener(new TextWatcherImpl() {
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() != 0) {
                    mIbtnClear.setVisibility(View.VISIBLE);
                } else {
                    mIbtnClear.setVisibility(View.GONE);
                }
            }
        });

        initTheme();
        initIconColor();
        initToolbar();
        initTabLayout();
        showSoftKeyboard();
    }

    public int getColorAccent() {
        return mColorAccent;
    }

    private void initTheme() {
        Resources resources = getResources();
        ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
        switch (theme) {
            case JUST_LIKE:
                mColorAccent = resources.getColor(R.color.colorPrimary);
                break;
            case WHITE:
            default:
                mColorAccent = resources.getColor(R.color.colorAccentWhite);
                return;
            case BLACK:
                mColorAccent = resources.getColor(R.color.colorPrimaryBlack);
                break;
            case GREY:
                mColorAccent = resources.getColor(R.color.colorPrimaryGrey);
                break;
            case GREEN:
                mColorAccent = resources.getColor(R.color.colorPrimaryGreen);
                break;
            case RED:
                mColorAccent = resources.getColor(R.color.colorPrimaryRed);
                break;
            case PINK:
                mColorAccent = resources.getColor(R.color.colorPrimaryPink);
                break;
            case BLUE:
                mColorAccent = resources.getColor(R.color.colorPrimaryBlue);
                break;
            case PURPLE:
                mColorAccent = resources.getColor(R.color.colorPrimaryPurple);
                break;
            case ORANGE:
                mColorAccent = resources.getColor(R.color.colorPrimaryOrange);
                break;
        }
        mEditText.setTextColor(mColorAccent);
    }

    private void initIconColor() {
        mIconBack = getResources().getDrawable(R.drawable.ic_back);
        mIconSearch = getResources().getDrawable(R.drawable.ic_search);
        ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
        if (theme == null || theme == ThemeManager.Theme.WHITE) {
            mTabLayout.setTabTextColors(getResources().getColor(R.color.base_black_hint), getResources().getColor(R.color.colorAccentWhite));
            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconSearch, getResources().getColor(R.color.colorAccentWhite));
        } else {
            mTabLayout.setTabTextColors(getResources().getColor(R.color.base_white_hint), getResources().getColor(R.color.colorPrimaryWhite));
            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimaryWhite));
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorPrimaryWhite));
            DrawableCompat.setTint(mIconSearch, getResources().getColor(R.color.colorPrimaryWhite));
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    private void initTabLayout() {
        mFragmentManager = getSupportFragmentManager();
        FragmentPagerAdapter pagerAdapter = new SearchPagerAdapter(mFragmentManager);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void showSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void hideKeyboard() {
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }
}
