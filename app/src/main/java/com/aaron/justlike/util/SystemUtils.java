package com.aaron.justlike.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class SystemUtils {

    /**
     * 利用反射开启 Toolbar 菜单图标
     */
    public static void setIconEnable(Menu menu, boolean enable) {
        try {
            @SuppressLint("PrivateApi")
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            m.invoke(menu, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断 View 是否可见
     */
    public static boolean isViewVisible(View view) {
        Rect rect = new Rect();
        view.getLocalVisibleRect(rect);
        return rect.top == 0;
    }

    /**
     * 获取 App 版本名
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String versionName = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int[] getResolution(WindowManager manager) {
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getRealMetrics(metrics);
        int[] resolutionArray = new int[2];
        resolutionArray[0] = metrics.widthPixels;
        resolutionArray[1] = metrics.heightPixels;
        return resolutionArray;
    }

    /**
     * 获取随机数
     */
    public static int getRandomNum(int num) {
        Random random = new Random();
        return random.nextInt(num) + 1;
    }

    /**
     * 获取图片生产日期
     */
    public static String getCreateDate(String path) {
        String date = null;
        try {
            ExifInterface exif = new ExifInterface(path);
            date = exif.getAttribute(ExifInterface.TAG_DATETIME);
            if (!TextUtils.isEmpty(date)) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = formatter.parse(date);
                date = formatter2.format(d);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static String getLastModified(String path, String pattern) {
        long time = new File(path).lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date d = new Date(time);
        return formatter.format(d);
    }

    public static String getCurrentDate(String pattern) {
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date d = new Date(time);
        return format.format(d);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getUnsplashDate(String date) {
        String finalDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
        try {
            Date d = sdf.parse(date.substring(0, 9));
            finalDate = sdf1.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return finalDate;
    }

    /**
     * dp 单位转 px
     *
     * @param context 上下文
     * @param values  dp 值
     * @return        px 值
     */
    public static int dp2px(Context context, float values) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (values * density + 0.5F);
    }
}
