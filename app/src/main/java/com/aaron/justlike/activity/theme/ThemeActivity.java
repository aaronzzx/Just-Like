package com.aaron.justlike.activity.theme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.main.MainActivity;
import com.aaron.justlike.adapter.theme.ThemeAdapter;
import com.aaron.justlike.common.ThemeManager;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ThemeActivity extends AppCompatActivity implements ThemeAdapter.Callback {

    private static final String PREFERENCES_NAME = "justlike_theme";
    private static final String CURRENT_THEME = "current_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 70);
        initView();
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
    public void onPress(int position) {
        if (saveTheme(position)) {
            reboot();
        }
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        initToolbar(toolbar);
        initRecyclerView(recyclerView);
    }

    private void initToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<Drawable> imageList = new ArrayList<>();
        imageList.add(getResources().getDrawable(R.drawable._default));
        imageList.add(getResources().getDrawable(R.drawable.white));
        imageList.add(getResources().getDrawable(R.drawable.black));
        imageList.add(getResources().getDrawable(R.drawable.grey));
        imageList.add(getResources().getDrawable(R.drawable.green));
        imageList.add(getResources().getDrawable(R.drawable.red));
        imageList.add(getResources().getDrawable(R.drawable.pink));
        imageList.add(getResources().getDrawable(R.drawable.blue));
        imageList.add(getResources().getDrawable(R.drawable.purple));
        imageList.add(getResources().getDrawable(R.drawable.brown));
        RecyclerView.Adapter adapter = new ThemeAdapter(imageList, this);
        recyclerView.setAdapter(adapter);
    }

    private boolean saveTheme(int position) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit();
        switch (position) {
            case ThemeAdapter.DEFAULT:
                editor.putString(CURRENT_THEME, "DEFAULT");
                break;
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
            case ThemeAdapter.BROWN:
                editor.putString(CURRENT_THEME, "BROWN");
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
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
