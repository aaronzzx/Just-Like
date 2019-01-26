package com.aaron.justlike.home.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.AboutActivity;
import com.aaron.justlike.activity.CollectionActivity;
import com.aaron.justlike.activity.DownloadManagerActivity;
import com.aaron.justlike.activity.MainImageActivity;
import com.aaron.justlike.activity.OnlineActivity;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.extend.GlideEngine;
import com.aaron.justlike.extend.MyGridLayoutManager;
import com.aaron.justlike.extend.SquareView;
import com.aaron.justlike.home.entity.DeleteEvent;
import com.aaron.justlike.home.entity.PreviewEvent;
import com.aaron.justlike.home.presenter.BasePresenter;
import com.aaron.justlike.home.presenter.IPresenter;
import com.aaron.justlike.util.SystemUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.jaeger.library.StatusBarUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends BaseView implements View.OnClickListener,
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
        EventBus.getDefault().register(this);
        attachPresenter();
        initView();
        mPresenter.requestImage(mImageList, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.detachView(); // 断开 Presenter
    }

    /**
     * 配合 AppBarLayout 进入伪全屏模式
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        if (!SystemUtils.isViewVisible(mToolbar)) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
    }

    /**
     * 接收 PreviewActivity 传过来的关于被删除图片的信息，并更新 UI
     */
    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onDeleteEvent(DeleteEvent event) {
        int position = event.getPosition();
        String path = event.getPath();
        mImageList.remove(position);
        mAdapter.notifyItemRemoved(position);
        mAdapter.notifyItemRangeChanged(position, mImageList.size() - 1);
        mPresenter.deleteImage(path);
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
//            case R.id.nav_settings:
//
//                break;
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
//            exitFullScreen();
            mFabButton.show();
        } else {
//            enableFullScreen();
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
     * Matisse 选取图片后默认回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    List<String> selectedList = Matisse.obtainPathResult(data);
                    mPresenter.addImage(mImageList, selectedList);
                }
                break;
        }
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
        setStatusBar();
        setSupportActionBar(mToolbar);
        enableHomeAsUp();
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        initRecyclerView();
    }

    private void setStatusBar() {
        StatusBarUtil.setTranslucentForDrawerLayout(this, mParentLayout, 80);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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
        mAdapter = new HomeAdapter();
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
     * 点击菜单跳转 Activity --- for NavigationView
     */
    private void startActivityByNav(Class whichActivity) {
        mParentLayout.closeDrawers();
        mParentLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                Intent intent = new Intent(MainActivity.this, whichActivity);
                startActivity(intent);
                mParentLayout.removeDrawerListener(this);
            }
        });
    }

    /**
     * 缓存视图 RecyclerView.ViewHolder
     */
    private static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        SquareView squareView;

        ViewHolder(View view) {
            super(view);
            itemView = view;
            squareView = view.findViewById(R.id.square_view);
        }
    }

    /**
     * RecyclerView 界面适配器
     */
    private class HomeAdapter extends RecyclerView.Adapter<ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.activity_main_recycler_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            // for Image onClick()
            holder.itemView.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                EventBus.getDefault().postSticky(new PreviewEvent<>(position, mImageList));
                Intent intent = new Intent(MainActivity.this, MainImageActivity.class);
                startActivity(intent);
            });
            // for Image onLongClick()
            holder.itemView.setOnLongClickListener(v -> {
                int position = holder.getAdapterPosition();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("删除图片")
                        .setMessage("图片将从设备中删除")
                        .setPositiveButton("确定", (dialog, which) -> {
                            String path = mImageList.get(position).getPath();
                            mImageList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(0, mImageList.size() - 1);
                            mPresenter.deleteImage(path);
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                        }).show();
                return true;
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Image image = mImageList.get(position); // 从集合中找到 Image 对象
            String path = image.getPath();

            RequestOptions options = new RequestOptions()
                    .placeholder(R.color.colorGrey);
            Glide.with(MainActivity.this)
                    .load(path)
                    .apply(options)
                    .into(new ImageViewTarget<Drawable>(holder.squareView) {
                        @Override
                        protected void setResource(@Nullable Drawable resource) {
                            holder.squareView.setImageDrawable(resource);
//                            AnimationUtil.showViewByAlpha(holder.squareView, 0.5F, 1, 300);
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return mImageList.size();
        }
    }
}
