package com.aaron.base.impl;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;

/**
 * 防止点击抖动打开多个页面
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
public abstract class OnClickListenerImpl implements View.OnClickListener {

    private static final String TAG = "OnClickListenerImpl";

    private long mLastClickTime = 0;
    private long mClickInterval = 1000L;

    public OnClickListenerImpl() {

    }

    public OnClickListenerImpl(long clickInterval) {
        mClickInterval = clickInterval;
    }

    @Override
    public void onClick(View view) {
        long nowClickTime = SystemClock.elapsedRealtime();
        long interval = nowClickTime - mLastClickTime;
        if (mLastClickTime == 0 || interval > mClickInterval) {
            onViewClick(view, interval);
            mLastClickTime = nowClickTime;
        } else {
            Log.e(TAG, "点击间隔不得小于" + mClickInterval + "毫秒，否则不予响应。\n当前间隔: " + interval);
        }
    }

    public void setClickInterval(long milliseconds) {
        mClickInterval = milliseconds;
    }

    public long getClickInterval() {
        return mClickInterval;
    }

    public abstract void onViewClick(View v, long interval);
}
