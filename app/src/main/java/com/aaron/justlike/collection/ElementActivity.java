package com.aaron.justlike.collection;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.justlike.R;
import com.aaron.justlike.common.CommonActivity;
import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.common.event.DeleteEvent;
import com.aaron.justlike.common.event.UpdateEvent;
import com.aaron.justlike.common.impl.SquareItemDecoration;
import com.aaron.justlike.common.manager.ThemeManager;
import com.aaron.justlike.common.util.FileUtil;
import com.aaron.justlike.common.widget.MyGridLayoutManager;
import com.aaron.justlike.common.widget.imageSelector.ImageSelector;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ElementActivity extends CommonActivity implements IElementContract.V<Image>, IElementCommunicable {

    private IElementContract.P<Image> mPresenter;
    private RecyclerView.Adapter mAdapter;

    private RecyclerView mRv;
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private Drawable mIconBack;
    private Drawable mIconAdd;

    private String mTitle;
    private List<Image> mImageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_element);
        initView();
        mPresenter = new ElementPresenter();
        mPresenter.attachView(this);
        mPresenter.requestImage(mTitle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mPresenter.detachView();
        EventBus.getDefault().post(new UpdateEvent());
        ImageSelector.getInstance().clear();
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
        getMenuInflater().inflate(R.menu.activity_element_menu, menu);
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            menu.findItem(R.id.add_element).setIcon(mIconAdd);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_element) {
            ArrayList<String> selectedList = new ArrayList<>();
            for (Image image : mImageList) {
                selectedList.add(image.getPath());
            }
            ImageSelector.getInstance(ElementActivity.this)
                    .setFilePath("/storage/emulated/0/Pictures/JustLike")
                    .setSelectedImage(selectedList)
                    .setCallback(new ImageSelector.ImageCallback() {
                        @Override
                        public void onResponse(List<String> response) {
                            mPresenter.saveImage(mTitle, mImageList.size(), response);
                        }
                    })
                    .start();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out);
        return super.onSupportNavigateUp();
    }

    /**
     * 接收 PreviewActivity 传过来的关于被删除图片的信息，并更新 UI
     */
    @Subscribe()
    public void onDeleteEvent(DeleteEvent event) {
        if (event.getEventType() == DeleteEvent.FROM_ELEMENT_ACTIVITY) {
            int position = event.getPosition();
            String path = event.getPath();
            mImageList.remove(position);
            mAdapter.notifyDataSetChanged();
            mPresenter.deleteImage(mTitle, path);
        }
    }

    @Override
    public void onDelete(String path, boolean isEmpty) {
        mPresenter.deleteImage(mTitle, path);
        if (isEmpty) {
            onBackPressed();
        }
    }

    @Override
    public void onShowImage(List<Image> list) {
        mImageList.clear();
        mImageList.addAll(list);
        runOnUiThread(() -> {
            mAdapter.notifyDataSetChanged();
            mRv.scrollToPosition(0);
        });
    }

    @Override
    public void onShowAddImage(List<Image> list) {
        mImageList.addAll(list);
        FileUtil.sortByDate(list, false);
        runOnUiThread(() -> {
            mAdapter.notifyItemRangeInserted(0, list.size());
            mAdapter.notifyItemRangeChanged(list.size(), mImageList.size() - list.size());
            mRv.scrollToPosition(0);
        });
    }

    private void initView() {
        // get title
        mTitle = getIntent().getStringExtra("title");

        // find id
        mToolbar = findViewById(R.id.toolbar);
        mRv = findViewById(R.id.rv);

        ((DefaultItemAnimator) mRv.getItemAnimator()).setSupportsChangeAnimations(false);
        MyGridLayoutManager layoutManager = new MyGridLayoutManager(this, 3);
        mRv.setLayoutManager(layoutManager);
        mRv.addItemDecoration(new SquareItemDecoration.XItemDecoration());
        mRv.addItemDecoration(new SquareItemDecoration.YItemDecoration());
        mAdapter = new ElementAdapter(mImageList);
        mRv.setAdapter(mAdapter);
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        // init status
        initIconColor();
        initToolbar();
    }

    private void initIconColor() {
        mIconBack = getResources().getDrawable(R.drawable.ic_back);
        mIconAdd = getResources().getDrawable(R.drawable.ic_add);
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconAdd, getResources().getColor(R.color.colorAccentWhite));
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
        mToolbar.setTitle(mTitle);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }
}
