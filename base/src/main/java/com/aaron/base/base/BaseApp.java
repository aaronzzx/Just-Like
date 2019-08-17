package com.aaron.base.base;

import android.app.Application;
import android.content.Context;

/**
 * Application 的父类。
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
public abstract class BaseApp extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        initModuleApp(this);
        initModuleData(this);
    }

    public static Context getContext() {
        return sContext;
    }

    public abstract void initModuleApp(Application app);

    public abstract void initModuleData(Application app);
}
