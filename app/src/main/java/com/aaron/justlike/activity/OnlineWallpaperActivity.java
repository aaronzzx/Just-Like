package com.aaron.justlike.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.google.android.material.navigation.NavigationView;
import com.jaeger.library.StatusBarUtil;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class OnlineWallpaperActivity extends AppCompatActivity {

    private DrawerLayout mParent;
    private NavigationView mNavView;
    private SwipeRefreshLayout mSwipeRefresh;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_wallpaper);
        initViews(); // 初始化控件
        StatusBarUtil.setTransparentForDrawerLayout(this, mParent); // 修改状态栏
    }

    @Override
    protected void onStart() {
        super.onStart();
        mNavView.setCheckedItem(R.id.nav_online_wallpaper);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 设置可以打开菜单
     *
     * @param item item 传入的 View 实例
     * @return 返回 true 才执行
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mParent.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    private void initViews() {
        mParent = findViewById(R.id.parent);
        mNavView = findViewById(R.id.nav_view);
        mSwipeRefresh = findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
//        mToolbar.setOnClickListener(this);
        ActionBar actionBar = getSupportActionBar();
        /*
         * 让标题栏启用滑动菜单并设置图标
         */
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_drawer_menu);
        }
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        mParent.closeDrawers();
                        mParent.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                            @Override
                            public void onDrawerClosed(View drawerView) {
                                onBackPressed();
                                mParent.removeDrawerListener(this);
                            }
                        });
                        break;
                    case R.id.nav_online_wallpaper:
                        mParent.closeDrawers();
                        break;
                    case R.id.nav_about:
                        mParent.closeDrawers();
                        mParent.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                            @Override
                            public void onDrawerClosed(View drawerView) {
                                startActivity(new Intent(OnlineWallpaperActivity.this,
                                        AboutActivity.class));
                                mParent.removeDrawerListener(this);
                            }
                        });
                        break;
                    default:
                        Toast.makeText(OnlineWallpaperActivity.this,
                                "暂未开放", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }
}
