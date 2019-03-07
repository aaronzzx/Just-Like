package com.aaron.justlike.app.collection.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.aaron.justlike.R;
import com.aaron.justlike.app.collection.adapter.CollectionAdapter;
import com.aaron.justlike.app.collection.adapter.ImageSelector;
import com.aaron.justlike.app.collection.entity.Album;
import com.aaron.justlike.app.collection.entity.Collection;
import com.aaron.justlike.app.collection.entity.Element;
import com.aaron.justlike.app.collection.entity.SelectEvent;
import com.aaron.justlike.app.collection.entity.UpdateEvent;
import com.aaron.justlike.app.collection.presenter.CollectionPresenter;
import com.aaron.justlike.app.collection.presenter.ICollectionPresenter;
import com.aaron.justlike.custom.MyGridLayoutManager;
import com.aaron.justlike.util.SystemUtils;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

public class CollectionActivity extends AppCompatActivity implements CollectionAdapter.OnPressCallback,
        ICollectionView, ImageSelector.ImageCallback {

    private ICollectionPresenter mPresenter;

    private ProgressDialog mDialog;
    private RecyclerView mRecyclerView;
    private MyGridLayoutManager mLayoutManager;
    private CollectionAdapter mAdapter;

    private List<Album> mCollections = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        EventBus.getDefault().register(this);
        initView();
        mPresenter = new CollectionPresenter();
        mPresenter.attachView(this);
        mPresenter.requestCollection(mCollections);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_collection_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.add: // 添加集合
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_collection, null);
                EditText editText = dialogView.findViewById(R.id.input_collection_name);
                new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setTitle("创建集合")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 打开图片选择器让用户选择图片添加到集合
                            String title = editText.getText().toString();
                            ImageSelector.getInstance(this)
                                    .setTitle(title)
                                    .setFilePath("/storage/emulated/0/Pictures/JustLike")
                                    .setCallback(this)
                                    .start();
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                        }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
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
    public void onPress(int position) {
        Intent intent = new Intent(this, ElementActivity.class);
        intent.putExtra("title", mCollections.get(position).getCollectionTitle());
        startActivity(intent);
    }

    @Override
    public void onLongPress(int position) {
        new AlertDialog.Builder(this)
                .setTitle("删除集合")
                .setMessage("请慎重！集合的所有信息将被清除")
                .setPositiveButton("确定", (dialog, which) -> {
                    mCollections.remove(position);
                    mAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("取消", (dialog, which) -> {
                }).show();
    }

    @Override
    public void onResponse(List<String> response, String title) {
        showProgress();

        Collection info = new Collection();
        info.setTitle(title);
        info.setTotal(response.size());
        info.setPath(response.get(response.size() - 1));
        info.setCreateAt(System.currentTimeMillis());
        info.save();

        for (String path : response) {
            Element element = new Element();
            element.setTitle(title);
            element.setPath(path);
            element.setCreateAt(System.currentTimeMillis());
            element.save();
        }

        new Thread(() -> {
            try {
                Thread.sleep(600);
                runOnUiThread(() -> {
                    hideProgress();
                    mPresenter.requestCollection(mCollections);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public <E> void onShowImage(List<E> list) {
        mCollections.clear();
        for (E e : list) {
            mCollections.add((Album) e);
        }
//        mAdapter.notifyItemRangeChanged(0, mCollections.size());
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);

        setStatusBar();
        initToolbar();
        initRecyclerView();
    }

    private void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 70);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.activity_collection_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initRecyclerView() {
        mLayoutManager = new MyGridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new YItemDecoration());
        mAdapter = new CollectionAdapter(this, mCollections);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showProgress() {
        mDialog = new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setTitle("创建集合");
        mDialog.setMessage("Loading...");
        mDialog.show();
    }

    private void hideProgress() {
        mDialog.dismiss();
    }

    private class YItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = 0;
            outRect.top = SystemUtils.dp2px(CollectionActivity.this, 9.9F);
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 0;
            }
        }
    }
}
