package com.aaron.justlike.extend;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

public class BaseApplication extends Application {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        LitePal.initialize(sContext);
    }
}
