package com.aaron.justlike.app.collection.view;

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
import com.aaron.justlike.app.collection.entity.Album;
import com.aaron.justlike.app.collection.entity.Collection;
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
        ICollectionView {

    private ICollectionPresenter mPresenter;

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
                            String collectionName = editText.getText().toString();
                            Intent intent = new Intent(this, SelectActivity.class);
                            intent.putExtra("collectionName", collectionName);
                            startActivity(intent);
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
    public <E> void onShowImage(List<E> list) {
        mCollections.clear();
        for (E e : list) {
            mCollections.add((Album) e);
        }
//        mAdapter.notifyItemRangeChanged(0, mCollections.size());
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.rv_home_activity_main);

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
