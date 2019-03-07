package com.aaron.justlike.custom.image_selector;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.module.main.entity.Image;
import com.aaron.justlike.util.FileUtils;
import com.aaron.justlike.util.SystemUtils;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SelectorActivity extends AppCompatActivity implements SelectorAdapter.Callback {

    private Worker mWorker;
    private ImageSelector mImageSelector;
    private ImageSelector.ImageCallback mCallback;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector);
        mImageSelector = ImageSelector.getInstance(this);
        mCallback = mImageSelector.getCallback();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_collection_add_menu, menu);
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
        mToolbar.setTitle("已选择（" + mWorker.response.size() + "）");
    }

    private void initView() {
        // init worker
        mWorker = new Worker();
        mWorker.setLoadPath(mImageSelector.getFilePath());
        mWorker.setTitle(mImageSelector.getTitle());
        mWorker.setImageList();

        // find id
        mToolbar = findViewById(R.id.toolbar);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        // init toolbar and status bar
        mToolbar.setTitle("已选择（0）");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 70);

        // init recyclerview
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new XItemDecoration());
        recyclerView.addItemDecoration(new YItemDecoration());
        RecyclerView.Adapter adapter = new SelectorAdapter(mWorker.imageList, this);
        ((SelectorAdapter) adapter).setSelectedImage(mImageSelector.getSelectedImage());
        recyclerView.setAdapter(adapter);
    }

    private static class Worker {

        private static final String[] TYPE = {"jpg", "jpeg", "png", "gif"};
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
            FileUtils.getLocalFiles(imageList, loadPath, TYPE);
        }
    }

    private class XItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 3 == 0) {
                outRect.left = 0;
                outRect.right = SystemUtils.dp2px(SelectorActivity.this, 2.8F); // 8px
            } else if (parent.getChildAdapterPosition(view) % 3 == 1) {
                outRect.left = SystemUtils.dp2px(SelectorActivity.this, 1.3F); // 4px
                outRect.right = SystemUtils.dp2px(SelectorActivity.this, 1.3F); // 4px
            } else if (parent.getChildAdapterPosition(view) % 3 == 2) {
                outRect.left = SystemUtils.dp2px(SelectorActivity.this, 2.8F); // 8px
                outRect.right = 0;
            }
        }
    }

    private class YItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.bottom = 0;
            outRect.top = SystemUtils.dp2px(SelectorActivity.this, 4.2F); // 12px
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
