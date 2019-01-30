package com.aaron.justlike.main.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.about.view.AboutActivity;
import com.aaron.justlike.activity.CollectionActivity;
import com.aaron.justlike.download.view.DownloadManagerActivity;
import com.aaron.justlike.entity.Image;
import com.aaron.justlike.extend.GlideEngine;
import com.aaron.justlike.extend.MyGridLayoutManager;
import com.aaron.justlike.main.adapter.MainAdapter;
import com.aaron.justlike.main.entity.DeleteEvent;
import com.aaron.justlike.main.entity.PreviewEvent;
import com.aaron.justlike.main.presenter.IMainPresenter;
import com.aaron.justlike.main.presenter.MainPresenter;
import com.aaron.justlike.online.view.Online2Activity;
import com.aaron.justlike.util.SystemUtils;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements IMainView<Image>, View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener,
        MainAdapter.Callback<Image> {

    private static final int REQUEST_PERMISSION = 0;
    private static final int REQUEST_SELECT_IMAGE = 1;

    private int mSortType;
    private boolean mIsAscending;
    private List<Image> mImageList = new ArrayList<>();

    private IMainPresenter<Image> mPresenter;

    private DrawerLayout mParentLayout;
    private LinearLayout mParentToolbar;
    private MenuItem mSortByDate;
    private MenuItem mSortByName;
    private MenuItem mSortBySize;
    private MenuItem mSortByAscending;
    private SwipeRefreshLayout mSwipeRefresh;
    private FloatingActionButton mFabButton;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // 由于设置了启动页，需要在这里将主题改回来
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        EventBus.getDefault().register(this);
        attachPresenter();
        initView();
        mPresenter.requestImage(mImageList, false);
        selectHideToolbar(); // 元素少于 16 个时禁止 Toolbar 隐藏
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.detachView(); // 断开 Presenter
    }

    @Override
    public void onBackPressed() {
        if (mParentLayout.isDrawerOpen(GravityCompat.START)) {
            mParentLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    /**
     * 配合 AppBarLayout 进入伪全屏模式
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = getWindow();
        if (hasFocus) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        // 实例化 Popup 子菜单
        mSortByDate = menu.findItem(R.id.sort_date);
        mSortByName = menu.findItem(R.id.sort_name);
        mSortBySize = menu.findItem(R.id.sort_size);
        mSortByAscending = menu.findItem(R.id.ascending_order);
        // 初始化 Popup 记忆状态
        initMenuItem(mSortType, mIsAscending);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 这里判断升序排列选项是否被选中
        boolean ascendingOrder = mSortByAscending.isChecked();
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mParentLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.sort_date:
                setSort(MainPresenter.SORT_BY_DATE, ascendingOrder);
                break;
            case R.id.sort_name:
                setSort(MainPresenter.SORT_BY_NAME, ascendingOrder);
                break;
            case R.id.sort_size:
                setSort(MainPresenter.SORT_BY_SIZE, ascendingOrder);
                break;
            case R.id.ascending_order:
                setSortByAscending(!ascendingOrder);
                break;
        }
        return super.onOptionsItemSelected(item);
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
     * 请求权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_home_activity_main:
                backToTop();
                break;
            case R.id.fab_home_activity_main:
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
                startActivityByNav(Online2Activity.class);
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
     * 下拉刷新监听器回调函数
     */
    @Override
    public void onRefresh() {
        mPresenter.requestImage(mImageList, true);
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

    /**
     * MainAdapter 中发生 onClick 行为，需要回调此方法
     *
     * @param position 被点击图片在 List 中的位置
     * @param list     存放 Image 实例 的 List
     */
    @Override
    public void onPress(int position, List<Image> list) {
        EventBus.getDefault().postSticky(new PreviewEvent<>(position, list));
        startActivity(new Intent(this, PreviewActivity.class));
    }

    /**
     * MainAdapter 中发生 onLongClick 行为，需要回调此方法
     *
     * @param position 被删图片的索引
     */
    @Override
    public void onLongPress(int position) {
        new AlertDialog.Builder(this)
                .setTitle("删除图片")
                .setMessage("图片将从设备删除")
                .setPositiveButton("确定", (dialog, which) -> {
                    String path = mImageList.get(position).getPath();
                    mImageList.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    mAdapter.notifyItemRangeChanged(0, mImageList.size() - 1);
                    mPresenter.deleteImage(path);
                })
                .setNegativeButton("取消", (dialog, which) -> {
                }).show();
    }

    /**
     * 关联 Presenter
     */
    @Override
    public void attachPresenter() {
        mPresenter = new MainPresenter(this);
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
     * 回调函数，添加用户所选图片
     *
     * @param list 所选图片的集合
     */
    @Override
    public void onShowAddImage(List<Image> list) {
        mImageList.addAll(0, list);
        mAdapter.notifyItemRangeInserted(0, list.size());
        mAdapter.notifyItemRangeChanged(list.size(), mImageList.size() - list.size());
        mRecyclerView.scrollToPosition(0);
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
        mParentLayout = findViewById(R.id.drawer_layout_home_activity_main);
        NavigationView navView = findViewById(R.id.navigation_view_home_activity_main);
        mParentToolbar = findViewById(R.id.activity_main_linear_layout);
        Toolbar toolbar = findViewById(R.id.toolbar_home_activity_main);
        mSwipeRefresh = findViewById(R.id.swipe_refresh_home_activity_main);
        mFabButton = findViewById(R.id.fab_home_activity_main);
        mRecyclerView = findViewById(R.id.rv_home_activity_main);

        // Part 2, setClickListener
        toolbar.setOnClickListener(this);
        mFabButton.setOnClickListener(this);
        navView.setNavigationItemSelectedListener(this);
        mSwipeRefresh.setOnRefreshListener(this);

        // Part 3, init status
        setStatusBar();
        setSupportActionBar(toolbar);
        enableHomeAsUp();
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        initRecyclerView();
    }

    private void setStatusBar() {
        StatusBarUtil.setTranslucentForDrawerLayout(this, mParentLayout, 70);
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
        MyGridLayoutManager layoutManager = new MyGridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mRecyclerView.addItemDecoration(new YItemDecoration());
        mAdapter = new MainAdapter<>(mImageList, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    mFabButton.hide();
                } else if (dy < 0) {
                    mFabButton.show();
                }
            }
        });
    }

    private void selectHideToolbar() {
        // 元素不够禁止隐藏 Toolbar
        if (mImageList.size() < 16) {
            AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) mParentToolbar.getLayoutParams();
            layoutParams.setScrollFlags(0);
        }
    }

    /**
     * 用于程序启动时初始化 Popup 菜单状态
     */
    private void initMenuItem(int sortType, boolean ascendingOrder) {
        switch (sortType) {
            case MainPresenter.SORT_BY_DATE:
                mSortByDate.setChecked(true);
                mSortByAscending.setChecked(ascendingOrder);
                break;
            case MainPresenter.SORT_BY_NAME:
                mSortByName.setChecked(true);
                mSortByAscending.setChecked(ascendingOrder);
                break;
            case MainPresenter.SORT_BY_SIZE:
                mSortBySize.setChecked(true);
                mSortByAscending.setChecked(ascendingOrder);
                break;
            default:
                mSortByDate.setChecked(true);
                mSortByAscending.setChecked(true);
                break;
        }
    }

    /**
     * 设置 Popup 菜单排序类型状态
     */
    private void setSort(int sortType, boolean ascendingOrder) {
        mPresenter.setSortType(sortType, ascendingOrder);
        switch (sortType) {
            case MainPresenter.SORT_BY_DATE:
                mSortByDate.setChecked(true);
                break;
            case MainPresenter.SORT_BY_NAME:
                mSortByName.setChecked(true);
                break;
            case MainPresenter.SORT_BY_SIZE:
                mSortBySize.setChecked(true);
                break;
        }
        mPresenter.requestImage(mImageList, false);
    }

    /**
     * 设置 Popup 菜单是否升序状态
     */
    private void setSortByAscending(boolean sortByAscending) {
        if (mSortByDate.isChecked()) {
            mPresenter.setSortType(MainPresenter.SORT_BY_DATE, sortByAscending);
            mSortByAscending.setChecked(sortByAscending);

        } else if (mSortByName.isChecked()) {
            mPresenter.setSortType(MainPresenter.SORT_BY_NAME, sortByAscending);
            mSortByAscending.setChecked(sortByAscending);

        } else {
            mPresenter.setSortType(MainPresenter.SORT_BY_SIZE, sortByAscending);
            mSortByAscending.setChecked(sortByAscending);
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

    private class XItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 3 == 0) {
                outRect.left = 0;
                outRect.right = SystemUtils.dp2px(MainActivity.this, 2.8F); // 8px
            } else if (parent.getChildAdapterPosition(view) % 3 == 1) {
                outRect.left = SystemUtils.dp2px(MainActivity.this, 1.3F); // 4px
                outRect.right = SystemUtils.dp2px(MainActivity.this, 1.3F); // 4px
            } else if (parent.getChildAdapterPosition(view) % 3 == 2) {
                outRect.left = SystemUtils.dp2px(MainActivity.this, 2.8F); // 8px
                outRect.right = 0;
            }
        }
    }

    private class YItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.bottom = 0;
            outRect.top = SystemUtils.dp2px(MainActivity.this, 4.2F); // 12px
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 0;
            } else if (parent.getChildAdapterPosition(view) == 1) {
                outRect.top = 0;
            } else if (parent.getChildAdapterPosition(view) == 2) {
                outRect.top = 0;
            }
        }
    }
}
