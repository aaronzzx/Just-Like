package com.aaron.justlike.custom;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;

import org.litepal.LitePal;

public class BaseApplication extends Application {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        sContext = getApplicationContext();
        LeakCanary.install(this);
        LitePal.initialize(sContext);
    }
}
