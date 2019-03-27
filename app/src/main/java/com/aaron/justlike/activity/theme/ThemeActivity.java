package com.aaron.justlike.activity.theme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.main.MainActivity;
import com.aaron.justlike.adapter.theme.ThemeAdapter;
import com.aaron.justlike.common.ThemeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ThemeActivity extends AppCompatActivity implements ThemeAdapter.Callback {

    private static final String PREFERENCES_THEME = "justlike_theme";
    private static final String CURRENT_THEME = "current_theme";
    private static final String PREFERENCES_THEME_CHECK = "justlike_theme_check";
    private static final String CURRENT_CHECK = "current_check";

    private ExecutorService mExecutorService;
    private LinearLayoutManager mLayoutManager;

    private Toolbar mToolbar;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onPress(int newCheck, int oldCheck) {
        if (saveTheme(newCheck)) {
            // change theme check
            View view = mLayoutManager.findViewByPosition(oldCheck);
            if (view != null) {
                ViewGroup checkbox = view.findViewById(R.id.checkbox);
                checkbox.setVisibility(View.GONE);
            }
            // reboot MainActivity
            reboot();
            // save new check to local
            mExecutorService.execute(() -> {
                SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_THEME_CHECK, MODE_PRIVATE).edit();
                editor.putInt(CURRENT_CHECK, newCheck);
                editor.apply();
            });
        }
    }

    private void initView() {
        mExecutorService = Executors.newSingleThreadExecutor();

        mToolbar = findViewById(R.id.toolbar);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        initIconColor();
        initToolbar();
        initRecyclerView(recyclerView);
    }

    private void initIconColor() {
        mIconBack = getResources().getDrawable(R.drawable.ic_back);
        if (ThemeManager.getInstance().getCurrentTheme() == null
                || ThemeManager.getInstance().getCurrentTheme() == ThemeManager.Theme.WHITE) {
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorGreyText));
        } else {
            DrawableCompat.setTint(mIconBack, getResources().getColor(R.color.colorPrimaryWhite));
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
        RecyclerView.Adapter adapter = new ThemeAdapter(imageList, this, currentCheck);
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
