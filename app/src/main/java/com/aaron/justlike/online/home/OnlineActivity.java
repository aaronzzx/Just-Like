package com.aaron.justlike.online.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.aaron.base.impl.OnClickListenerImpl;
import com.aaron.base.util.StatusBarUtils;
import com.aaron.justlike.R;
import com.aaron.justlike.collection.CollectionActivity;
import com.aaron.justlike.common.CommonActivity;
import com.aaron.justlike.common.event.HotfixEvent;
import com.aaron.justlike.common.manager.ThemeManager;
import com.aaron.justlike.common.manager.UiManager;
import com.aaron.justlike.common.util.SelectorUtils;
import com.aaron.justlike.main.MainActivity;
import com.aaron.justlike.others.about.AboutActivity;
import com.aaron.justlike.others.download.DownloadManagerActivity;
import com.aaron.justlike.others.theme.ThemeActivity;
import com.aaron.ui.util.DialogUtil;
import com.aaron.ui.widget.TopBar;
import com.blankj.utilcode.util.AppUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class OnlineActivity extends CommonActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_PERMISSION = 0;

    private DrawerLayout mParentLayout;
    private NavigationView mNavView;
//    private View mStatusBar;
//    private Toolbar mToolbar;
    private TopBar mTopBar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageView mNavHeaderImage;

    private FragmentManager mFragmentManager;
    private ActionBar mActionBar;
    private Drawable mIconDrawer;
    private Drawable mIconSearch;
    private Drawable mIconFilter;

    private int mColorAccent;

    public TopBar getTopBar() {
        return mTopBar;
    }

    /**
     * 热修复完成，提示用户重启应用
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onHotfixSuccess(HotfixEvent event) {
        hotfixRemind();
    }

    private void hotfixRemind() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.app_dialog_normal_alert, null);
        Dialog hotfixDialog = DialogUtil.createDialog(this, dialogView);
        hotfixDialog.setCanceledOnTouchOutside(false);
        TextView tvTitle = dialogView.findViewById(R.id.app_tv_title);
        TextView tvContent = dialogView.findViewById(R.id.app_tv_content);
        Button btnLeft = dialogView.findViewById(R.id.app_btn_left);
        Button btnRight = dialogView.findViewById(R.id.app_btn_right);
        tvTitle.setText(R.string.app_find_update);
        tvContent.setText(R.string.app_restart_to_update);
        btnLeft.setText(R.string.app_later);
        btnRight.setText(R.string.app_restart_right_now);
        btnLeft.setOnClickListener(new OnClickListenerImpl() {
            @Override
            public void onViewClick(View v, long interval) {
                hotfixDialog.dismiss();
            }
        });
        btnRight.setOnClickListener(new OnClickListenerImpl() {
            @Override
            public void onViewClick(View v, long interval) {
                AppUtils.relaunchApp(true);
            }
        });
        hotfixDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        EventBus.getDefault().register(this);
        requestPermission();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
//                mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccentWhite));
                mTopBar.setTextColor(getResources().getColor(R.color.colorAccentWhite));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                UiManager.showShort("不开启权限将无法使用壁纸缓存功能");
            }
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
//        overridePendingTransition(0, R.anim.activity_slide_out);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activity_online_toolbar) {
            List<Fragment> fragments = mFragmentManager.getFragments();
            for (Fragment fragment : fragments) {
                if (fragment.getUserVisibleHint()) {
                    ((OnlineFragment) fragment).backToTop();
                    return;
                }
            }
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
    public int getColorAccent() {
        return mColorAccent;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mParentLayout = findViewById(R.id.drawer_layout);
//        mStatusBar = findViewById(R.id.status_bar);
        mNavView = findViewById(R.id.navigation_view);
        mTopBar = findViewById(R.id.activity_online_toolbar);
        mTabLayout = findViewById(R.id.tab_online);
        mViewPager = findViewById(R.id.view_pager);
        View headerView = mNavView.getHeaderView(0);
        mNavHeaderImage = headerView.findViewById(R.id.nav_head_image);

//        mTopBar.setOnClickListener(this);
        mTopBar.setOnTapListener(v -> {
            List<Fragment> fragments = mFragmentManager.getFragments();
            for (Fragment fragment : fragments) {
                if (fragment.getUserVisibleHint()) {
                    ((OnlineFragment) fragment).backToTop();
                    return;
                }
            }
        });
        mTopBar.setOnBackTapListener(v -> mParentLayout.openDrawer(GravityCompat.START));
        mNavView.setNavigationItemSelectedListener(this);

        initTheme();
        initIconColor();
        initToolbar();
        initTabLayout();
    }

    private void initToolbar() {
        StatusBarUtils.setTransparent(this);
//        setSupportActionBar(mToolbar);
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
            mTabLayout.setTabTextColors(getResources().getColor(R.color.base_black_hint), getResources().getColor(R.color.colorAccentWhite));
            mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconDrawer, getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconSearch, getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconFilter, getResources().getColor(R.color.colorAccentWhite));
        } else {
            mTabLayout.setTabTextColors(getResources().getColor(R.color.base_white_hint), getResources().getColor(R.color.colorPrimaryWhite));
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
        mTopBar.setTextColor(getResources().getColor(R.color.base_white));
        Drawable normal = new ColorDrawable(Color.WHITE);
        Drawable checked = getDrawable(R.drawable.app_bg_nav_checked);
        switch (theme) {
            case JUST_LIKE:
                mColorAccent = resources.getColor(R.color.colorPrimary);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_just_like));
                break;
            case WHITE:
            default:
                mColorAccent = resources.getColor(R.color.colorAccentWhite);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_white));
                mTopBar.setTextColor(getResources().getColor(R.color.colorAccentWhite));
                checked.setTint(Color.BLACK);
                return;
            case BLACK:
                mColorAccent = resources.getColor(R.color.colorPrimaryBlack);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_black));
                break;
            case GREY:
                mColorAccent = resources.getColor(R.color.colorPrimaryGrey);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_grey));
                break;
            case GREEN:
                mColorAccent = resources.getColor(R.color.colorPrimaryGreen);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_green));
                break;
            case RED:
                mColorAccent = resources.getColor(R.color.colorPrimaryRed);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_red));
                break;
            case PINK:
                mColorAccent = resources.getColor(R.color.colorPrimaryPink);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_pink));
                break;
            case BLUE:
                mColorAccent = resources.getColor(R.color.colorPrimaryBlue);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_blue));
                break;
            case PURPLE:
                mColorAccent = resources.getColor(R.color.colorPrimaryPurple);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_purple));
                break;
            case ORANGE:
                mColorAccent = resources.getColor(R.color.colorPrimaryOrange);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_orange));
                break;
        }
        checked.setTint(mColorAccent);
        checked.setAlpha(20);
        Drawable selector = SelectorUtils.createCheckedSelector(this, normal, checked);
        mNavView.setItemBackground(selector);
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
//                overridePendingTransition(R.anim.activity_slide_in, android.R.anim.fade_out);
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
