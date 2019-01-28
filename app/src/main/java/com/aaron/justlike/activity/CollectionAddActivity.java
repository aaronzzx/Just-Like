package com.aaron.justlike.activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.CollectionAddAdapter;
import com.aaron.justlike.entity.CollectionInfo;
import com.aaron.justlike.entity.ISetToolbar;
import com.aaron.justlike.entity.Image;
import com.aaron.justlike.extend.MyGridLayoutManager;
import com.aaron.justlike.util.FileUtils;
import com.aaron.justlike.util.SystemUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

public class CollectionAddActivity extends AppCompatActivity {

    private static final String TITLE = "已选择";
    private static final String LOAD_PATH = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + "/JustLike";
    private static final String[] TYPE = {"jpg", "jpeg", "png", "gif"};
    private SQLiteDatabase mDatabase;
    private String mCollectionName;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private MyGridLayoutManager mLayoutManager;
    private CollectionAddAdapter mAddAdapter;
    private List<Image> mImages = new ArrayList<>();
    private List<String> mOnPaths;
    private ISetToolbar mListener = new ISetToolbar() {
        @Override
        public void onSetToolbar(List<String> paths) {
            mOnPaths = paths;
            mToolbar.setTitle(TITLE + "(" + paths.size() + ")");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_add);
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
                finish();
                break;
            case R.id.done:
                if (mOnPaths == null) {
                    Toast.makeText(this, "请先选择图片", Toast.LENGTH_SHORT).show();
                    break;
                }
                // 存储集合名称和集合里元素数量
                CollectionInfo info = new CollectionInfo();
                info.setTitle(mCollectionName);
                info.setTotal(mOnPaths.size());
                info.setPath(mOnPaths.get(0));
                info.save();

                // 存储单个集合里图片的路径
                ContentValues values = new ContentValues();
                for (String path : mOnPaths) {
                    values.put("title", mCollectionName);
                    values.put("path", path);
                    mDatabase.insert("Collection", null, values);
                    values.clear();
                }
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        // 获取用户输入的集合创建名
        mCollectionName = getIntent().getStringExtra("collectionName");
        mDatabase = LitePal.getDatabase();

        // 初始化界面元素
        mToolbar = findViewById(R.id.activity_collection_add_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 连接适配器
        FileUtils.getLocalFiles(mImages, LOAD_PATH, TYPE);
        mRecyclerView = findViewById(R.id.rv_home_activity_main);
        mLayoutManager = new MyGridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mRecyclerView.addItemDecoration(new YItemDecoration());
        mAddAdapter = new CollectionAddAdapter(this, mImages, mListener);
        mRecyclerView.setAdapter(mAddAdapter);
    }

    public class XItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 3 == 0) {
                outRect.left = 0;
                outRect.right = SystemUtils.dp2px(CollectionAddActivity.this, 2.8F); // 8px
            } else if (parent.getChildAdapterPosition(view) % 3 == 1) {
                outRect.left = SystemUtils.dp2px(CollectionAddActivity.this, 1.3F); // 4px
                outRect.right = SystemUtils.dp2px(CollectionAddActivity.this, 1.3F); // 4px
            } else if (parent.getChildAdapterPosition(view) % 3 == 2) {
                outRect.left = SystemUtils.dp2px(CollectionAddActivity.this, 2.8F); // 8px
                outRect.right = 0;
            }
        }
    }

    public class YItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = 0;
            outRect.top = SystemUtils.dp2px(CollectionAddActivity.this, 4.2F); // 12px
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
