package com.aaron.justlike.others.theme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.base.base.ActivityCollector;
import com.aaron.justlike.R;
import com.aaron.justlike.common.CommonActivity;
import com.aaron.justlike.common.manager.ThemeManager;
import com.aaron.ui.widget.TopBar;
import com.github.anzewei.parallaxbacklayout.ParallaxBack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ParallaxBack
public class ThemeActivity extends CommonActivity implements IThemeCommunicable {

    private static final String PREFERENCES_THEME = "justlike_theme";
    private static final String CURRENT_THEME = "current_theme";
    private static final String PREFERENCES_THEME_CHECK = "justlike_theme_check";
    private static final String CURRENT_CHECK = "current_check";

    private ExecutorService mExecutorService;
    private LinearLayoutManager mLayoutManager;

//    private Toolbar mToolbar;
    private TopBar mTopBar;
    private ActionBar mActionBar;
    private Drawable mIconBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        initView();
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
//            overridePendingTransition(0, R.anim.activity_slide_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
//        overridePendingTransition(0, R.anim.activity_slide_out);
    }

    @Override
    public void onTap(int newChecked, int oldChecked) {
        if (saveTheme(newChecked)) {
            // change theme check
            View view = mLayoutManager.findViewByPosition(oldChecked);
            if (view != null) {
                ViewGroup checkbox = view.findViewById(R.id.checkbox);
                checkbox.setVisibility(View.GONE);
            }
            // reboot MainActivity
            reboot();
            // save new check to local
            mExecutorService.execute(() -> {
                SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_THEME_CHECK, MODE_PRIVATE).edit();
                editor.putInt(CURRENT_CHECK, newChecked);
                editor.apply();
            });
        }
    }

    private void initView() {
        mExecutorService = Executors.newSingleThreadExecutor();

        mTopBar = findViewById(R.id.toolbar);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        initIconColor();
        initToolbar();
        initRecyclerView(recyclerView);
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

    private void initRecyclerView(RecyclerView recyclerView) {
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.theme_white);
        imageList.add(R.drawable.theme_black);
        imageList.add(R.drawable.theme_grey);
        imageList.add(R.drawable.theme_green);
        imageList.add(R.drawable.theme_red);
        imageList.add(R.drawable.theme_pink);
        imageList.add(R.drawable.theme_blue);
        imageList.add(R.drawable.theme_purple);
        imageList.add(R.drawable.theme_orange);
        imageList.add(R.drawable.theme_just_like);
        int currentCheck = getSharedPreferences(PREFERENCES_THEME_CHECK, MODE_PRIVATE).getInt(CURRENT_CHECK, 0);
        RecyclerView.Adapter adapter = new ThemeAdapter(imageList, currentCheck);
        recyclerView.setAdapter(adapter);
    }

    private boolean saveTheme(int position) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_THEME, MODE_PRIVATE).edit();
        switch (position) {
            case ThemeAdapter.WHITE:
                editor.putString(CURRENT_THEME, "WHITE");
                break;
            case ThemeAdapter.BLACK:
                editor.putString(CURRENT_THEME, "BLACK");
                break;
            case ThemeAdapter.GREY:
                editor.putString(CURRENT_THEME, "GREY");
                break;
            case ThemeAdapter.GREEN:
                editor.putString(CURRENT_THEME, "GREEN");
                break;
            case ThemeAdapter.RED:
                editor.putString(CURRENT_THEME, "RED");
                break;
            case ThemeAdapter.PINK:
                editor.putString(CURRENT_THEME, "PINK");
                break;
            case ThemeAdapter.BLUE:
                editor.putString(CURRENT_THEME, "BLUE");
                break;
            case ThemeAdapter.PURPLE:
                editor.putString(CURRENT_THEME, "PURPLE");
                break;
            case ThemeAdapter.ORANGE:
                editor.putString(CURRENT_THEME, "ORANGE");
                break;
            case ThemeAdapter.JUST_LIKE:
                editor.putString(CURRENT_THEME, "JUST_LIKE");
                break;
        }
        return editor.commit();
    }

    private void reboot() {
        Activity activity = ActivityCollector.getBottom();
        if (activity == null) return;
        Class clazz = activity.getClass();
        ActivityCollector.finishAll();
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
