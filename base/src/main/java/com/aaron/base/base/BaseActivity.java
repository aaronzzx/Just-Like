package com.aaron.base.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

/**
 * 所有业务 Activity 的基类，理论上应该都继承于它，统一管理。
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
public abstract class BaseActivity extends AppCompatActivity implements IBaseActivity {

    static {
        // 设置启用 5.0 以下版本对矢量图形的支持
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private boolean mForbidScaleTextSize;

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        // 这里作用是决定能否随着系统字体大小变化而变化
        if (mForbidScaleTextSize && newConfig.fontScale != 1) {
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        // 这里作用是决定能否随着系统字体大小变化而变化
        if (mForbidScaleTextSize) {
            Resources res = super.getResources();
            if (res.getConfiguration().fontScale != 1) {
                Configuration newConfig = new Configuration();
                newConfig.setToDefaults();
                res.updateConfiguration(newConfig, res.getDisplayMetrics());
            }
            return res;
        }
        return super.getResources();
    }

    @Override
    public void forbidScaleTextSize(boolean yes) {
        mForbidScaleTextSize = yes;
    }
}
