package com.aaron.justlike.about.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.about.entity.Library;
import com.aaron.justlike.about.entity.Message;
import com.aaron.justlike.about.presenter.AboutPresenter;
import com.aaron.justlike.about.presenter.IAboutPresenter;
import com.aaron.justlike.adapter.AboutLibraryAdapter;
import com.aaron.justlike.adapter.AboutMessageAdapter;
import com.aaron.justlike.util.SystemUtils;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class About2Activity extends AppCompatActivity implements IAboutView<Message, Library> {

    private IAboutPresenter mPresenter;

    private Toolbar mToolbar;
    private RecyclerView mRecycleMessage;
    private RecyclerView mRecycleLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        StatusBarUtil.setTranslucent(this, 70);
        attachPresenter();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    /**
     * 标题栏返回键销毁活动
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void attachPresenter() {
        mPresenter = new AboutPresenter(this);
    }

    @Override
    public void onShowMessage(List<Message> list) {
        initMessage(list);
    }

    @Override
    public void onShowLibrary(List<Library> list) {
        initLibrary(list);
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        mToolbar = findViewById(R.id.activity_about_toolbar);
        mRecycleMessage = findViewById(R.id.activity_about_message_recycler);
        mRecycleLibrary = findViewById(R.id.activity_about_library_recycler);
        initToolbar();
        initVersionStatus();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initVersionStatus() {
        TextView version = findViewById(R.id.activity_about_version);
        String versionName = SystemUtils.getVersionName(this);
        version.setText("Version " + versionName);
    }

    private <T> void initMessage(List<T> messageList) {
        LinearLayoutManager messageManager = new LinearLayoutManager(this);
        mRecycleMessage.setLayoutManager(messageManager);
        AboutMessageAdapter messageAdapter = new AboutMessageAdapter(this, messageList);
        mRecycleMessage.setAdapter(messageAdapter);
    }

    private <T> void initLibrary(List<T> libraryList) {
        LinearLayoutManager libraryManager = new LinearLayoutManager(this);
        mRecycleLibrary.setLayoutManager(libraryManager);
        AboutLibraryAdapter libraryAdapter = new AboutLibraryAdapter(this, libraryList);
        mRecycleLibrary.setAdapter(libraryAdapter);
    }
}
