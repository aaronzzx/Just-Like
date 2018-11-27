package com.aaron.justlike.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.aaron.justlike.extend.MyGridLayoutManager;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

public class SystemUtils {

    private static int mNum = 0; // getOrderNum()

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

    public static int getRandomNum(int num) {
        Random random = new Random();
        return random.nextInt(num);
    }

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
