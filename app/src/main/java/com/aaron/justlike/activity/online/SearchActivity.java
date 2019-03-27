package com.aaron.justlike.activity.online;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.online.OnlinePagerAdapter;
import com.aaron.justlike.common.ThemeManager;
import com.aaron.justlike.fragment.online.search.CollectionFragment;
import com.aaron.justlike.fragment.online.search.PhotoFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener,
        EditText.OnEditorActionListener {

    private PhotoFragment mPhotoFragment;
    private CollectionFragment mCollectionFragment;

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private TabLayout.Tab mPhotoTab;
    private TabLayout.Tab mCollectionTab;
    private ViewPager mViewPager;

    private EditText mEditText;
    private TextView mTextView;

    private FragmentManager mFragmentManager;
    private ActionBar mActionBar;
    private Drawable mIconBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        getFragment();
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
            case R.id.tv_search:
                String text = mEditText.getText().toString();
                if (mCollectionTab.isSelected()) {
                    mCollectionFragment.setKeyWord(text);
                    return;
                }
                mPhotoFragment.setKeyWord(text);
                break;
        }
    }

    /**
     * 回调搜索框输入的内容
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        String text = v.getText().toString();
        if (mCollectionTab.isSelected()) {
            mCollectionFragment.setKeyWord(text);
            return true;
        }
        mPhotoFragment.setKeyWord(text);
        return true;
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

        initToolbar();
        initTabLayout();
        showSoftKeyboard();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        mIconBack = getResources().getDrawable(R.drawable.ic_back);
    }

    private void initTabLayout() {
        mFragmentManager = getSupportFragmentManager();
        FragmentPagerAdapter pagerAdapter = new OnlinePagerAdapter(mFragmentManager);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void getFragment() {
        List<Fragment> fragments = mFragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof PhotoFragment) {
                mPhotoFragment = (PhotoFragment) fragment;
            } else if (fragment instanceof CollectionFragment) {
                mCollectionFragment = (CollectionFragment) fragment;
            }
        }
    }

    private void showSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}
