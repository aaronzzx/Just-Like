package com.aaron.justlike.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.aaron.justlike.extend.MyGridLayoutManager;

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
