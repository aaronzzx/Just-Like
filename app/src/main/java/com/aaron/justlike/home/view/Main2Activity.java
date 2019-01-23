package com.aaron.justlike.home.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.extend.MyGridLayoutManager;
import com.aaron.justlike.home.presenter.BasePresenter;
import com.aaron.justlike.home.presenter.IPresenter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
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
    private MenuItem mSortByDate;
    private MenuItem mSortByName;
    private MenuItem mSortBySize;
    private MenuItem mAscendingOrder;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefresh;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    private int mSortType;
    private boolean mIsAscending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        attachPresenter();
        initView();
        mPresenter.requestImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        mSortByDate = menu.findItem(R.id.sort_date);
        mSortByName = menu.findItem(R.id.sort_name);
        mSortBySize = menu.findItem(R.id.sort_size);
        mAscendingOrder = menu.findItem(R.id.ascending_order);
        initMenuItem(mSortType, mIsAscending);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ascendingOrder = mAscendingOrder.isChecked();
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mParentLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.sort_date:
                mPresenter.setSortType(BasePresenter.SORT_BY_DATE, ascendingOrder);
                mSortByDate.setChecked(true);
                mPresenter.requestImage();
                break;
            case R.id.sort_name:
                mPresenter.setSortType(BasePresenter.SORT_BY_NAME, ascendingOrder);
                mSortByName.setChecked(true);
                mPresenter.requestImage();
                break;
            case R.id.sort_size:
                mPresenter.setSortType(BasePresenter.SORT_BY_SIZE, ascendingOrder);
                mSortBySize.setChecked(true);
                mPresenter.requestImage();
                break;
            case R.id.ascending_order:
                if (mSortByDate.isChecked()) {
                    mPresenter.setSortType(BasePresenter.SORT_BY_DATE, !ascendingOrder);
                    mAscendingOrder.setChecked(!ascendingOrder);

                } else if (mSortByName.isChecked()) {
                    mPresenter.setSortType(BasePresenter.SORT_BY_NAME, !ascendingOrder);
                    mAscendingOrder.setChecked(!ascendingOrder);

                } else {
                    mPresenter.setSortType(BasePresenter.SORT_BY_SIZE, !ascendingOrder);
                    mAscendingOrder.setChecked(!ascendingOrder);
                }
                mPresenter.requestImage();
                break;
        }
        return super.onOptionsItemSelected(item);
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
    public void onShowImage(List<Image> imageList, int sortType, boolean ascendingOrder) {
        mAdapter = new HomeAdapter(imageList);
        mRecyclerView.setAdapter(mAdapter);
        mSortType = sortType;
        mIsAscending = ascendingOrder;
    }

    @Override
    public void onShowMessage(String args) {
        Toast.makeText(this, args, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHideRefresh() {
        mSwipeRefresh.setRefreshing(false);
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

    private void initMenuItem(int sortType, boolean ascendingOrder) {
        switch (sortType) {
            case BasePresenter.SORT_BY_DATE:
                mSortByDate.setChecked(true);
                mAscendingOrder.setChecked(ascendingOrder);
                break;
            case BasePresenter.SORT_BY_NAME:
                mSortByName.setChecked(true);
                mAscendingOrder.setChecked(ascendingOrder);
                break;
            case BasePresenter.SORT_BY_SIZE:
                mSortBySize.setChecked(true);
                mAscendingOrder.setChecked(ascendingOrder);
                break;
        }
    }
}
