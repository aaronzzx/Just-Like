package com.aaron.justlike.app.collection.view;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.aaron.justlike.R;
import com.aaron.justlike.app.GridFragment;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ThemeActivity extends AppCompatActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, ICollectionView {

    private SwipeRefreshLayout mSwipeRefresh;
    private GridFragment mGridFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar:

                break;
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public <E> void onShowImage(List<E> list) {

    }

    private void initView() {
        // find id
        LinearLayout parentToolbar = findViewById(R.id.linear_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mSwipeRefresh = findViewById(R.id.swipe_refresh);
        mGridFragment = (GridFragment) getSupportFragmentManager().findFragmentById(R.id.grid_fragment);

        // set listener
        toolbar.setOnClickListener(this);
        mSwipeRefresh.setOnRefreshListener(this);

        // init status
        enableHomeAsUp();
        setStatusBar();
    }

    private void enableHomeAsUp() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setStatusBar() {
        StatusBarUtil.setTranslucent(this, 70);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
