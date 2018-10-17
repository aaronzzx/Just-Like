package com.aaron.justlike;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class FileUtils {

    private static Activity mActivity;
    private static List<Image> mImageList;
    private static ImageAdapter mImageAdapter;
    private static List<Uri> mUriList = new ArrayList<>(); // ViewPager 需要用到的集合

    /**
     * 获取图片的旋转角度
     *
     * @param path 图片的路径
     * @return 返回图片的旋转角度
     */
    static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 此类专门用于获取图片资源的信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的方向
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 获取指定目录指定类型的文件，并将其转换成 JSON 数组
     *
     * @param dirPath 目录路径
     * @param type    文件类型
     * @return 返回 JSON 数组
     */
    private static JSONArray getAllFiles(String dirPath, String type) {
        File file = new File(dirPath);
        if (!file.exists()) return null;
        File[] files = file.listFiles();
        if (files == null) return null;
        JSONArray fileList = new JSONArray();
        // 遍历数组内的文件
        for (File aFile : files) {
            // 如果是文件且后缀名为 type 就执行以下操作
            if (aFile.isFile() && aFile.getName().endsWith(type)) {
                /*
                 * 获取文件的绝对路径，并存入 JSON 数组
                 */
                String filePath = aFile.getAbsolutePath();
                JSONObject fileInfo = new JSONObject();
                try {
                    fileInfo.put("path", filePath);
                    fileList.put(fileInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 如果是目录则重复调用此方法直到找到文件
            } else if (aFile.isDirectory()) {
                getAllFiles(aFile.getAbsolutePath(), type);
            }
        }
        return fileList; // 返回 JSON 数组
    }

    /**
     * 获取从用户点击图片资源后返回的 URI ，并直接将文件缓存到应用缓存目录下
     *
     * @param context 上下文
     * @param uri     相册或文件管理器返回的 URI 数据
     */
    static void saveToCache(Context context, Uri uri) {
        String filePath = getPath(context, uri);
        String fileName = filePath.substring(filePath.lastIndexOf("/"));
        File file = new File(context.getExternalCacheDir().getAbsolutePath() + fileName);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (!file.exists()) {
                fis = new FileInputStream(filePath);
                fos = new FileOutputStream(context.getExternalCacheDir() + fileName);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer, 0, buffer.length)) != -1) {
                    fos.write(buffer, 0, len);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
                if (fis != null) fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     */
    static void deleteFile(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) return;
        File file = new File(context.getExternalCacheDir() + fileName);
        if (file.exists()) file.delete();
    }

    /**
     * 通过返回的 URI 来获取文件的真实路径
     *
     * @param context 上下文
     * @param uri     相册或文件管理器返回的 URI
     * @return 返回图片的真实路径
     */
    private static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver()
                    .query(uri, projection, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    return cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                }
                cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * 通过原始路径，获取绝对路径
     *
     * @param path 原始路径
     * @return 返回绝对路径
     */
    static String getAbsolutePath(String path) {
        String absolutePath;
        if (path.startsWith("/e")) {
            absolutePath = path.replace("/external_files",
                    "/storage/emulated/0");
        } else {
            absolutePath = path.substring(path.indexOf("/s"));
        }
        return absolutePath;
    }

    /**
     * 加载保存在应用缓存目录的文件
     */
    static void getLocalCache(Activity activity, List<Image> imageList,
                              ImageAdapter imageAdapter) {
        mActivity = activity;
        mImageList = imageList;
        mImageAdapter = imageAdapter;
        // 从缓存中读取的数据被放置在 JSON 数组中
        JSONArray jpgArray = FileUtils
                .getAllFiles(activity.getExternalCacheDir().getAbsolutePath(), "jpg");
        JSONArray jpg2Array = FileUtils
                .getAllFiles(activity.getExternalCacheDir().getAbsolutePath(), "JPG");
        JSONArray jpegArray = FileUtils
                .getAllFiles(activity.getExternalCacheDir().getAbsolutePath(), "jpeg");
        JSONArray jpeg2Array = FileUtils
                .getAllFiles(activity.getExternalCacheDir().getAbsolutePath(), "JPEG");
        JSONArray pngArray = FileUtils
                .getAllFiles(activity.getExternalCacheDir().getAbsolutePath(), "png");
        JSONArray png2Array = FileUtils
                .getAllFiles(activity.getExternalCacheDir().getAbsolutePath(), "PNG");
        try {/*
         * 遍历 JSON 数组，从中取出文件的路径，并转换为 URI 传入
         * Image 构造方法，将 Image 对象传入集合并通知适配器更新，
         * 从而达到加载缓存的目的。
         */
            if (jpgArray != null) noChoice(jpgArray);
            if (jpg2Array != null) noChoice(jpg2Array);
            if (jpegArray != null) noChoice(jpegArray);
            if (jpeg2Array != null) noChoice(jpeg2Array);
            if (pngArray != null) noChoice(pngArray);
            if (png2Array != null) noChoice(png2Array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 为了改良 getLocalCache() 方法循环体内的代码重复，目前没有更好的办法。
     */
    private static void noChoice(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String path = jsonObject.getString("path");
            String fileProvider = "com.aaron.justlike.fileprovider";
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(mActivity, fileProvider, new File(path));
            } else {
                uri = Uri.fromFile(new File(path));
            }

            mUriList.add(uri);
            /*
             * 之所以不能放在循环体外面，是因为除了 jpg 格式要加载，
             * 还有其他格式图片，在如果放在循环体外，因为加载其他格式
             * 图片与 jpg 格式不符合，所以会清空集合，导致 ViewPager
             * 无法显示。
             */
            MainActivity.setPhotoViewList(mUriList);

            mImageList.add(new Image(uri));
            mImageAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 格式化时间
     *
     * @param param 字符串时间
     * @throws ParseException
     */
    static void formatDateAndTime(ActionBar actionBar, String param) throws ParseException {
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
}
