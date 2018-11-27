package com.aaron.justlike.util;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.aaron.justlike.activity.MainActivity;
import com.aaron.justlike.another.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.core.content.FileProvider;

public class FileUtils {

    public static void sortByDate(List<Image> imageList, final boolean isOrder) {
        if (!imageList.isEmpty()) {
            Collections.sort(imageList, new Comparator<Image>() {
                @Override
                public int compare(Image o1, Image o2) {
                    if (isOrder) {
                        return o1.getCreateDate().compareTo(o2.getCreateDate());
                    } else {
                        return o2.getCreateDate().compareTo(o1.getCreateDate());
                    }
                }
            });
        }
    }

    public static void sortBySize(List<Image> imageList, final boolean isOrder) {
        if (!imageList.isEmpty()) {
            Collections.sort(imageList, new Comparator<Image>() {
                @Override
                public int compare(Image o1, Image o2) {
                    if (isOrder) {
                        return Long.compare(o1.getSize(), o2.getSize());
                    } else {
                        return Long.compare(o2.getSize(), o1.getSize());
                    }
                }
            });
        }
    }

    public static Uri getUriFromPath(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context,
                    "com.aaron.justlike.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static void setWallpaper(Context context, String path) {
        WallpaperManager manager = WallpaperManager.getInstance(context);
        if (manager != null && path != null) {
            File file = new File(path);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                manager.setStream(fis);
                Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取图片的旋转角度
     *
     * @param path 图片的路径
     * @return 返回图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
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

    public static String getImageName(String path) {
        return path.substring(path.lastIndexOf("/"));
    }

    /**
     * 获取图片生产时间
     *
     * @param path
     * @return
     */
    public static String getImageDate(String path) {
        String time;
        File file = new File(path);
        time = file.getName();
        return time;
    }

    public static long getImageSize(String path) {
        long size = 0;
        File file = new File(path);
        size = file.length();
        return size;
    }

    /**
     * 获取指定目录指定类型的文件，并将其转换成 JSON 数组
     *
     * @param dirPath 目录路径
     * @param type    文件类型
     * @return 返回 JSON 数组
     */
    public static JSONArray getAllFiles(String dirPath, String type) {
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
     * @param path     相册或文件管理器返回的路径
     */
    public static void saveToCache(Context context, String path, int num) {
        String originalDate;
        String createDate = SystemUtils.getCreateDate(path);
        if (!TextUtils.isEmpty(createDate)) {
            originalDate = createDate;
        } else {
            originalDate = SystemUtils.getLastModified(path, "yyyy-MM-dd HH:mm:ss");
        }
        String dirPath = Environment.getExternalStorageDirectory().getPath() + "/JustLike/images";
        File mkDir = new File(dirPath);
        if (!mkDir.exists()) mkDir.mkdirs();
        String date = SystemUtils.getCurrentDate("yyyyMMdd_HHmmss");
        String name = "/IMG_" + date + "（" + num + "）";
        String suffix = path.substring(path.lastIndexOf("."));
        final String fileName = name + suffix;
        String filePath = dirPath + fileName;
        File file = new File(filePath);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        try {
            if (!file.exists()) {
                fis = new FileInputStream(path);
                fos = new FileOutputStream(file);
                int orientation = getBitmapDegree(path);
                if (orientation != 0 & orientation != 1) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(getBitmapDegree(path));
                    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                }
                ExifInterface exif2 = new ExifInterface(filePath);
                exif2.setAttribute(ExifInterface.TAG_DATETIME, originalDate);
                exif2.saveAttributes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
                if (fis != null) fis.close();
                if (!bitmap.isRecycled()) bitmap.recycle();
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
    public static void deleteFile(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) return;
        File file = new File(Environment.getExternalStorageDirectory(), "/JustLike/images" + fileName);
        if (file.exists()) file.delete();
        File file1 = new File(context.getExternalCacheDir(), "/" + fileName);
        if (file1.exists()) file1.delete();
    }

    /**
     * 通过返回的 URI 来获取文件的真实路径
     *
     * @param context 上下文
     * @param uri     相册或文件管理器返回的 URI
     * @return 返回图片的真实路径
     */
    public static String getPath(Context context, Uri uri) {
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
    public static String getAbsolutePath(String path) {
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
     * 获取需要旋转的角度，将返回的值给 Picasso 的 rotate() 方法
     *
     * @param image   Image对象
     * @param isClick 通过点击判断路径来源
     * @return 返回需要旋转的角度
     */
    public static int getRotateDegree(Image image, boolean isClick) {
        int degree;
        String path = image.getPath(); // 获取图片原始路径
        /*
         * 判断是点击添加还是缓存加载
         */
        if (isClick) {
            // 如果路径是 /e 开头则是 FileProvider 返回
            if (path.startsWith("/e")) {
                String absolutePath = path.replace("/external_files",
                        "/storage/emulated/0");
                degree = FileUtils.getBitmapDegree(absolutePath);
            } else { // 否则路径正常截取
                String absolutePath = path.substring(image.getPath().indexOf("/s"));
                degree = FileUtils.getBitmapDegree(absolutePath);
            }
        } else { // 由于缓存的文件通过 FileProvider 提供路径，所以需要自己修改成绝对路径
            String fileName = path.substring(path.lastIndexOf("/"));
            String absolutePath = "/storage/emulated/0/Android/data/com.aaron.justlike/cache"
                    + fileName;
            degree = FileUtils.getBitmapDegree(absolutePath);
        }
        return degree;
    }

    /**
     * 加载保存在应用缓存目录的文件
     *
     * @param searchCacheDir 判断是加载应用缓存目录还是加载应用目录
     */
    public static void getLocalCache(Activity activity, List<Image> imageList, /*List<String> pathList,*/
                                     boolean searchCacheDir, String... type) {
        try {
             // 从缓存中读取的数据被放置在 JSON 数组中,
             // 并遍历 JSON 数组，从中取出文件的路径，并转换为 URI 传入
             // Image 构造方法，将 Image 对象传入集合并通知适配器更新，
             // 从而达到加载缓存的目的。
            JSONArray typeArray;
            for (String imageType : type) {
                if (searchCacheDir) {
                    typeArray = getAllFiles(activity.getExternalCacheDir().getAbsolutePath(),
                            imageType);
                } else {
                    typeArray = getAllFiles(Environment.getExternalStorageDirectory().getPath() + "/JustLike/images",
                            imageType);
                }
                if (typeArray != null) {
                    for (int i = 0; i < typeArray.length(); i++) {
                        JSONObject jsonObject = typeArray.getJSONObject(i);
                        String path = jsonObject.getString("path");
                        String fileName = path.substring(path.lastIndexOf("/") + 1);

                        MainActivity.getFileNameList().add(fileName);
                        Image image = new Image(path);
                        image.setCreateDate(getImageDate(path));
                        image.setSize(getImageSize(path));
                        imageList.add(image);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
