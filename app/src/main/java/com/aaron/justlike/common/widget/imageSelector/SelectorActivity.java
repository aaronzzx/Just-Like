package com.aaron.justlike.common.widget.imageSelector;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.justlike.R;
import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.common.manager.ThemeManager;
import com.aaron.justlike.common.util.FileUtil;
import com.aaron.justlike.common.util.SystemUtil;
import com.aaron.ui.widget.TopBar;

import java.util.ArrayList;
import java.util.List;

public class SelectorActivity extends AppCompatActivity implements SelectorAdapter.Callback {

    private static final String[] TYPE = {"jpg", "jpeg", "png", "gif"};

    private Worker mWorker;
    private ImageSelector mImageSelector;
    private ImageSelector.ImageCallback mCallback;

//    private Toolbar mToolbar;
    private TopBar mTopBar;
    private ActionBar mActionBar;
    private RecyclerView mRecyclerView;
    private View mEmptyView;

    private Drawable mIconBack;
    private Drawable mIconDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        mImageSelector = ImageSelector.getInstance(this);
        mCallback = mImageSelector.getCallback();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageSelector.getInstance(this).clear();
        mCallback = null;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = getWindow();
        View decorView = window.getDecorView();
        if (hasFocus) {
            ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
            mTopBar.setTextColor(getResources().getColor(R.color.base_white));
            if (theme == null || theme == ThemeManager.Theme.WHITE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    window.setStatusBarColor(getResources().getColor(R.color.status_bar_background));
                }
//                mToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccentWhite));
                mTopBar.setTextColor(getResources().getColor(R.color.colorAccentWhite));
                mActionBar.setHomeAsUpIndicator(mIconBack);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_selector_menu, menu);
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            menu.findItem(R.id.done).setIcon(mIconDone);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.done:
                if (mWorker.response.size() == 0) {
                    Toast.makeText(this, "请先选择图片", Toast.LENGTH_SHORT).show();
                    break;
                }
                mCallback.onResponse(mWorker.response);
                mCallback.onResponse(mWorker.response, mWorker.title);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPress(List<String> response) {
//        mWorker.response.add(path);
        mWorker.response.clear();
        mWorker.response.addAll(response);
//        mToolbar.setTitle("已选择（" + mWorker.response.size() + "）");
        mTopBar.setText("已选择（" + mWorker.response.size() + "）");
    }

    private void initView() {
        // init worker
        mWorker = new Worker();
        mWorker.setLoadPath(mImageSelector.getFilePath());
        mWorker.setTitle(mImageSelector.getTitle());
        mWorker.setImageList();

        // find id
        mTopBar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recycler_view);
        mEmptyView = findViewById(R.id.empty_view);
        if (mWorker.getImageList().size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        initIconColor();
        initToolbar();
        initRecyclerView();
    }

    private void initIconColor() {
        mIconBack = getResources().getDrawable(R.drawable.ic_back);
        mIconDone = getResources().getDrawable(R.drawable.ic_check);
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorAccentWhite));
            DrawableCompat.setTint(mIconDone, getResources().getColor(R.color.colorAccentWhite));
        } else {
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorPrimaryWhite));
            DrawableCompat.setTint(mIconDone, getResources().getColor(R.color.colorPrimaryWhite));
        }
    }

    private void initToolbar() {
        Window window = getWindow();
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        mTopBar.setText("已选择（0）");
//        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mRecyclerView.addItemDecoration(new YItemDecoration());
        ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
        RecyclerView.Adapter adapter = new SelectorAdapter(mWorker.imageList, theme, this);
        ((SelectorAdapter) adapter).setSelectedImage(mImageSelector.getSelectedImage());
        mRecyclerView.setAdapter(adapter);
    }

    private static class Worker {

        private String loadPath;
        private String title;

        private List<Image> imageList = new ArrayList<>();
        private List<String> response = new ArrayList<>();

        private Worker() {

        }

        private void setTitle(String title) {
            this.title = title;
        }

        private void setLoadPath(String path) {
            loadPath = path;
        }

        private void setImageList() {
            FileUtil.getLocalFiles(imageList, loadPath, TYPE);
        }

        private List<Image> getImageList() {
            return imageList;
        }
    }

    private class XItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 3 == 0) {
                outRect.left = 0;
                outRect.right = SystemUtil.dp2px(SelectorActivity.this, 2.8F); // 8px
            } else if (parent.getChildAdapterPosition(view) % 3 == 1) {
                outRect.left = SystemUtil.dp2px(SelectorActivity.this, 1.3F); // 4px
                outRect.right = SystemUtil.dp2px(SelectorActivity.this, 1.3F); // 4px
            } else if (parent.getChildAdapterPosition(view) % 3 == 2) {
                outRect.left = SystemUtil.dp2px(SelectorActivity.this, 2.8F); // 8px
                outRect.right = 0;
            }
        }
    }

    private class YItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.bottom = 0;
            outRect.top = SystemUtil.dp2px(SelectorActivity.this, 4.2F); // 12px
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 0;
            } else if (parent.getChildAdapterPosition(view) == 1) {
                outRect.top = 0;
            } else if (parent.getChildAdapterPosition(view) == 2) {
                outRect.top = 0;
            }
        }
    }
}
