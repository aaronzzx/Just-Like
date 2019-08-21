package com.aaron.justlike.collection;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.base.impl.OnClickListenerImpl;
import com.aaron.base.util.StatusBarUtils;
import com.aaron.justlike.R;
import com.aaron.justlike.collection.element.ElementActivity;
import com.aaron.justlike.common.CommonActivity;
import com.aaron.justlike.common.bean.Album;
import com.aaron.justlike.common.bean.Collection;
import com.aaron.justlike.common.event.SelectEvent;
import com.aaron.justlike.common.event.UpdateEvent;
import com.aaron.justlike.common.manager.ThemeManager;
import com.aaron.justlike.common.manager.UiManager;
import com.aaron.justlike.common.util.EmptyViewUtil;
import com.aaron.justlike.common.util.SelectorUtils;
import com.aaron.justlike.common.util.SystemUtil;
import com.aaron.justlike.common.widget.MyGridLayoutManager;
import com.aaron.justlike.common.widget.imageSelector.ImageSelector;
import com.aaron.justlike.main.MainActivity;
import com.aaron.justlike.online.home.OnlineActivity;
import com.aaron.justlike.others.about.AboutActivity;
import com.aaron.justlike.others.download.DownloadManagerActivity;
import com.aaron.justlike.others.theme.ThemeActivity;
import com.aaron.ui.util.DialogUtil;
import com.aaron.ui.widget.TopBar;
import com.blankj.utilcode.util.StringUtils;
import com.google.android.material.navigation.NavigationView;

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
//    private Toolbar mToolbar;
    private TopBar mTopBar;
//    private View mStatusBar;
    private ImageView mNavHeaderImage;

    private ActionBar mActionBar;
    private Drawable mIconDrawer;
    private Drawable mIconAdd;
    private Dialog mDialog;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private MyGridLayoutManager mLayoutManager;
    private CollectionAdapter mAdapter;

    private int mColorAccent;
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
        ImageSelector.getInstance().setCallback(null);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = getWindow();
        View decorView = window.getDecorView();
        if (hasFocus) {
            mNavView.setCheckedItem(R.id.nav_collection);
            ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
            mTopBar.setTextColor(getResources().getColor(R.color.base_white));
            if (theme == null || theme == ThemeManager.Theme.WHITE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    window.setStatusBarColor(getResources().getColor(R.color.status_bar_background));
                }
//                mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccentWhite));
                mTopBar.setTextColor(getResources().getColor(R.color.colorAccentWhite));
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 添加集合
        if (item.getItemId() == R.id.add_collection) {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.app_dialog_input, null);
            EditText etContent = dialogView.findViewById(R.id.app_et_content);
            Button btnLeft = dialogView.findViewById(R.id.app_btn_left);
            Button btnRight = dialogView.findViewById(R.id.app_btn_right);
            btnLeft.setText(R.string.app_cancel);
            btnRight.setText(R.string.app_confirm);
            btnRight.setTextColor(mColorAccent);
            Dialog dialog = DialogUtil.createDialog(this, dialogView);
            btnLeft.setOnClickListener(new OnClickListenerImpl() {
                @Override
                public void onViewClick(View v, long interval) {
                    dialog.dismiss();
                }
            });
            btnRight.setOnClickListener(new OnClickListenerImpl() {
                @Override
                public void onViewClick(View v, long interval) {
                    String name = etContent.getText().toString();
                    if (StringUtils.isEmpty(name)) {
                        UiManager.showShortCenter(R.string.app_type_collection_name_first);
                    } else if (mPresenter.isCollectionExists(name)) {
                        UiManager.showShortCenter(R.string.app_collection_exists);
                    } else {
                        dialog.dismiss();
                        // 打开图片选择器让用户选择图片添加到集合
                        String title = etContent.getText().toString();
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
                    }
                }
            });
            dialog.show();
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
//        overridePendingTransition(0, R.anim.activity_slide_out);
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
//        overridePendingTransition(R.anim.activity_slide_in, android.R.anim.fade_out);
    }

    @Override
    public void onLongTap(View v, int pos) {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.app_dialog_normal_alert, null);
        TextView tvTitle = dialogView.findViewById(R.id.app_tv_title);
        TextView tvContent = dialogView.findViewById(R.id.app_tv_content);
        Button btnLeft = dialogView.findViewById(R.id.app_btn_left);
        Button btnRight = dialogView.findViewById(R.id.app_btn_right);
        tvTitle.setText(R.string.app_notice);
        tvContent.setText(R.string.app_delete_collection_forever);
        btnLeft.setText(R.string.app_cancel);
        btnRight.setText(R.string.app_confirm);
        btnRight.setTextColor(mColorAccent);
        Dialog dialog = DialogUtil.createDialog(this, dialogView);
        btnLeft.setOnClickListener(new OnClickListenerImpl() {
            @Override
            public void onViewClick(View v, long interval) {
                dialog.dismiss();
            }
        });
        btnRight.setOnClickListener(new OnClickListenerImpl() {
            @Override
            public void onViewClick(View v, long interval) {
                dialog.dismiss();
                String title = mCollections.get(pos).getCollectionTitle();
                mPresenter.deleteCollection(title);
                mCollections.remove(pos);
                mAdapter.notifyDataSetChanged();
                if (mCollections.size() == 0) {
                    EmptyViewUtil.showEmptyView(mEmptyView);
                } else {
                    EmptyViewUtil.hideEmptyView(mEmptyView);
                }
            }
        });
        dialog.show();
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
        mTopBar = findViewById(R.id.activity_collection_toolbar);
//        mStatusBar = findViewById(R.id.status_bar);
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

        mDialog = DialogUtil.createDialog(this, R.layout.app_dialog_loading);

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
            DrawableCompat.setTint(mIconDrawer, getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconAdd, getResources().getColor(R.color.colorAccentWhite));
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
//            mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccentWhite));
            mTopBar.setTextColor(getResources().getColor(R.color.colorAccentWhite));
//            mStatusBar.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimaryWhite)));
            return;
        }
        Drawable normal = new ColorDrawable(Color.WHITE);
        Drawable checked = getDrawable(R.drawable.app_bg_nav_checked);
        switch (theme) {
            case JUST_LIKE:
                mColorAccent = getResources().getColor(R.color.colorAccent);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_just_like));
                break;
            case WHITE:
            default:
                mColorAccent = getResources().getColor(R.color.colorAccentWhite);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_white));
                break;
            case BLACK:
                mColorAccent = getResources().getColor(R.color.colorAccentBlack);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_black));
                break;
            case GREY:
                mColorAccent = getResources().getColor(R.color.colorAccentGrey);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_grey));
                break;
            case GREEN:
                mColorAccent = getResources().getColor(R.color.colorAccentGreen);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_green));
                break;
            case RED:
                mColorAccent = getResources().getColor(R.color.colorAccentRed);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_red));
                break;
            case PINK:
                mColorAccent = getResources().getColor(R.color.colorAccentPink);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_pink));
                break;
            case BLUE:
                mColorAccent = getResources().getColor(R.color.colorAccentBlue);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_blue));
                break;
            case PURPLE:
                mColorAccent = getResources().getColor(R.color.colorAccentPurple);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_purple));
                break;
            case ORANGE:
                mColorAccent = getResources().getColor(R.color.colorAccentOrange);
                mNavHeaderImage.setImageDrawable(getResources().getDrawable(R.drawable.theme_orange));
                break;
        }
        if (theme == ThemeManager.Theme.WHITE) {
            checked.setTint(Color.BLACK);
        } else {
            checked.setTint(mColorAccent);
        }
        checked.setAlpha(20);
        Drawable selector = SelectorUtils.createCheckedSelector(this, normal, checked);
        mNavView.setItemBackground(selector);
    }

    private void initToolbar() {
        StatusBarUtils.setTransparent(this);
//        setSupportActionBar(mToolbar);
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
//                overridePendingTransition(R.anim.activity_slide_in, android.R.anim.fade_out);
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
