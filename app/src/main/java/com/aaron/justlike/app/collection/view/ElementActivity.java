package com.aaron.justlike.app.collection.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.aaron.justlike.R;
import com.aaron.justlike.app.GridFragment;
import com.aaron.justlike.app.collection.entity.UpdateEvent;
import com.aaron.justlike.app.collection.presenter.ElementPresenter;
import com.aaron.justlike.app.collection.presenter.IElementPresenter;
import com.aaron.justlike.app.main.entity.Image;
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

    private List<Image> mImageList = new ArrayList<>();
    private GridFragment mGridFragment;
    private Toolbar mToolbar;

    private String mTitle;

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
                Intent intent = new Intent(this, SelectActivity.class);
                intent.putStringArrayListExtra("selectedList", selectedList);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDelete(String path) {
        mPresenter.deleteImage(mTitle, path);
    }

    @Override
    public void onShowImage(List<Image> list) {
        mImageList.clear();
        mImageList.addAll(list);
        mGridFragment.update(list);
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
