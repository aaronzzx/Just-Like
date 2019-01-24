package com.aaron.justlike.home.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.AboutActivity;
import com.aaron.justlike.activity.CollectionActivity;
import com.aaron.justlike.activity.DownloadManagerActivity;
import com.aaron.justlike.activity.OnlineActivity;
import com.aaron.justlike.adapter.HomeAdapter;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.extend.GlideEngine;
import com.aaron.justlike.extend.MyGridLayoutManager;
import com.aaron.justlike.home.presenter.BasePresenter;
import com.aaron.justlike.home.presenter.IPresenter;
import com.aaron.justlike.util.SystemUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.jaeger.library.StatusBarUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Main2Activity extends BaseView implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, AppBarLayout.OnOffsetChangedListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final int REQUEST_SELECT_IMAGE = 0;
    private int mSortType;
    private boolean mIsAscending;

    private IPresenter<Image> mPresenter;

    private DrawerLayout mParentLayout;
    private NavigationView mNavView;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private MenuItem mSortByDate;
    private MenuItem mSortByName;
    private MenuItem mSortBySize;
    private MenuItem mAscendingOrder;
    private SwipeRefreshLayout mSwipeRefresh;
    private FloatingActionButton mFabButton;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    private List<Image> mImageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        attachPresenter();
        initView();
        mPresenter.requestImage(mImageList, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView(); // 断开 Presenter
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        // 实例化 Popup 子菜单
        mSortByDate = menu.findItem(R.id.sort_date);
        mSortByName = menu.findItem(R.id.sort_name);
        mSortBySize = menu.findItem(R.id.sort_size);
        mAscendingOrder = menu.findItem(R.id.ascending_order);
        // 初始化 Popup 记忆状态
        initMenuItem(mSortType, mIsAscending);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 这里判断升序排列选项是否被选中
        boolean ascendingOrder = mAscendingOrder.isChecked();
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mParentLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.sort_date:
                setSort(BasePresenter.SORT_BY_DATE, ascendingOrder);
                break;
            case R.id.sort_name:
                setSort(BasePresenter.SORT_BY_NAME, ascendingOrder);
                break;
            case R.id.sort_size:
                setSort(BasePresenter.SORT_BY_SIZE, ascendingOrder);
                break;
            case R.id.ascending_order:
                setAscendingOrder(!ascendingOrder);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_toolbar:
                backToTop();
                break;
            case R.id.fab:
                openImageSelector();
                break;
        }
    }

    /**
     * 响应侧滑菜单点击
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                mParentLayout.closeDrawers();
                break;
            case R.id.nav_online_wallpaper:
                startActivityByNav(OnlineActivity.class);
                break;
            case R.id.nav_collection:
                startActivityByNav(CollectionActivity.class);
                break;
            case R.id.nav_download_manager:
                startActivityByNav(DownloadManagerActivity.class);
                break;
            // TODO 编写侧滑菜单设置项的逻辑
//                    case R.id.nav_settings:
//                        break;
            case R.id.nav_about:
                startActivityByNav(AboutActivity.class);
                break;
        }
        return true;
    }

    /**
     * 监听 AppBarLayout 偏移量
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        // 判断 Toolbar 是否可见
        boolean isVisible = SystemUtils.isViewVisible(mToolbar);
        if (isVisible) {
            exitFullScreen();
            mFabButton.show();
        } else {
            enableFullScreen();
            mFabButton.hide();
        }
    }

    /**
     * 下拉刷新监听器回调函数
     */
    @Override
    public void onRefresh() {
        mPresenter.requestImage(mImageList, true);
    }

    /**
     * 另一 Activity 回调此 Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // TODO onActivityResult()
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 关联 Presenter
     */
    @Override
    public void attachPresenter() {
        mPresenter = new BasePresenter(this);
    }

    /**
     * 回调函数，在请求到数据后显示图片
     */
    @Override
    public void onShowImage(List<Image> imageList, int sortType, boolean ascendingOrder) {
        mImageList.clear();
        mImageList.addAll(imageList);
        mAdapter.notifyItemRangeChanged(0, mImageList.size());
        mSortType = sortType;
        mIsAscending = ascendingOrder;
    }

    /**
     * 回调函数，用于请求不到数据时显示信息
     */
    @Override
    public void onShowMessage(String args) {
        Toast.makeText(this, args, Toast.LENGTH_SHORT).show();
    }

    /**
     * 回调函数，用于请求数据得到结果后隐藏下拉刷新
     */
    @Override
    public void onHideRefresh() {
        mSwipeRefresh.setRefreshing(false);
    }

    private void initView() {
        // Part 1, find id
        mParentLayout = findViewById(R.id.drawer_layout);
        mNavView = findViewById(R.id.nav_view);
        mAppBarLayout = findViewById(R.id.activity_main_appbar_layout);
        mToolbar = findViewById(R.id.activity_main_toolbar);
        mSwipeRefresh = findViewById(R.id.swipe_refresh);
        mFabButton = findViewById(R.id.fab);
        mRecyclerView = findViewById(R.id.recycler_view);

        // Part 2, setClickListener
        mToolbar.setOnClickListener(this);
        mFabButton.setOnClickListener(this);
        mNavView.setNavigationItemSelectedListener(this);
        mAppBarLayout.addOnOffsetChangedListener(this);
        mSwipeRefresh.setOnRefreshListener(this);

        // Part 3, init status
        StatusBarUtil.setTransparentForDrawerLayout(this, mParentLayout);
        setSupportActionBar(mToolbar);
        enableHomeAsUp();
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        initRecyclerView();
    }

    /**
     * 启用 HomeAsUp 按钮
     */
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
        mAdapter = new HomeAdapter<>(mImageList);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 用于程序启动时初始化 Popup 菜单状态
     */
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
            default:
                mSortByDate.setChecked(true);
                mAscendingOrder.setChecked(true);
                break;
        }
    }

    /**
     * 设置 Popup 菜单排序类型状态
     */
    private void setSort(int sortType, boolean ascendingOrder) {
        mPresenter.setSortType(sortType, ascendingOrder);
        switch (sortType) {
            case BasePresenter.SORT_BY_DATE:
                mSortByDate.setChecked(true);
                break;
            case BasePresenter.SORT_BY_NAME:
                mSortByName.setChecked(true);
                break;
            case BasePresenter.SORT_BY_SIZE:
                mSortBySize.setChecked(true);
                break;
        }
        mPresenter.requestImage(mImageList, false);
    }

    /**
     * 设置 Popup 菜单是否升序状态
     */
    private void setAscendingOrder(boolean ascendingOrder) {
        if (mSortByDate.isChecked()) {
            mPresenter.setSortType(BasePresenter.SORT_BY_DATE, ascendingOrder);
            mAscendingOrder.setChecked(ascendingOrder);

        } else if (mSortByName.isChecked()) {
            mPresenter.setSortType(BasePresenter.SORT_BY_NAME, ascendingOrder);
            mAscendingOrder.setChecked(ascendingOrder);

        } else {
            mPresenter.setSortType(BasePresenter.SORT_BY_SIZE, ascendingOrder);
            mAscendingOrder.setChecked(ascendingOrder);
        }
        mPresenter.requestImage(mImageList, false);
    }

    /**
     * 单击 Toolbar 回顶部
     */
    public void backToTop() {
        // 查找当前屏幕内第一个可见的 View
        View firstVisibleItem = mRecyclerView.getChildAt(0);
        // 查找当前 View 在 RecyclerView 中处于哪个位置
        int itemPosition = mRecyclerView.getChildLayoutPosition(firstVisibleItem);
        if (itemPosition >= 48) {
            mRecyclerView.scrollToPosition(42);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

    /**
     * 打开图片选择器
     */
    private void openImageSelector() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .showSingleMediaType(true)
                .countable(true)
                .maxSelectable(9)
//                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(SystemUtils.dp2px(this, 120.0F))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .theme(R.style.MatisseTheme)
                .forResult(REQUEST_SELECT_IMAGE);
    }

    /**
     * 进入伪全屏模式---NavigationBar 不隐藏
     */
    private void enableFullScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * 退出伪全屏模式
     */
    private void exitFullScreen() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    /**
     * 点击菜单跳转 Activity--- for NavigationView
     */
    private void startActivityByNav(Class whichActivity) {
        mParentLayout.closeDrawers();
        mParentLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                Intent intent = new Intent(Main2Activity.this,
                        whichActivity);
                startActivity(intent);
                mParentLayout.removeDrawerListener(this);
            }
        });
    }
}
