package com.aaron.justlike.library.aria;

import android.content.Context;

import com.arialyy.aria.core.Aria;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AriaApp {

    private static AriaApp sAriaApp;
    private LinkedHashMap<String, Integer> mModeContainer = new LinkedHashMap<>();

    private AriaApp() {

    }

    public static AriaApp getInstance() {
        if (sAriaApp == null) {
            synchronized (AriaApp.class) {
                if (sAriaApp == null) {
                    sAriaApp = new AriaApp();
                }
            }
        }
        return sAriaApp;
    }

    public Map.Entry<String, Integer> getMode() {
        String path = null;
        int mode = 0;
        for (Map.Entry<String, Integer> entry : mModeContainer.entrySet()) {
            path = entry.getKey();
            mode = entry.getValue();
        }
        if (path != null) {
            HashMap<String, Integer> map = new HashMap<>();
            map.put(path, mode);
            return map.entrySet().iterator().next();
        }
        return null;
    }

    public void startDownload(Context context, String urls, String savePath, int mode) {
        Aria.download(context)
                .load(urls)
                .setFilePath(savePath + ".TEMP")
                .start();
        mModeContainer.put(savePath, mode);
    }
}
