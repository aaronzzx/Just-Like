package com.aaron.justlike.util;

import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
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

    public static class DownloadUtils {

        // 下载器
        private DownloadManager downloadManager;
        // 上下文
        private Context mContext;
        // 下载的ID
        private long downloadId;
        // photoId
        private String mPhotoName;
        // 判断是否需要设置壁纸
        private int mFabType;

        public  DownloadUtils(Context context){
            mContext = context;
        }

        public void downloadImage(String url, String name, int fabType) {
            mPhotoName = name;
            mFabType = fabType;

            // 创建下载任务
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

            // 在通知栏中显示，默认就是显示的
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle(" " + name);

            // 设置下载的路径
            request.setDestinationInExternalPublicDir
                    (Environment.DIRECTORY_PICTURES, "/JustLike/online/" + name);
            // 设置通知栏点击跳转
            request.setMimeType("image/jpeg");

            // 获取DownloadManager
            downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            // 将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
            downloadId = downloadManager.enqueue(request);
            Toast.makeText(mContext, "开始下载", Toast.LENGTH_SHORT).show();

            // 注册广播接收者，监听下载状态
            mContext.registerReceiver(receiver,
                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        //广播监听下载的各个状态
        private BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkStatus();
            }
        };


        //检查下载状态
        private void checkStatus() {
            DownloadManager.Query query = new DownloadManager.Query();
            //通过下载的id查找
            query.setFilterById(downloadId);
            Cursor c = downloadManager.query(query);
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    //下载暂停
                    case DownloadManager.STATUS_PAUSED:
                        break;
                    //下载延迟
                    case DownloadManager.STATUS_PENDING:
                        break;
                    //正在下载
                    case DownloadManager.STATUS_RUNNING:
                        break;
                    //下载完成
                    case DownloadManager.STATUS_SUCCESSFUL:
                        if (mFabType == 1) {
                            String path = Environment.getExternalStoragePublicDirectory
                                    (Environment.DIRECTORY_PICTURES) + "/JustLike/online/" + mPhotoName;
                            int i = FileUtils.setWallpaper(mContext, path);
                            if (i == -1) {
                                Toast.makeText(mContext, "改造失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                        mContext.unregisterReceiver(receiver);
                        break;
                    //下载失败
                    case DownloadManager.STATUS_FAILED:
                        Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                        mContext.unregisterReceiver(receiver);
                        break;
                }
            }
            c.close();
        }
    }

    public static Bitmap getBitmapFormDrawable(Drawable drawable){
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
        drawable.getIntrinsicHeight(),drawable.getOpacity()!= PixelFormat.OPAQUE
        ?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        //设置绘画的边界，此处表示完整绘制
        drawable.draw(canvas);
        return bitmap;
    }

    public static void sortByName(List<Image> imageList, final boolean isOrder) {
        if (!imageList.isEmpty()) {
            Collections.sort(imageList, new Comparator<Image>() {
                @Override
                public int compare(Image o1, Image o2) {
                    if (isOrder) {
                        return o1.getFileName().compareTo(o2.getFileName());
                    } else {
                        return o2.getFileName().compareTo(o1.getFileName());
                    }
                }
            });
        }
    }

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

    public static int setWallpaper(Context context, String path) {
        Toast.makeText(context, "改造中", Toast.LENGTH_SHORT).show();
        WallpaperManager manager = WallpaperManager.getInstance(context);
        if (manager != null && path != null) {
            File file = new File(path);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                manager.setStream(fis);
                Toast.makeText(context, "改造成功", Toast.LENGTH_SHORT).show();
                return 1;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    public static String getImageName(String path) {
        return path.substring(path.lastIndexOf("/"));
    }

    /**
     * 获取图片生产时间
     */
    public static String getImageDate(String path) {
        File file = new File(path);
        return String.valueOf(file.lastModified());
    }

    public static long getImageSize(String path) {
        File file = new File(path);
        return file.length();
    }

    /**
     * 获取指定目录指定类型的文件，并将其转换成 JSON 数组
     *
     * @param dirPath 目录路径
     * @param type    文件类型
     * @return        返回 JSON 数组
     */
    public static JSONArray getAllFiles(String dirPath, String type) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return null;
        }
        File[] files = file.listFiles();
        if (files == null) {
            return null;
        }
        JSONArray fileList = new JSONArray();
        // 遍历数组内的文件
        for (File aFile : files) {
            // 如果是文件且后缀名为 type 就执行以下操作
            if (aFile.isFile() && aFile.getName()
                    .substring(aFile.getName().lastIndexOf(".") + 1)
                    .toLowerCase().equals(type)) {
                // 获取文件的绝对路径，并存入 JSON 数组
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
     * @param path    相册或文件管理器返回的路径
     */
    public static void saveToCache(String path, int num) {
        String originalDate;
        String createDate = SystemUtils.getCreateDate(path);
        if (!TextUtils.isEmpty(createDate)) {
            originalDate = createDate;
        } else {
            originalDate = SystemUtils.getLastModified(path, "yyyy-MM-dd HH:mm:ss");
        }
        String dirPath = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES) + "/JustLike/images";
        File mkDir = new File(dirPath);
        if (!mkDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            mkDir.mkdirs();
        }
        String date = SystemUtils.getCurrentDate("yyyyMMdd_HHmmss");
        String name = "/IMG_" + date + "_" + num;
        String suffix = path.substring(path.lastIndexOf("."));
        String fileName = name + suffix;
        String filePath = dirPath + fileName;
        File file = new File(filePath);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (!file.exists()) {
                fis = new FileInputStream(path);
                fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     */
    public static void deleteFile(String fileName) {
        if (TextUtils.isEmpty(fileName))
            return;
        String imagePath = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES) + "/JustLike/images" + fileName;
        String onlinePath = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES) + "/JustLike/online" + fileName;
        File images = new File(imagePath);
        if (images.exists()) {
            images.delete();
        }
        File online = new File(onlinePath);
        if (online.exists()) {
            online.delete();
        }
    }

    /**
     * 通过返回的 URI 来获取文件的真实路径
     *
     * @param  context 上下文
     * @param  uri     相册或文件管理器返回的 URI
     * @return         返回图片的真实路径
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
     * 加载保存在应用缓存目录的文件
     */
    public static void getLocalCache(List<Image> imageList, String... type) {
        try {
            for (String imageType : type) {
                String direction = Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_PICTURES).getPath();
                JSONArray typeArray = getAllFiles(direction + "/JustLike/images", imageType);
                JSONArray onlineArray = getAllFiles(direction + "/JustLike/online", imageType);
                if (typeArray != null) {
                    for (int i = 0; i < typeArray.length(); i++) {
                        JSONObject jsonObject = typeArray.getJSONObject(i);
                        String path = jsonObject.getString("path");
                        String fileName = path.substring(path.lastIndexOf("/") + 1);
                        MainActivity.getFileNameList().add(fileName);
                        Image image = new Image(path);
                        image.setFileName(getImageName(path));
                        image.setCreateDate(getImageDate(path));
                        image.setSize(getImageSize(path));
                        imageList.add(image);
                    }
                }
                if (onlineArray != null) {
                    for (int i = 0; i < onlineArray.length(); i++) {
                        JSONObject jsonObject = onlineArray.getJSONObject(i);
                        String path = jsonObject.getString("path");
                        String fileName = path.substring(path.lastIndexOf("/") + 1);
                        MainActivity.getFileNameList().add(fileName);
                        Image image = new Image(path);
                        image.setFileName(getImageName(path));
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
