package com.aaron.justlike.app.collection.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.aaron.justlike.R;
import com.aaron.justlike.app.GridFragment;
import com.aaron.justlike.app.collection.adapter.ImageSelector;
import com.aaron.justlike.app.collection.entity.Collection;
import com.aaron.justlike.app.collection.entity.Element;
import com.aaron.justlike.app.collection.entity.UpdateEvent;
import com.aaron.justlike.app.collection.presenter.ElementPresenter;
import com.aaron.justlike.app.collection.presenter.IElementPresenter;
import com.aaron.justlike.app.main.entity.Image;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ElementActivity extends AppCompatActivity implements GridFragment.Callback,
        IElementView<Image>, ImageSelector.ImageCallback {

    private IElementPresenter<Image> mPresenter;
    private ExecutorService mService;

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
//                Intent intent = new Intent(this, SelectActivity.class);
//                intent.putStringArrayListExtra("selectedList", selectedList);
//                startActivity(intent);
                ImageSelector.getInstance(this)
                        .setFilePath("/storage/emulated/0/Pictures/JustLike")
                        .setSelectedImage(selectedList)
                        .setCallback(this)
                        .start();
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

    public void showAddImage(List<Image> list) {
        mImageList.addAll(list);
        mGridFragment.update(list);
    }

    @Override
    public void onResponse(List<String> response) {
        List<Image> imageList = new ArrayList<>();
        mService.execute(() -> {
            for (String path : response) {
                Element element = new Element();
                element.setTitle(mTitle);
                element.setPath(path);
                element.setCreateAt(System.currentTimeMillis());
                element.save();

                Collection info = new Collection();
                info.setTitle(mTitle);
                info.setTotal(mImageList.size());
                info.setPath(mImageList.get(mImageList.size() - 1).getPath());
                info.setCreateAt(System.currentTimeMillis());
                info.save();

                Image image = new Image(path);
                imageList.add(image);
                mImageList.add(image);
            }
            runOnUiThread(() -> showAddImage(imageList));
        });
    }

    private void initView() {
        // open thread pool
        mService = Executors.newSingleThreadExecutor();

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
