package com.aaron.justlike.online;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.aaron.justlike.R;
import com.aaron.justlike.collection.CollectionActivity;
import com.aaron.justlike.common.CommonActivity;
import com.aaron.justlike.common.JustLike;
import com.aaron.justlike.common.manager.ThemeManager;
import com.aaron.justlike.main.MainActivity;
import com.aaron.justlike.settings.AboutActivity;
import com.aaron.justlike.settings.DownloadManagerActivity;
import com.aaron.justlike.settings.ThemeActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

public class OnlineActivity extends CommonActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_PERMISSION = 0;

    private DrawerLayout mParentLayout;
    private NavigationView mNavView;
    private View mStatusBar;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageView mNavHeaderImage;

    private FragmentManager mFragmentManager;
    private ActionBar mActionBar;
    private Drawable mIconDrawer;
    private Drawable mIconSearch;
    private Drawable mIconFilter;

    private int mColorPrimary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        requestPermission();
        initView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = getWindow();
        View decorView = window.getDecorView();
        if (hasFocus) {
            mNavView.setCheckedItem(R.id.nav_home);
            ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
            if (theme == null || theme == ThemeManager.Theme.WHITE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    window.setStatusBarColor(getResources().getColor(R.color.status_bar_background));
                }
                setIconFilterColor(theme);
                mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccentWhite));
//                mActionBar.setHomeAsUpIndicator(mIconDrawer);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(JustLike.getContext(), "不开启权限将无法使用壁纸缓存功能", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        mParentLayout.openDrawer(GravityCompat.START);
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (mParentLayout.isDrawerOpen(GravityCompat.START)) {
            mParentLayout.closeDrawer(GravityCompat.START);
            return;
        }
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_online_toolbar:
                List<Fragment> fragments = mFragmentManager.getFragments();
                for (Fragment fragment : fragments) {
                    if (fragment.getUserVisibleHint()) {
                        ((OnlineFragment) fragment).backToTop();
                        return;
                    }
                }
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                mParentLayout.closeDrawers();
                break;
            case R.id.nav_mine:
                startActivityByNav(MainActivity.class);
                break;
            case R.id.nav_collection:
                startActivityByNav(CollectionActivity.class);
                break;
            case R.id.nav_download_manager:
                startActivityByNav(DownloadManagerActivity.class);
                break;
            // TODO 编写侧滑菜单设置项的逻辑
//            case R.id.nav_settings:
//
//                break;
            case R.id.nav_theme:
                startActivityByNav(ThemeActivity.class);
                break;
            case R.id.nav_about:
                startActivityByNav(AboutActivity.class);
                break;
        }
        return true;
    }

    /**
     * Called by fragment.
     */
    public int getColorPrimary() {
        return mColorPrimary;
    }

    private void initView() {
        mParentLayout = findViewById(R.id.drawer_layout);
        mStatusBar = findViewById(R.id.status_bar);
        mNavView = findViewById(R.id.navigation_view);
        mToolbar = findViewById(R.id.activity_online_toolbar);
        mTabLayout = findViewById(R.id.tab_online);
        mViewPager = findViewById(R.id.view_pager);
        View headerView = mNavView.getHeaderView(0);
        mNavHeaderImage = headerView.findViewById(R.id.nav_head_image);

        mToolbar.setOnClickListener(this);
        mNavView.setNavigationItemSelectedListener(this);

        initTheme();
        initIconColor();
        initToolbar();
        initTabLayout();
    }

    private void initToolbar() {
        StatusBarUtil.setTransparentForDrawerLayout(this, mParentLayout);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_drawer_menu);
        }
    }

    private void initIconColor() {
        mIconDrawer = getResources().getDrawable(R.drawable.ic_drawer_menu);
        mIconSearch = getResources().getDrawable(R.drawable.ic_search);
        mIconFilter = getResources().getDrawable(R.drawable.ic_filter);
        ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
        if (theme == null || theme == ThemeManager.Theme.WHITE) {
            mTabLayout.setTabTextColors(getResources().getColor(R.color.blackText), getResources().getColor(R.color.colorAccentWhite));
            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconDrawer, getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconSearch, getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconFilter, getResources().getColor(R.color.colorAccentWhite));
        } else {
            mTabLayout.setTabTextColors(getResources().getColor(R.color.whiteText), getResources().getColor(R.color.colorPrimaryWhite));
            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimaryWhite));
            DrawableCompat.setTint(mIconDrawer, getResources().getColor(R.color.colorPrimaryWhite));
            DrawableCompat.setTint(mIconSearch, getResources().getColor(R.color.colorPrimaryWhite));
            DrawableCompat.setTint(mIconFilter, getResources().getColor(R.color.colorPrimaryWhite));
        }
    }

    private void setIconFilterColor(ThemeManager.Theme theme) {
        if (theme == null || theme == ThemeManager.Theme.WHITE) {
            DrawableCompat.setTint(mIconFilter, getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconSearch, getResources().getColor(R.color.colorAccentWhite));
        } else {
            DrawableCompat.setTint(mIconFilter, getResources().getColor(R.color.colorPrimaryWhite));
            DrawableCompat.setTint(mIconSearch, getResources().getColor(R.color.colorPrimaryWhite));
        }
    }

    private void initTheme() {
        Resources resources = getResources();
        ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
        if (theme == null) {
            mColorPrimary = resources.getColor(R.color.colorAccentWhite);
            mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_white));
            mStatusBar.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimaryWhite)));
            mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccentWhite));
            return;
        }
        switch (theme) {
            case JUST_LIKE:
                mColorPrimary = resources.getColor(R.color.colorPrimary);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_just_like));
                break;
            case WHITE:
                mColorPrimary = resources.getColor(R.color.colorAccentWhite);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_white));
                mStatusBar.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimaryWhite)));
                return;
            case BLACK:
                mColorPrimary = resources.getColor(R.color.colorPrimaryBlack);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_black));
                break;
            case GREY:
                mColorPrimary = resources.getColor(R.color.colorPrimaryGrey);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_grey));
                break;
            case GREEN:
                mColorPrimary = resources.getColor(R.color.colorPrimaryGreen);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_green));
                break;
            case RED:
                mColorPrimary = resources.getColor(R.color.colorPrimaryRed);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_red));
                break;
            case PINK:
                mColorPrimary = resources.getColor(R.color.colorPrimaryPink);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_pink));
                break;
            case BLUE:
                mColorPrimary = resources.getColor(R.color.colorPrimaryBlue);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_blue));
                break;
            case PURPLE:
                mColorPrimary = resources.getColor(R.color.colorPrimaryPurple);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_purple));
                break;
            case ORANGE:
                mColorPrimary = resources.getColor(R.color.colorPrimaryOrange);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_orange));
                break;
        }
        mStatusBar.setBackground(new ColorDrawable(mColorPrimary));
    }

    private void initTabLayout() {
        mFragmentManager = getSupportFragmentManager();
        FragmentPagerAdapter pagerAdapter = new OnlinePagerAdapter(mFragmentManager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void startActivityByNav(Class whichActivity) {
        mParentLayout.closeDrawers();
        mParentLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                Intent intent = new Intent(OnlineActivity.this, whichActivity);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_slide_in, android.R.anim.fade_out);
                mParentLayout.removeDrawerListener(this);
            }
        });
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        // 判断是否已经获得权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请读写存储的权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
    }
}
