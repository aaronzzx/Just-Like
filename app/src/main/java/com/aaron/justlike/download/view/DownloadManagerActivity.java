package com.aaron.justlike.download.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.DownloadManagerAdapter;
import com.aaron.justlike.download.presenter.BasePresenter;
import com.aaron.justlike.download.presenter.IPresenter;
import com.aaron.justlike.entity.Image;
import com.aaron.justlike.util.SystemUtils;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadManagerActivity extends AppCompatActivity implements IView<Image> {

    private List<Image> mImageList = new ArrayList<>();

    private IPresenter mPresenter;

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

    private void initView() {
        Toolbar toolbar = findViewById(R.id.activity_download_manager_toolbar);
        RecyclerView recyclerView = findViewById(R.id.rv_home_activity_main);

        initToolbar(toolbar);
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
        mAdapter = new DownloadManagerAdapter(this, mImageList);
        recyclerView.setAdapter(mAdapter);
    }
}
