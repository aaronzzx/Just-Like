package com.aaron.justlike.activity.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.about.AboutActivity;
import com.aaron.justlike.activity.collection.CollectionActivity;
import com.aaron.justlike.activity.download.DownloadManagerActivity;
import com.aaron.justlike.activity.online.OnlineActivity;
import com.aaron.justlike.activity.theme.ThemeActivity;
import com.aaron.justlike.common.SquareFragment;
import com.aaron.justlike.common.ThemeManager;
import com.aaron.justlike.entity.Image;
import com.aaron.justlike.library.glide.GlideEngine;
import com.aaron.justlike.mvp.presenter.main.IMainPresenter;
import com.aaron.justlike.mvp.presenter.main.MainPresenter;
import com.aaron.justlike.mvp.view.main.IMainView;
import com.aaron.justlike.util.SystemUtil;
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
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements IMainView<Image>, View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener,
        SquareFragment.Callback {

    private static final int REQUEST_PERMISSION = 0;
    private static final int REQUEST_SELECT_IMAGE = 1;

    private int mMatisseTheme;
    private int mColorPrimary;
    private int mSortType;
    private boolean mIsAscending;
    private List<Image> mImageList = new ArrayList<>();

    private IMainPresenter<Image> mPresenter;

    private DrawerLayout mParentLayout;
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private MenuItem mSortByDate;
    private MenuItem mSortByName;
    private MenuItem mSortBySize;
    private MenuItem mSortByAscending;
    private SwipeRefreshLayout mSwipeRefresh;
    private FloatingActionButton mFabButton;
    private SquareFragment mSquareFragment;
    private ImageView mNavHeaderImage;

    private Drawable mIconDrawer;
    private Drawable mIconSort;
    private Drawable mIconAdd;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        attachPresenter();
        initView();
        mPresenter.requestImage(mImageList, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView(); // 断开 OnlinePresenter
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
        View decorView = window.getDecorView();
        if (hasFocus) {
            ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
            if (theme == null || theme == ThemeManager.Theme.WHITE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    StatusBarUtil.setTranslucentForDrawerLayout(this, mParentLayout, 70);
                }
                mToolbar.setTitleTextColor(getResources().getColor(R.color.colorGreyText));
                mActionBar.setHomeAsUpIndicator(mIconDrawer);
            } else {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        mParentLayout.openDrawer(GravityCompat.START);
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        SystemUtil.setIconEnable(menu, true);
        ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
        if (theme == null || theme == ThemeManager.Theme.WHITE) {
            menu.findItem(R.id.sort).setIcon(mIconSort);
        }
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
        switch (item.getItemId()) {
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
                mSquareFragment.backToTop();
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
     * 下拉刷新监听器回调函数
     */
    @Override
    public void onRefresh() {
        mPresenter.requestImage(mImageList, true);
    }

    @Override
    public void onDelete(String path, boolean isEmpty) {
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
     * 关联 OnlinePresenter
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
        runOnUiThread(() -> {
            mSquareFragment.update(imageList);
            if (mImageList.size() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        });
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
        runOnUiThread(() -> mSquareFragment.updateForAdd(list));
    }

    /**
     * 回调函数，用于请求不到数据时显示信息
     */
    @Override
    public void onShowMessage(String args) {
        runOnUiThread(() -> {
            if (args != null) {
                new Handler().postDelayed(() -> Toast.makeText(this, args, Toast.LENGTH_SHORT).show(), 100);
            }
            if (mImageList.size() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 回调函数，用于请求数据得到结果后隐藏下拉刷新
     */
    @Override
    public void onHideRefresh() {
        if (mSwipeRefresh.isRefreshing()) {
            mSwipeRefresh.setRefreshing(false);
        }
    }

    private void initView() {
        // Part 1, find id
        mParentLayout = findViewById(R.id.drawer_layout_home_activity_main);
        NavigationView navView = findViewById(R.id.navigation_view);
        View headerView = navView.getHeaderView(0);
        mToolbar = findViewById(R.id.toolbar_home_activity_main);
        mSwipeRefresh = findViewById(R.id.swipe_refresh_home_activity_main);
        mFabButton = findViewById(R.id.fab_home_activity_main);
        mSquareFragment = (SquareFragment) getSupportFragmentManager().findFragmentById(R.id.square_fragment);
        mNavHeaderImage = headerView.findViewById(R.id.nav_head_image);
        mEmptyView = findViewById(R.id.empty_view);

        // Part 2, setClickListener
        mToolbar.setOnClickListener(this);
        mFabButton.setOnClickListener(this);
        navView.setNavigationItemSelectedListener(this);
        mSwipeRefresh.setOnRefreshListener(this);

        // Part 3, init status
        initIconColor();
        initTheme();
        initToolbar();
        mSwipeRefresh.setColorSchemeResources(mColorPrimary);
    }

    private void initIconColor() {
        mIconDrawer = getResources().getDrawable(R.drawable.ic_drawer_menu);
        mIconSort = getResources().getDrawable(R.drawable.ic_sort);
        mIconAdd = getResources().getDrawable(R.drawable.ic_add_fab);
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            DrawableCompat.setTint(mIconDrawer, getResources().getColor(R.color.colorGreyText));
            DrawableCompat.setTint(mIconSort, getResources().getColor(R.color.colorGreyText));
            DrawableCompat.setTint(mIconAdd, getResources().getColor(R.color.colorGreyText));
            mFabButton.setBackgroundTintList(getColorStateListTest());
        } else {
            DrawableCompat.setTint(mIconDrawer, getResources().getColor(R.color.colorPrimaryWhite));
            DrawableCompat.setTint(mIconSort, getResources().getColor(R.color.colorPrimaryWhite));
            DrawableCompat.setTint(mIconAdd, getResources().getColor(R.color.colorPrimaryWhite));
        }
        mFabButton.setImageDrawable(mIconAdd);
    }

    private void initTheme() {
        ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
        if (theme == null) {
            mColorPrimary = R.color.colorPrimaryBlack;
            mMatisseTheme = R.style.MatisseBlackTheme;
            mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_white));
            // 初次安装时由于有权限申请，此时没有获取到焦点，所以会有一刹那没变色，这里设置一下就好了
            mToolbar.setTitleTextColor(getResources().getColor(R.color.colorGreyText));
            return;
        }
        switch (theme) {
            case JUST_LIKE:
                mColorPrimary = R.color.colorPrimary;
                mMatisseTheme = R.style.MatisseJustLikeTheme;
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_just_like));
                break;
            case WHITE:
                mColorPrimary = R.color.colorPrimaryBlack;
                mMatisseTheme = R.style.MatisseBlackTheme;
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_white));
                break;
            case BLACK:
                mColorPrimary = R.color.colorPrimaryBlack;
                mMatisseTheme = R.style.MatisseBlackTheme;
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_black));
                break;
            case GREY:
                mColorPrimary = R.color.colorPrimaryGrey;
                mMatisseTheme = R.style.MatisseGreyTheme;
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_grey));
                break;
            case GREEN:
                mColorPrimary = R.color.colorPrimaryGreen;
                mMatisseTheme = R.style.MatisseGreenTheme;
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_green));
                break;
            case RED:
                mColorPrimary = R.color.colorPrimaryRed;
                mMatisseTheme = R.style.MatisseRedTheme;
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_red));
                break;
            case PINK:
                mColorPrimary = R.color.colorPrimaryPink;
                mMatisseTheme = R.style.MatissePinkTheme;
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_pink));
                break;
            case BLUE:
                mColorPrimary = R.color.colorPrimaryBlue;
                mMatisseTheme = R.style.MatisseBlueTheme;
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_blue));
                break;
            case PURPLE:
                mColorPrimary = R.color.colorPrimaryPurple;
                mMatisseTheme = R.style.MatissePurpleTheme;
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_purple));
                break;
            case ORANGE:
                mColorPrimary = R.color.colorPrimaryOrange;
                mMatisseTheme = R.style.MatisseBrownTheme;
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_orange));
                break;
        }
    }

    private void initToolbar() {
        StatusBarUtil.setTransparentForDrawerLayout(this, mParentLayout);
        Window window = getWindow();
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_drawer_menu);
        }
    }

    private ColorStateList getColorStateListTest() {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed} // pressed
        };
        int color = getResources().getColor(R.color.colorPrimaryWhite);
        int[] colors = new int[]{color, color, color, color};
        return new ColorStateList(states, colors);
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
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
                .gridExpectedSize(SystemUtil.dp2px(this, 120.0F))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .autoHideToolbarOnSingleTap(true)
                .theme(mMatisseTheme)
                .forResult(REQUEST_SELECT_IMAGE);
    }
}
