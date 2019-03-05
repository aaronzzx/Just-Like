package com.aaron.justlike.app.collection.view;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.aaron.justlike.R;
import com.aaron.justlike.app.GridFragment;
import com.aaron.justlike.app.collection.presenter.ElementPresenter;
import com.aaron.justlike.app.collection.presenter.IElementPresenter;
import com.aaron.justlike.app.main.entity.Image;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ElementActivity extends AppCompatActivity implements View.OnClickListener,
        GridFragment.Callback, IElementView<Image> {

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar:

                break;
        }
    }

    @Override
    public void onDelete(String path) {

    }

    @Override
    public void onShowImage(List<Image> list) {
        mImageList.clear();
        mImageList.addAll(list);
        mGridFragment.update(list);
    }

    private void initView() {
        // get theme name
        mTitle = getIntent().getStringExtra("title");
        Log.d("ElementActivity", "mTitle: " + mTitle);

        // find id
        mToolbar = findViewById(R.id.toolbar);
        mGridFragment = (GridFragment) getSupportFragmentManager().findFragmentById(R.id.grid_fragment);

        // set listener
        mToolbar.setOnClickListener(this);

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
