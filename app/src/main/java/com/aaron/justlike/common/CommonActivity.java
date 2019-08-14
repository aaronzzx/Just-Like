package com.aaron.justlike.common;

import android.content.Intent;
import android.os.Bundle;

import com.aaron.base.base.ActivityCollector;
import com.aaron.base.base.BaseActivity;
import com.aaron.justlike.R;

public abstract class CommonActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);

        forbidScaleTextSize(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_slide_in, android.R.anim.fade_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.activity_slide_out);
    }
}
