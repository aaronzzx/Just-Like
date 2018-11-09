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
import java.util.List;

import androidx.core.content.FileProvider;

public class FileUtils {

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
    public static void saveToCache(Context context, String path) {
        File mkDir = new File(Environment.getExternalStorageDirectory(),
                "JustLike/images");
        if (!mkDir.exists()) mkDir.mkdirs();
        String fileName = path.substring(path.lastIndexOf("/"),
                path.lastIndexOf(".")) + ".JPG";
        File file = new File(Environment.getExternalStorageDirectory(),"/JustLike/images" + fileName);
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
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                } else {
                    byte[] buffer = new byte[1024];
                    int total;
                    while ((total = fis.read(buffer)) != -1) {
                        fos.write(buffer, 0, total);
                    }
                }
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
     * 加载保存在应用缓存目录的文件
     */
    public static void getLocalCache(Activity activity, List<Image> imageList, List<String> pathList,
                                     String[] type, boolean b) {
        try {
            /*
             * 从缓存中读取的数据被放置在 JSON 数组中,
             * 并遍历 JSON 数组，从中取出文件的路径，并转换为 URI 传入
             * Image 构造方法，将 Image 对象传入集合并通知适配器更新，
             * 从而达到加载缓存的目的。
             */
            JSONArray typeArray;
            for (String imageType : type) {
                if (b) {
                    typeArray = getAllFiles(Environment.getExternalStorageDirectory().getPath() + "/JustLike/images",
                            imageType);
                } else {
                    typeArray = getAllFiles(activity.getExternalCacheDir().getAbsolutePath() + "/JustLike/images",
                            imageType);
                }
                if (typeArray != null) {
                    for (int i = 0; i < typeArray.length(); i++) {
                        JSONObject jsonObject = typeArray.getJSONObject(i);
                        String path = jsonObject.getString("path");
                        String fileName = path.substring(path.lastIndexOf("/") + 1);

                        MainActivity.getFileNameList().add(fileName);
                        /*
                         * 之所以不能放在循环体外面，是因为除了 jpg 格式要加载，
                         * 还有其他格式图片，在如果放在循环体外，因为加载其他格式
                         * 图片与 jpg 格式不符合，所以会清空集合，导致 ViewPager
                         * 无法显示。
                         */
                        pathList.add(path);

                        imageList.add(new Image(path));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
