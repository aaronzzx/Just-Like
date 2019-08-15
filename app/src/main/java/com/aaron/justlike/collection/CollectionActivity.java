package com.aaron.justlike.collection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.justlike.R;
import com.aaron.justlike.collection.element.ElementActivity;
import com.aaron.justlike.common.CommonActivity;
import com.aaron.justlike.common.bean.Album;
import com.aaron.justlike.common.bean.Collection;
import com.aaron.justlike.common.event.SelectEvent;
import com.aaron.justlike.common.event.UpdateEvent;
import com.aaron.justlike.common.manager.ThemeManager;
import com.aaron.justlike.common.util.EmptyViewUtil;
import com.aaron.justlike.common.util.SystemUtil;
import com.aaron.justlike.common.widget.MyGridLayoutManager;
import com.aaron.justlike.common.widget.imageSelector.ImageSelector;
import com.aaron.justlike.main.MainActivity;
import com.aaron.justlike.online.home.OnlineActivity;
import com.aaron.justlike.others.about.AboutActivity;
import com.aaron.justlike.others.download.DownloadManagerActivity;
import com.aaron.justlike.others.theme.ThemeActivity;
import com.google.android.material.navigation.NavigationView;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends CommonActivity implements ICollectionCommunicable,
        ICollectionContract.V {

    private ICollectionContract.P mPresenter;

    private DrawerLayout mParentLayout;
    private NavigationView mNavView;
    private Toolbar mToolbar;
    private View mStatusBar;
    private ImageView mNavHeaderImage;

    private ActionBar mActionBar;
    private Drawable mIconDrawer;
    private Drawable mIconAdd;
    private ProgressDialog mDialog;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private MyGridLayoutManager mLayoutManager;
    private CollectionAdapter mAdapter;

    private int mColorPrimary;
    private List<Album> mCollections = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        EventBus.getDefault().register(this);
        initView();
        mPresenter = new CollectionPresenter(this);
        mPresenter.requestCollection(mCollections);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.detachView();
        ImageSelector.getInstance()
                .setCallback(null);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = getWindow();
        View decorView = window.getDecorView();
        if (hasFocus) {
            mNavView.setCheckedItem(R.id.nav_collection);
            ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
            if (theme == null || theme == ThemeManager.Theme.WHITE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    window.setStatusBarColor(getResources().getColor(R.color.status_bar_background));
                }
                mToolbar.setTitleTextColor(getResources().getColor(R.color.colorGreyText));
                mActionBar.setHomeAsUpIndicator(mIconDrawer);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_collection_menu, menu);
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            menu.findItem(R.id.add_collection).setIcon(mIconAdd);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_collection: // 添加集合
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_collection, null);
                EditText editText = dialogView.findViewById(R.id.input_collection_name);
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setTitle("创建集合")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 打开图片选择器让用户选择图片添加到集合
                            String title = editText.getText().toString();
                            ImageSelector.getInstance(CollectionActivity.this)
                                    .setTitle(title)
                                    .setFilePath("/storage/emulated/0/Pictures/JustLike")
                                    .setSelectedImage(null)
                                    .setCallback(new ImageSelector.ImageCallback() {
                                        @Override
                                        public void onResponse(List<String> response, String title) {
                                            showProgress();
                                            int i = mPresenter.saveCollection(response, title);
                                            if (i == 1) {
                                                mPresenter.requestCollection(mCollections);
                                            }
                                        }
                                    })
                                    .start();
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                        })
                        .create();
                alertDialog.setOnShowListener(dialog -> SystemUtil.showKeyboard(editText));
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        overridePendingTransition(0, R.anim.activity_slide_out);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onSelectEvent(SelectEvent event) {
        Collection collection = event.getCollection();
        Album album = new Album();
        album.setCollectionTitle(collection.getTitle());
        album.setElementTotal(String.valueOf(collection.getTotal()));
        album.setImagePath(collection.getPath());
        album.setCreateAt(collection.getCreateAt());
        mCollections.add(0, album);
//        mAdapter.notifyItemRangeInserted(0, 1);
        mAdapter.notifyItemRangeChanged(0, mCollections.size());
//        mRecyclerView.scrollToPosition(0);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUpdateEvent(UpdateEvent event) {
        mPresenter.requestCollection(mCollections);
    }

    @Override
    public void onTap(View v, int pos) {
        Intent intent = new Intent(this, ElementActivity.class);
        intent.putExtra("title", mCollections.get(pos).getCollectionTitle());
        startActivity(intent);
        overridePendingTransition(R.anim.activity_slide_in, android.R.anim.fade_out);
    }

    @Override
    public void onLongTap(View v, int pos) {
        new AlertDialog.Builder(this)
                .setTitle("删除集合")
                .setMessage("请慎重！集合的所有信息将被清除")
                .setPositiveButton("确定", (dialog, which) -> {
                    Log.d("CollectionActivity", "which: " + which);
                    String title = mCollections.get(pos).getCollectionTitle();
                    mPresenter.deleteCollection(title);
                    mCollections.remove(pos);
                    mAdapter.notifyDataSetChanged();
                    if (mCollections.size() == 0) {
                        EmptyViewUtil.showEmptyView(mEmptyView);
                    } else {
                        EmptyViewUtil.hideEmptyView(mEmptyView);
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                }).show();
    }

    @Override
    public <E> void onShowImage(List<E> list) {
        mCollections.clear();
        for (E e : list) {
            mCollections.add((Album) e);
        }
        runOnUiThread(() -> {
            hideProgress();
//            mAdapter.notifyItemRangeChanged(0, mCollections.size());
            mAdapter.notifyDataSetChanged();
            if (mCollections.size() == 0) {
                EmptyViewUtil.showEmptyView(mEmptyView);
            } else {
                EmptyViewUtil.hideEmptyView(mEmptyView);
            }
        });
    }

    private void initView() {
        mParentLayout = findViewById(R.id.drawer_layout);
        mNavView = findViewById(R.id.navigation_view);
        mToolbar = findViewById(R.id.activity_collection_toolbar);
        mStatusBar = findViewById(R.id.status_bar);
        mRecyclerView = findViewById(R.id.recycler_view);
        mEmptyView = findViewById(R.id.empty_view);
        View headerView = mNavView.getHeaderView(0);
        mNavHeaderImage = headerView.findViewById(R.id.nav_head_image);

        mNavView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    startActivityByNav(OnlineActivity.class);
                    break;
                case R.id.nav_mine:
                    startActivityByNav(MainActivity.class);
                    break;
                case R.id.nav_collection:
                    mParentLayout.closeDrawers();
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

        mDialog = new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setTitle("创建集合");
        mDialog.setMessage("Loading...");

        initIconColor();
        initTheme();
        initToolbar();
        initRecyclerView();
    }

    private void initIconColor() {
        mIconDrawer = getResources().getDrawable(R.drawable.ic_drawer_menu);
        mIconAdd = getResources().getDrawable(R.drawable.ic_add);
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            DrawableCompat.setTint(mIconDrawer, getResources().getColor(R.color.colorGreyText));
            DrawableCompat.setTint(mIconAdd, getResources().getColor(R.color.colorGreyText));
        } else {
            DrawableCompat.setTint(mIconDrawer, getResources().getColor(R.color.colorPrimaryWhite));
            DrawableCompat.setTint(mIconAdd, getResources().getColor(R.color.colorPrimaryWhite));
        }
    }

    private void initTheme() {
        ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
        if (theme == null) {
            mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_white));
            // 初次安装时由于有权限申请，此时没有获取到焦点，所以会有一刹那没变色，这里设置一下就好了
            mToolbar.setTitleTextColor(getResources().getColor(R.color.colorGreyText));
            mStatusBar.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimaryWhite)));
            return;
        }
        switch (theme) {
            case JUST_LIKE:
                mColorPrimary = getResources().getColor(R.color.colorPrimary);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_just_like));
                break;
            case WHITE:
                mColorPrimary = getResources().getColor(R.color.colorPrimaryWhite);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_white));
                break;
            case BLACK:
                mColorPrimary = getResources().getColor(R.color.colorPrimaryBlack);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_black));
                break;
            case GREY:
                mColorPrimary = getResources().getColor(R.color.colorPrimaryGrey);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_grey));
                break;
            case GREEN:
                mColorPrimary = getResources().getColor(R.color.colorPrimaryGreen);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_green));
                break;
            case RED:
                mColorPrimary = getResources().getColor(R.color.colorPrimaryRed);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_red));
                break;
            case PINK:
                mColorPrimary = getResources().getColor(R.color.colorPrimaryPink);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_pink));
                break;
            case BLUE:
                mColorPrimary = getResources().getColor(R.color.colorPrimaryBlue);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_blue));
                break;
            case PURPLE:
                mColorPrimary = getResources().getColor(R.color.colorPrimaryPurple);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_purple));
                break;
            case ORANGE:
                mColorPrimary = getResources().getColor(R.color.colorPrimaryOrange);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_orange));
                break;
        }
        mStatusBar.setBackground(new ColorDrawable(mColorPrimary));
    }

    private void initToolbar() {
        StatusBarUtil.setTransparentForDrawerLayout(this, mParentLayout);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_drawer_menu);
        }
    }

    private void initRecyclerView() {
        ((DefaultItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mLayoutManager = new MyGridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new YItemDecoration());
        mAdapter = new CollectionAdapter(mCollections);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showProgress() {
        mDialog.show();
    }

    private void hideProgress() {
        mDialog.dismiss();
    }

    private void startActivityByNav(Class whichActivity) {
        mParentLayout.closeDrawers();
        mParentLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                Intent intent = new Intent(CollectionActivity.this, whichActivity);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_slide_in, android.R.anim.fade_out);
                mParentLayout.removeDrawerListener(this);
            }
        });
    }

    private class YItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.bottom = 0;
            outRect.top = SystemUtil.dp2px(CollectionActivity.this, 9.9F);
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 0;
            }
        }
    }
}
