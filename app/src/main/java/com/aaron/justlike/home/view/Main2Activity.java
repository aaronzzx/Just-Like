package com.aaron.justlike.home.view;

import android.os.Bundle;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.extend.MyGridLayoutManager;
import com.aaron.justlike.home.HomeAdapter;
import com.aaron.justlike.home.presenter.BasePresenter;
import com.aaron.justlike.home.presenter.IPresenter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Main2Activity extends BaseView {

    private IPresenter mPresenter;

    private DrawerLayout mParentLayout;
    private NavigationView mNavView;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        attachPresenter();
        initView();
        mPresenter.requestImage(BasePresenter.SORT_BY_DATE, BasePresenter.ASCENDING_ORDER);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void attachPresenter() {
        mPresenter = new BasePresenter(this);
    }

    @Override
    public void showImage(List<Image> imageList) {
        mAdapter = new HomeAdapter(imageList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(String args) {
        Toast.makeText(this, args, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void openPreviewActivity() {

    }

    @Override
    public void openSelectorActivity() {

    }

    @Override
    public void openMainActivity() {

    }

    @Override
    public void openOnlineActivity() {

    }

    @Override
    public void openCollectionActivity() {

    }

    @Override
    public void openDownloadManagerActivity() {

    }

    @Override
    public void openAboutActivity() {

    }

    private void initView() {
        mParentLayout = findViewById(R.id.drawer_layout);
        mNavView = findViewById(R.id.nav_view);
        mAppBarLayout = findViewById(R.id.activity_main_appbar_layout);
        mToolbar = findViewById(R.id.activity_main_toolbar);
        mRecyclerView = findViewById(R.id.recycler_view);
        mSwipeRefresh = findViewById(R.id.swipe_refresh);

        StatusBarUtil.setTransparentForDrawerLayout(this, mParentLayout);
        setSupportActionBar(mToolbar);
        enableHomeAsUp();
        initRecyclerView();
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
    }

    private void enableHomeAsUp() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_drawer_menu);
        }
    }

    /**
     * 配置 RecyclerView 初始状态，使用自定义 LayoutManager 为了能控制滑动状态，
     * 并添加 RecyclerView 元素间距
     */
    private void initRecyclerView() {
        // 将 RecyclerView 的布局风格改为网格类型,使用自定义的布局管理器，为了能修改滑动状态
        mLayoutManager = new MyGridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mRecyclerView.addItemDecoration(new YItemDecoration());
    }
}
