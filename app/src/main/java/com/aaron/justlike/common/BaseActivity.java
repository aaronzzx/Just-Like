package com.aaron.justlike.common;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.aaron.justlike.common.util.ActivityCollector;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        Log.d(TAG, "onCreate(): " + this.getLocalClassName());
        Log.d(TAG, "sActivityList.size(): " + ActivityCollector.getActivityTotal());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        Log.d(TAG, "onDestroy: " + this.getLocalClassName());
        Log.d(TAG, "top_activity: " + ActivityCollector.getTopActivity());
        Log.d(TAG, "bottom_activity: " + ActivityCollector.getBottomActivity());
        Log.d(TAG, "sActivityList.size(): " + ActivityCollector.getActivityTotal());
    }
}
