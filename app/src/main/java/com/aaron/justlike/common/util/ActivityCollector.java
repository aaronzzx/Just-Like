package com.aaron.justlike.common.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {

    private static List<Activity> sActivityList = new ArrayList<>();

    public static void addActivity(Activity activity) {
        sActivityList.add(activity);
    }

    public static void removeActivity(Activity activity) {
        sActivityList.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : sActivityList) {
            activity.finish();
        }
        sActivityList.clear();
    }

    public static int getActivityTotal() {
        return sActivityList.size();
    }

    public static Activity getTopActivity() {
        return sActivityList.isEmpty() ? null : sActivityList.get(sActivityList.size() - 1);
    }

    public static Activity getBottomActivity() {
        return sActivityList.isEmpty() ? null : sActivityList.get(0);
    }
}
