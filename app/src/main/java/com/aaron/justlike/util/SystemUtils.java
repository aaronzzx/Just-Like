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
import androidx.recyclerview.widget.RecyclerView;

public class SystemUtils {

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

    public static int getRandomNum() {
        Random random = new Random();
        return random.nextInt(9);
    }

    /**
     * 格式化时间
     *
     * @param param 字符串时间
     * @throws ParseException
     */
    public static void formatDateAndTime(ActionBar actionBar, String param) throws ParseException {
        if (param != null) {
            String dateArray[] = param.split(" ");
            String part1 = "yyyy:MM:dd";
            String part2 = "yyyy年MM月dd日";
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat(part1);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat(part2);
            Date d = sdf1.parse(dateArray[0]);
            String date = sdf2.format(d);
            String time = dateArray[1].substring(0, 5);
            actionBar.setTitle(date);
            actionBar.setSubtitle(time);
        }
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
