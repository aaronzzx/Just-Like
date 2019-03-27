package com.aaron.justlike.activity.collection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
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

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.collection.CollectionAdapter;
import com.aaron.justlike.common.ThemeManager;
import com.aaron.justlike.entity.Album;
import com.aaron.justlike.entity.Collection;
import com.aaron.justlike.entity.SelectEvent;
import com.aaron.justlike.entity.UpdateEvent;
import com.aaron.justlike.mvp.presenter.collection.CollectionPresenter;
import com.aaron.justlike.mvp.presenter.collection.ICollectionPresenter;
import com.aaron.justlike.mvp.view.collection.ICollectionView;
import com.aaron.justlike.ui.MyGridLayoutManager;
import com.aaron.justlike.ui.image_selector.ImageSelector;
import com.aaron.justlike.util.EmptyViewUtil;
import com.aaron.justlike.util.SystemUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

public class CollectionActivity extends AppCompatActivity implements CollectionAdapter.OnPressCallback,
        ICollectionView {

    private ICollectionPresenter mPresenter;

    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private Drawable mIconBack;
    private Drawable mIconAdd;
    private ProgressDialog mDialog;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private MyGridLayoutManager mLayoutManager;
    private CollectionAdapter mAdapter;

    private List<Album> mCollections = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
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
        ImageSelector.getInstance()
                .setCallback(null);
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
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.add_collection: // 添加集合
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_collection, null);
                EditText editText = dialogView.findViewById(R.id.input_collection_name);
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setTitle("创建集合")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 打开图片选择器让用户选择图片添加到集合
                            String title = editText.getText().toString();
                            ImageSelector.getInstance(getApplicationContext())
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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onLongPress(int position) {
        new AlertDialog.Builder(this)
                .setTitle("删除集合")
                .setMessage("请慎重！集合的所有信息将被清除")
                .setPositiveButton("确定", (dialog, which) -> {
                    Log.d("CollectionActivity", "which: " + which);
                    String title = mCollections.get(position).getCollectionTitle();
                    mPresenter.deleteCollection(title);
                    mCollections.remove(position);
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
        mToolbar = findViewById(R.id.activity_collection_toolbar);
        mRecyclerView = findViewById(R.id.recycler_view);
        mEmptyView = findViewById(R.id.empty_view);

        mDialog = new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setTitle("创建集合");
        mDialog.setMessage("Loading...");

        initIconColor();
        initToolbar();
        initRecyclerView();
    }

    private void initIconColor() {
        mIconBack = getResources().getDrawable(R.drawable.ic_back);
        mIconAdd = getResources().getDrawable(R.drawable.ic_add);
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorGreyText));
            DrawableCompat.setTint(mIconAdd, getResources().getColor(R.color.colorGreyText));
        } else {
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorPrimaryWhite));
            DrawableCompat.setTint(mIconAdd, getResources().getColor(R.color.colorPrimaryWhite));
        }
    }

    private void initToolbar() {
        Window window = getWindow();
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);
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
        mDialog.show();
    }

    private void hideProgress() {
        mDialog.dismiss();
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
