package com.aaron.justlike.main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aaron.base.impl.OnClickListenerImpl;
import com.aaron.base.util.StatusBarUtils;
import com.aaron.justlike.R;
import com.aaron.justlike.collection.CollectionActivity;
import com.aaron.justlike.common.CommonActivity;
import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.common.event.DeleteEvent;
import com.aaron.justlike.common.http.glide.GlideEngine;
import com.aaron.justlike.common.impl.SquareItemDecoration;
import com.aaron.justlike.common.manager.ThemeManager;
import com.aaron.justlike.common.manager.UiManager;
import com.aaron.justlike.common.util.SystemUtil;
import com.aaron.justlike.common.widget.MyGridLayoutManager;
import com.aaron.justlike.online.home.OnlineActivity;
import com.aaron.justlike.others.about.AboutActivity;
import com.aaron.justlike.others.download.DownloadManagerActivity;
import com.aaron.justlike.others.theme.ThemeActivity;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends CommonActivity implements IMainContract.V<Image>, IMainCommunicable{

    private static final int REQUEST_SELECT_IMAGE = 1;

    private int mMatisseTheme;
    private int mColorPrimary;
    private int mSortType;
    private boolean mIsAscending;
    private List<Image> mImageList = new ArrayList<>();

    private IMainContract.P<Image> mPresenter;
    private RecyclerView.Adapter mAdapter;

    private DrawerLayout mParentLayout;
    private NavigationView mNavView;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRv;
    private FloatingActionButton mFabButton;
    private ImageView mNavHeaderImage;
    private View mEmptyView;

    private ActionBar mActionBar;
    private MenuItem mSortByDate;
    private MenuItem mSortByName;
    private MenuItem mSortBySize;
    private MenuItem mSortByAscending;

    private Drawable mIconDrawer;
    private Drawable mIconSort;
    private Drawable mIconAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        requestPermission();
        attachPresenter();
        initView();
        mPresenter.requestImage(mImageList, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.detachView(); // 断开 OnlinePresenter
    }

    @Override
    public void onBackPressed() {
        if (mParentLayout.isDrawerOpen(GravityCompat.START)) {
            mParentLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
//        finish();
//        overridePendingTransition(0, R.anim.activity_slide_out);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = getWindow();
        View decorView = window.getDecorView();
        if (hasFocus) {
            mNavView.setCheckedItem(R.id.nav_mine);
            ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
            if (theme == null || theme == ThemeManager.Theme.WHITE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    StatusBarUtils.setTransparent(this, true);
                } else {
//                    StatusBarUtil.setTranslucentForDrawerLayout(this, mParentLayout, 70);
                    StatusBarUtils.setTranslucent(this);
                }
                mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccentWhite));
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
        getMenuInflater().inflate(R.menu.activity_mine_menu, menu);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
        if (requestCode == REQUEST_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                List<String> selectedList = Matisse.obtainPathResult(data);
                mPresenter.addImage(mImageList, selectedList);
            }
        }
    }

    /**
     * 接收 PreviewActivity 传过来的关于被删除图片的信息，并更新 UI
     */
    @Subscribe()
    public void onDeleteEvent(DeleteEvent event) {
        if (event.getEventType() == DeleteEvent.FROM_MAIN_ACTIVITY) {
            int position = event.getPosition();
            String path = event.getPath();
            mImageList.remove(position);
            mAdapter.notifyDataSetChanged();
            mPresenter.deleteImage(path, mImageList.isEmpty());
        }
    }

    @Override
    public void onDelete(String path, boolean isListEmpty) {
        mPresenter.deleteImage(path, isListEmpty);
    }

    @Override
    public void attachPresenter() {
        mPresenter = new MainPresenter(this);
    }

    @Override
    public void onShowImage(List<Image> imageList, int sortType, boolean ascendingOrder) {
        mImageList.clear();
        mImageList.addAll(imageList);
        runOnUiThread(() -> {
            mAdapter.notifyDataSetChanged();
            mRv.scrollToPosition(0);
        });
        mSortType = sortType;
        mIsAscending = ascendingOrder;
    }

    @Override
    public void onShowAddImage(List<Image> list) {
        mImageList.addAll(0, list);
        runOnUiThread(() -> {
            mAdapter.notifyItemRangeInserted(0, list.size());
            mAdapter.notifyItemRangeChanged(list.size(), mImageList.size() - list.size());
            mRv.scrollToPosition(0);
        });
    }

    @Override
    public void onShowMessage(String args) {
        runOnUiThread(() -> {
            if (args != null) UiManager.showShort(args);
        });
    }

    @Override
    public void onShowEmptyView() {
        runOnUiThread(() -> mEmptyView.setVisibility(View.VISIBLE));
    }

    @Override
    public void onHideEmptyView() {
        runOnUiThread(() -> mEmptyView.setVisibility(View.GONE));
    }

    @Override
    public void onHideRefresh() {
        runOnUiThread(() -> mSwipeRefresh.setRefreshing(false));
    }

    private void initView() {
        // Part 1, find id
        mParentLayout = findViewById(R.id.drawer_layout_home_activity_main);
        mNavView = findViewById(R.id.navigation_view);
        View headerView = mNavView.getHeaderView(0);
        mNavHeaderImage = headerView.findViewById(R.id.nav_head_image);
        mToolbar = findViewById(R.id.toolbar_home_activity_main);
        mSwipeRefresh = findViewById(R.id.swipe_refresh_home_activity_main);
        mRv = findViewById(R.id.rv);
        mFabButton = findViewById(R.id.fab_home_activity_main);
        mEmptyView = findViewById(R.id.empty_view);

        // Part 2
        mToolbar.setOnClickListener(new OnClickListenerImpl() {
            @Override
            public void onViewClick(View v, long interval) {
                // 查找当前屏幕内第一个可见的 View
                View firstVisibleItem = mRv.getChildAt(0);
                // 查找当前 View 在 RecyclerView 中处于哪个位置
                int itemPosition = mRv.getChildLayoutPosition(firstVisibleItem);
                if (itemPosition >= 48) {
                    mRv.scrollToPosition(36);
                }
                mRv.smoothScrollToPosition(0);
            }
        });
        mFabButton.setOnClickListener(new OnClickListenerImpl() {
            @Override
            public void onViewClick(View v, long interval) {
                openImageSelector();
            }
        });
        mNavView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    startActivityByNav(OnlineActivity.class);
                    break;
                case R.id.nav_mine:
                    mParentLayout.closeDrawers();
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
        });
        mSwipeRefresh.setOnRefreshListener(() -> mPresenter.requestImage(mImageList, true));
        ((DefaultItemAnimator) mRv.getItemAnimator()).setSupportsChangeAnimations(false);
        MyGridLayoutManager layoutManager = new MyGridLayoutManager(this, 3);
        mRv.setLayoutManager(layoutManager);
        mRv.addItemDecoration(new SquareItemDecoration.XItemDecoration());
        mRv.addItemDecoration(new SquareItemDecoration.YItemDecoration());
        mAdapter = new MainAdapter(mImageList);
        mRv.setAdapter(mAdapter);
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccentWhite));
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
        StatusBarUtils.setTransparent(this);
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
                break;
            case MainPresenter.SORT_BY_NAME:
                mSortByName.setChecked(true);
                break;
            case MainPresenter.SORT_BY_SIZE:
                mSortBySize.setChecked(true);
                break;
            default:
                mSortByDate.setChecked(true);
                break;
        }
        mSortByAscending.setChecked(ascendingOrder);
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
        PermissionUtils.permission(PermissionConstants.STORAGE)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied() {
                        UiManager.showShort("不开启权限将无法使用壁纸缓存功能");
                    }
                })
                .request();
    }

    /**
     * 点击菜单跳转 Activity --- for NavigationView
     */
    private void startActivityByNav(Class target) {
        mParentLayout.closeDrawers();
        mParentLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                Intent intent = new Intent(MainActivity.this, target);
                startActivity(intent);
//                overridePendingTransition(R.anim.activity_slide_in, android.R.anim.fade_out);
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
