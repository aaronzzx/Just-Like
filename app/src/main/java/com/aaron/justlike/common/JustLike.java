package com.aaron.justlike.common;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import org.litepal.LitePal;

public class JustLike extends Application {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();

        litepal();
        leakCanary();
    }

    private void litepal() {
        LitePal.initialize(this);
    }

    private void leakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
