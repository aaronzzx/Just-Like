package com.aaron.justlike.download.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.OnlineImageActivity;
import com.aaron.justlike.download.adapter.DownloadManagerAdapter;
import com.aaron.justlike.download.presenter.BasePresenter;
import com.aaron.justlike.download.presenter.IPresenter;
import com.aaron.justlike.entity.Image;
import com.aaron.justlike.util.FileUtils;
import com.aaron.justlike.util.SystemUtils;
import com.google.android.material.snackbar.Snackbar;
import com.jaeger.library.StatusBarUtil;
import com.kc.unsplash.models.Photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadManagerActivity extends AppCompatActivity implements IView<Image>,
        DownloadManagerAdapter.Callback {

    private static final String PROGRESS_TITLE = "加载资源";
    private static final String PROGRESS_MESSAGE = "Loading...";

    private static final String SNACKBAR_TEXT = "加载失败";
    private static final String SNACKBAR_ACTION_TEXT = "重试";

    private List<Image> mImageList = new ArrayList<>();

    private IPresenter mPresenter;

    private ProgressDialog mDialog;
    private Toolbar mToolbar;
    private DownloadManagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 70);
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
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_download_manager_menu, menu);
        SystemUtils.setIconEnable(menu, true);
        menu.findItem(R.id.sort_latest).setChecked(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_latest:
                item.setChecked(true);
                mPresenter.requestImage(BasePresenter.DESCENDING);
                break;
            case R.id.sort_oldest:
                item.setChecked(true);
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
        openImage.setDataAndType(FileUtils.getImageContentUri(this, new File(path)), "image/*");
        startActivity(openImage);
    }

    /**
     * 适配器中点击视图回调，搜索网络资源
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
    }

    /**
     * 搜索网络资源的回调
     */
    @Override
    public void onOpenPreview(Photo photo) {
        Intent intent = new Intent(this, OnlineImageActivity.class);
        intent.putExtra("photo", photo);
        startActivity(intent);
    }

    /**
     * 搜索网络资源的回调
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
        RecyclerView recyclerView = findViewById(R.id.rv_home_activity_main);

        initToolbar(mToolbar);
        initRecyclerView(recyclerView);
    }

    private void initToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new DownloadManagerAdapter<>(mImageList, this);
        recyclerView.setAdapter(mAdapter);
    }
}
