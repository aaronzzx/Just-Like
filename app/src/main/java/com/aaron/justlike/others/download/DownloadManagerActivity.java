package com.aaron.justlike.others.download;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.ActionBar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.justlike.R;
import com.aaron.justlike.common.CommonActivity;
import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.common.event.PhotoEvent;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.manager.ThemeManager;
import com.aaron.justlike.common.util.EmptyViewUtil;
import com.aaron.justlike.common.util.SystemUtil;
import com.aaron.justlike.online.preview.PreviewActivity;
import com.aaron.ui.widget.TopBar;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class DownloadManagerActivity extends CommonActivity implements IDownloadContract.V<Image>,
        IDownloadCommunicable {

    private static final String PROGRESS_TITLE = "加载资源";
    private static final String PROGRESS_MESSAGE = "Loading...";
    private static final String SNACKBAR_TEXT = "加载失败";
    private static final String SNACKBAR_ACTION_TEXT = "重试";

    private List<Image> mImageList = new ArrayList<>();

    private IDownloadContract.P mPresenter;

    private ProgressDialog mDialog;
//    private Toolbar mToolbar;
    private TopBar mTopBar;
    private ActionBar mActionBar;
    private Drawable mIconBack;
    private View mEmptyView;
    private DownloadManagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        initView();
        attachPresenter();
        mPresenter.requestImage(DownloadPresenter.DESCENDING); // 按最新下载来排序
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = getWindow();
        View decorView = window.getDecorView();
        if (hasFocus) {
            ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
            mTopBar.setTextColor(getResources().getColor(R.color.base_white));
            if (theme == null || theme == ThemeManager.Theme.WHITE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    window.setStatusBarColor(getResources().getColor(R.color.status_bar_background));
                }
//                mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccentWhite));
                mTopBar.setTextColor(getResources().getColor(R.color.colorAccentWhite));
                mActionBar.setHomeAsUpIndicator(mIconBack);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out);
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_download_manager_menu, menu);
        // 打开 Popup 菜单的图标
        SystemUtil.setIconEnable(menu, true);
        menu.findItem(R.id.sort_latest).setChecked(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.sort_latest:
                mPresenter.requestImage(DownloadPresenter.DESCENDING);
                break;
            case R.id.sort_oldest:
                mPresenter.requestImage(DownloadPresenter.ASCENDING);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 适配器中点击搜索按钮回调，搜索网络资源
     */
    @Override
    public void onTap(View v, String path) {
        mPresenter.findImageByOnline(path);
    }

    @Override
    public void attachPresenter() {
        mPresenter = new DownloadPresenter(this);
    }

    @Override
    public void onShowImage(List<Image> list) {
        mImageList.clear();
        mImageList.addAll(list);
        mAdapter.notifyItemRangeChanged(0, mImageList.size());
        if (mImageList.size() == 0) {
            EmptyViewUtil.showEmptyView(mEmptyView);
        } else {
            EmptyViewUtil.hideEmptyView(mEmptyView);
        }
    }

    /**
     * 搜索网络资源的回调，打开在线壁纸的预览窗口
     */
    @Override
    public void onOpenPreview(Photo photo) {
        EventBus.getDefault().postSticky(new PhotoEvent(photo));
        Intent intent = new Intent(this, PreviewActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 搜索网络资源的回调，显示 SnackBar 可选择重新搜索
     */
    @Override
    public void onShowSnackBar(String path) {
        Snackbar.make(mTopBar, SNACKBAR_TEXT, Snackbar.LENGTH_LONG)
                .setAction(SNACKBAR_ACTION_TEXT, v -> mPresenter.findImageByOnline(path))
                .show();
    }

    @Override
    public void onShowProgress() {
        mDialog = new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setTitle(PROGRESS_TITLE);
        mDialog.setMessage(PROGRESS_MESSAGE);
        mDialog.show();
    }

    @Override
    public void onHideProgress() {
        mDialog.dismiss();
    }

    private void initView() {
        mTopBar = findViewById(R.id.activity_download_manager_toolbar);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mEmptyView = findViewById(R.id.empty_view);

        initIconColor();
        initToolbar();
        initRecyclerView(recyclerView);
    }

    private void initIconColor() {
        mIconBack = getResources().getDrawable(R.drawable.ic_back);
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorAccentWhite));
        } else {
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorPrimaryWhite));
        }
    }

    private void initToolbar() {
        Window window = getWindow();
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new DownloadManagerAdapter(mImageList);
        recyclerView.setAdapter(mAdapter);
    }
}
