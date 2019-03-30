package com.aaron.justlike.activity.download;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.BaseActivity;
import com.aaron.justlike.activity.online.PreviewActivity;
import com.aaron.justlike.adapter.download.DownloadManagerAdapter;
import com.aaron.justlike.common.ThemeManager;
import com.aaron.justlike.entity.Image;
import com.aaron.justlike.entity.PhotoEvent;
import com.aaron.justlike.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.mvp.presenter.download.BasePresenter;
import com.aaron.justlike.mvp.presenter.download.IPresenter;
import com.aaron.justlike.mvp.view.download.IView;
import com.aaron.justlike.util.EmptyViewUtil;
import com.aaron.justlike.util.FileUtil;
import com.aaron.justlike.util.SystemUtil;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadManagerActivity extends BaseActivity implements IView<Image>,
        DownloadManagerAdapter.Callback {

    private static final String PROGRESS_TITLE = "加载资源";
    private static final String PROGRESS_MESSAGE = "Loading...";
    private static final String SNACKBAR_TEXT = "加载失败";
    private static final String SNACKBAR_ACTION_TEXT = "重试";

    private List<Image> mImageList = new ArrayList<>();

    private IPresenter mPresenter;

    private ProgressDialog mDialog;
    private Toolbar mToolbar;
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
        mPresenter.requestImage(BasePresenter.DESCENDING); // 按最新下载来排序
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
            if (theme == null || theme == ThemeManager.Theme.WHITE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    window.setStatusBarColor(getResources().getColor(R.color.status_bar_background));
                }
                mToolbar.setTitleTextColor(getResources().getColor(R.color.colorGreyText));
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
                mPresenter.requestImage(BasePresenter.DESCENDING);
                break;
            case R.id.sort_oldest:
                mPresenter.requestImage(BasePresenter.ASCENDING);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 适配器中点击视图回调，用本地 App 打开
     */
    @Override
    public void onSearchByLocal(String path) {
        Intent openImage = new Intent(Intent.ACTION_VIEW);
        openImage.setDataAndType(FileUtil.getImageContentUri(this, new File(path)), "image/*");
        startActivity(openImage);
    }

    /**
     * 适配器中点击搜索按钮回调，搜索网络资源
     */
    @Override
    public void onSearchByOnline(String path) {
        mPresenter.findImageByOnline(path);
    }

    @Override
    public void attachPresenter() {
        mPresenter = new BasePresenter(this);
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
        Snackbar.make(mToolbar, SNACKBAR_TEXT, Snackbar.LENGTH_LONG)
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
        mToolbar = findViewById(R.id.activity_download_manager_toolbar);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mEmptyView = findViewById(R.id.empty_view);

        initIconColor();
        initToolbar(mToolbar);
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

    private void initToolbar(Toolbar toolbar) {
        Window window = getWindow();
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new DownloadManagerAdapter<>(mImageList, this);
        recyclerView.setAdapter(mAdapter);
    }
}
