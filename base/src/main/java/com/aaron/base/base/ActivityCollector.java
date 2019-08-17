package com.aaron.base.base;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity 收集器，方便移除与实现一键退出，在 {@link BaseActivity} 中使用。
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
public final class ActivityCollector {

    private static List<Activity> sActivities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        sActivities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        sActivities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : sActivities) {
            if (!activity.isFinishing()) activity.finish();
        }
    }

    public static int getCount() {
        return sActivities.size();
    }

    public static Activity getTop() {
        return sActivities.isEmpty() ? null : sActivities.get(sActivities.size() - 1);
    }

    public static Activity getBottom() {
        return sActivities.isEmpty() ? null : sActivities.get(0);
    }

    private ActivityCollector() {

    }
}
