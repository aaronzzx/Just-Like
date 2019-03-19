package com.aaron.justlike.activity.collection;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.aaron.justlike.R;
import com.aaron.justlike.entity.Image;
import com.aaron.justlike.entity.UpdateEvent;
import com.aaron.justlike.fragment.GridFragment;
import com.aaron.justlike.mvp.presenter.collection.element.ElementPresenter;
import com.aaron.justlike.mvp.presenter.collection.element.IElementPresenter;
import com.aaron.justlike.mvp.view.collection.IElementView;
import com.aaron.justlike.ui.image_selector.ImageSelector;
import com.aaron.justlike.util.FileUtils;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ElementActivity extends AppCompatActivity implements GridFragment.Callback,
        IElementView<Image> {

    private IElementPresenter<Image> mPresenter;

    private GridFragment mGridFragment;
    private Toolbar mToolbar;

    private String mTitle;
    private List<Image> mImageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_element_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.add:
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
        initToolbar();
        setStatusBar();
    }

    private void initToolbar() {
        mToolbar.setTitle(mTitle);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 70);
    }
}
