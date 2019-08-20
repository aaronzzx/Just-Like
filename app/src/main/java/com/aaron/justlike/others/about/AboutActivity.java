package com.aaron.justlike.others.about;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.justlike.R;
import com.aaron.justlike.common.CommonActivity;
import com.aaron.justlike.common.bean.Library;
import com.aaron.justlike.common.bean.Message;
import com.aaron.justlike.common.manager.ThemeManager;
import com.aaron.justlike.common.util.SystemUtil;
import com.aaron.ui.widget.TopBar;

import java.util.List;

public class AboutActivity extends CommonActivity implements IAboutContract.V<Message, Library> {

    private IAboutContract.P mPresenter;

    private Drawable mIconBack;

//    private Toolbar mToolbar;
    private TopBar mTopBar;
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

    /**
     * 标题栏返回键销毁活动
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out);
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.activity_slide_out);
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
        mTopBar = findViewById(R.id.activity_about_toolbar);
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
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorAccentWhite));
        } else {
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorPrimaryWhite));
        }
    }

    private void initToolbar() {
        Window window = getWindow();
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        setSupportActionBar(mToolbar);
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
