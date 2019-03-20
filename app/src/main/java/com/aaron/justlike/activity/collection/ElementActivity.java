package com.aaron.justlike.activity.collection;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.aaron.justlike.R;
import com.aaron.justlike.common.ThemeManager;
import com.aaron.justlike.entity.Image;
import com.aaron.justlike.entity.UpdateEvent;
import com.aaron.justlike.fragment.GridFragment;
import com.aaron.justlike.mvp.presenter.collection.element.ElementPresenter;
import com.aaron.justlike.mvp.presenter.collection.element.IElementPresenter;
import com.aaron.justlike.mvp.view.collection.IElementView;
import com.aaron.justlike.ui.image_selector.ImageSelector;
import com.aaron.justlike.util.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

public class ElementActivity extends AppCompatActivity implements GridFragment.Callback,
        IElementView<Image> {

    private IElementPresenter<Image> mPresenter;

    private GridFragment mGridFragment;
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
        setContentView(R.layout.activity_element);
        initView();
        mPresenter = new ElementPresenter();
        mPresenter.attachView(this);
        mPresenter.requestImage(mTitle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        EventBus.getDefault().post(new UpdateEvent());
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
            if (theme != null && theme == ThemeManager.Theme.WHITE) {
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
        if (ThemeManager.getInstance().getCurrentTheme() != null
                && ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            menu.findItem(R.id.add_element).setIcon(mIconAdd);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.add_element:
                ArrayList<String> selectedList = new ArrayList<>();
                for (Image image : mImageList) {
                    selectedList.add(image.getPath());
                }
                ImageSelector.getInstance(getApplicationContext())
                        .setFilePath("/storage/emulated/0/Pictures/JustLike")
                        .setSelectedImage(selectedList)
                        .setCallback(new ImageSelector.ImageCallback() {
                            @Override
                            public void onResponse(List<String> response) {
                                mPresenter.saveImage(mTitle, mImageList.size(), response);
                            }
                        })
                        .start();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onDelet(String path) {
        mPresenter.deleteImage(mTitle, path);
    }

    @Override
    public void onShowImage(List<Image> list) {
        mImageList.clear();
        mImageList.addAll(list);
        mGridFragment.updateForAdd(list);
    }

    @Override
    public void onShowAddImage(List<Image> list) {
        mImageList.addAll(list);
        FileUtils.sortByDate(list, false);
        runOnUiThread(() -> mGridFragment.updateForAdd(list));
    }

    private void initView() {
        // get title
        mTitle = getIntent().getStringExtra("title");

        // find id
        mToolbar = findViewById(R.id.toolbar);
        mGridFragment = (GridFragment) getSupportFragmentManager().findFragmentById(R.id.grid_fragment);

        // init status
        initIconColor();
        initToolbar();
    }

    private void initIconColor() {
        if (ThemeManager.getInstance().getCurrentTheme() != null
                && ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            mIconBack = getResources().getDrawable(R.drawable.ic_back);
            mIconAdd = getResources().getDrawable(R.drawable.ic_add);
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorGreyText));
            DrawableCompat.setTint(mIconAdd, getResources().getColor(R.color.colorGreyText));
        } else {
            mIconAdd = getResources().getDrawable(R.drawable.ic_add);
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
        }
    }
}
