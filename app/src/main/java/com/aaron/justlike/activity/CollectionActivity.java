package com.aaron.justlike.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.CollectionAdapter;

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
    private CollectionAdapter mAdapter;
    private List<String> mCollections = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        mDatabase = LitePal.getDatabase();
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
                            // TODO 编写创建集合的逻辑
                        })
                        .setNegativeButton("取消", (dialog, which) -> {

                        }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.activity_collection_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new CollectionAdapter(this, mCollections);
        mRecyclerView.setAdapter(mAdapter);
    }
}
