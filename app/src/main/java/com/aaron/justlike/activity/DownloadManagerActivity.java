package com.aaron.justlike.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.AboutLibraryAdapter;
import com.aaron.justlike.adapter.AboutMessageAdapter;
import com.aaron.justlike.adapter.DownloadManagerAdapter;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.util.FileUtils;
import com.aaron.justlike.util.SystemUtils;
import com.jaeger.library.StatusBarUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DownloadManagerActivity extends AppCompatActivity {

    private DownloadManagerAdapter mAdapter;
    private List<Image> mImageList = new ArrayList<>();
    private static final String PATH = Environment.getExternalStoragePublicDirectory
            (Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/JustLike/online";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_manager);
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorPrimary));
        initViews();
        // 设置默认排序
        FileUtils.sortByDate(mImageList, false);
    }

    /**
     * 标题栏返回键销毁活动
     */
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
                FileUtils.sortByDate(mImageList, false);
                mAdapter.notifyItemRangeChanged(0, mImageList.size() - 1);
                break;
            case R.id.sort_oldest:
                item.setChecked(true);
                FileUtils.sortByDate(mImageList, true);
                mAdapter.notifyItemRangeChanged(0, mImageList.size() - 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.activity_download_manager_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        FileUtils.getLocalFiles(mImageList, PATH, "jpg");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new DownloadManagerAdapter(this, mImageList);
        recyclerView.setAdapter(mAdapter);
    }
}