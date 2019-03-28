package com.aaron.justlike.activity.about;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.BaseActivity;
import com.aaron.justlike.adapter.about.AboutLibraryAdapter;
import com.aaron.justlike.adapter.about.AboutMessageAdapter;
import com.aaron.justlike.common.ThemeManager;
import com.aaron.justlike.entity.Library;
import com.aaron.justlike.entity.Message;
import com.aaron.justlike.mvp.presenter.about.AboutPresenter;
import com.aaron.justlike.mvp.presenter.about.IAboutPresenter;
import com.aaron.justlike.mvp.view.about.IAboutView;
import com.aaron.justlike.util.SystemUtil;

import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AboutActivity extends BaseActivity implements IAboutView<Message, Library> {

    private IAboutPresenter mPresenter;

    private Drawable mIconBack;

    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private RecyclerView mRecycleMessage;
    private RecyclerView mRecycleLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
        attachPresenter();
        mPresenter.requestMessage(AboutPresenter.Element.ICON_ID, AboutPresenter.Element.TITLE);
        mPresenter.requestLibrary(AboutPresenter.Element.LIBRARY_NAME,
                AboutPresenter.Element.LIBRARY_AUTHOR,
                AboutPresenter.Element.LIBRARY_INTRODUCE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = getWindow();
        View decorView = window.getDecorView();
        if (hasFocus) {
            ThemeManager.Theme theme = ThemeManager.getInstance().getCurrentTheme();
            if (theme == null || theme == ThemeManager.Theme.WHITE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    window.setStatusBarColor(getResources().getColor(R.color.status_bar_background));
                }
                mToolbar.setTitleTextColor(getResources().getColor(R.color.colorGreyText));
                mActionBar.setHomeAsUpIndicator(mIconBack);
            }
        }
    }

    /**
     * 标题栏返回键销毁活动
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

        initIconColor();
        initToolbar();
        initVersionStatus();
    }

    private void initIconColor() {
        mIconBack = getResources().getDrawable(R.drawable.ic_back);
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorGreyText));
        } else {
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorGreyText));
        }
    }

    private void initToolbar() {
        Window window = getWindow();
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initVersionStatus() {
        TextView version = findViewById(R.id.activity_about_version);
        String versionName = SystemUtil.getVersionName(this);
        version.setText("Version " + versionName);
    }

    private <T> void initMessage(List<T> messageList) {
        LinearLayoutManager messageManager = new LinearLayoutManager(this);
        mRecycleMessage.setLayoutManager(messageManager);
        AboutMessageAdapter messageAdapter = new AboutMessageAdapter<>(this, messageList);
        mRecycleMessage.setAdapter(messageAdapter);
    }

    private <T> void initLibrary(List<T> libraryList) {
        LinearLayoutManager libraryManager = new LinearLayoutManager(this);
        mRecycleLibrary.setLayoutManager(libraryManager);
        AboutLibraryAdapter libraryAdapter = new AboutLibraryAdapter<>(this, libraryList);
        mRecycleLibrary.setAdapter(libraryAdapter);
    }
}
