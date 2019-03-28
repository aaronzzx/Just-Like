package com.aaron.justlike.activity;

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
}
