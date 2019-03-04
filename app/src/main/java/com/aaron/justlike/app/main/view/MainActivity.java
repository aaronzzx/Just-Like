package com.aaron.justlike.app.main.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.app.GridFragment;
import com.aaron.justlike.app.about.view.AboutActivity;
import com.aaron.justlike.app.collection.view.CollectionActivity;
import com.aaron.justlike.app.download.view.DownloadManagerActivity;
import com.aaron.justlike.app.main.entity.Image;
import com.aaron.justlike.app.main.presenter.IMainPresenter;
import com.aaron.justlike.app.main.presenter.MainPresenter;
import com.aaron.justlike.app.online.view.OnlineActivity;
import com.aaron.justlike.custom.GlideEngine;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements IMainView<Image>, View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener, GridFragment.Callback {

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
    private GridFragment mGridFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // 由于设置了启动页，需要在这里将主题改回来
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        attachPresenter();
        initView();
        mPresenter.requestImage(mImageList, false);
        selectHideToolbar(); // 元素少于 16 个时禁止 Toolbar 隐藏
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                mGridFragment.backToTop();
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
     * 下拉刷新监听器回调函数
     */
    @Override
    public void onRefresh() {
        mPresenter.requestImage(mImageList, true);
    }

    @Override
    public void onDelete(String path) {
        mPresenter.deleteImage(path);
    }

    @Override
    public void onHide() {
        mFabButton.hide();
    }

    @Override
    public void onShow() {
        mFabButton.show();
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
        mGridFragment.update(imageList);
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
        mGridFragment.update(list);
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
        mGridFragment = (GridFragment) getSupportFragmentManager().findFragmentById(R.id.grid_fragment);

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
}
