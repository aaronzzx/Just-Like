package com.aaron.justlike.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.CollectionAdapter;
import com.aaron.justlike.another.Album;
import com.aaron.justlike.another.CollectionInfo;
import com.aaron.justlike.extend.MyGridLayoutManager;
import com.aaron.justlike.util.SystemUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

public class CollectionActivity extends AppCompatActivity {

    private SQLiteDatabase mDatabase;
    private RecyclerView mRecyclerView;
    private MyGridLayoutManager mLayoutManager;
    private CollectionAdapter mAdapter;
    private List<Album> mCollections = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        initDatabase();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_collection_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.add: // 添加集合
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_collection, null);
                EditText editText = dialogView.findViewById(R.id.input_collection_name);
                new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setTitle("创建集合")
                        .setPositiveButton("确定", (dialog, which) -> {
                            // 打开图片选择器让用户选择图片添加到集合
                            String collectionName = editText.getText().toString();
                            Intent intent = new Intent(this, CollectionAddActivity.class);
                            intent.putExtra("collectionName", collectionName);
                            startActivity(intent);
                        })
                        .setNegativeButton("取消", (dialog, which) -> {

                        }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        // 初始化界面 UI
        Toolbar toolbar = findViewById(R.id.activity_collection_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 初始化控件
        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new MyGridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new YItemDecoration());
        mAdapter = new CollectionAdapter(this, mCollections);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initDatabase() {
        mDatabase = LitePal.getDatabase();
        // 取出集合的名称和元素的数量
        List<CollectionInfo> collections = LitePal.findAll(CollectionInfo.class);
        Cursor cursor = null;
        for (CollectionInfo info : collections) {
            Album album = new Album();
            String title = info.getTitle();
            String total = String.valueOf(info.getTotal());
            album.setCollectionTitle(title);
            album.setElementTotal(total);
            // 取出集合具体元素
            cursor = mDatabase.query("Collection", new String[]{"path"}, "title = ?",
                    new String[]{title}, null, null, null);
            if (cursor.moveToFirst()) {
                String path = cursor.getString(cursor.getColumnIndex("path"));
                album.setImagePath(path);
            }
            mCollections.add(album);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public class YItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = 0;
            outRect.top = SystemUtils.dp2px(CollectionActivity.this, 9.9F);
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 0;
            }
        }
    }
}
