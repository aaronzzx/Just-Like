package com.aaron.justlike.common.util;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import androidx.core.content.FileProvider;

import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.main.preview.PreviewActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class FileUtil {

    public static long getFileSize(String path) {
        File file = new File(path);
        return file.length();
    }

    public static int[] getImageWidthHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        // 最关键在此，把options.inJustDecodeBounds = true;
        // 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        // options.outHeight为原始图片的高
        return new int[]{options.outWidth, options.outHeight};
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

    public static void sortByName(List<Image> imageList, final boolean ascendingOrder) {
        if (!imageList.isEmpty()) {
            Collections.sort(imageList, new Comparator<Image>() {
                @Override
                public int compare(Image o1, Image o2) {
                    if (ascendingOrder) {
                        return o1.getName().compareTo(o2.getName());
                    } else {
                        return o2.getName().compareTo(o1.getName());
                    }
                }
            });
        }
    }

    public static void sortByDate(List<Image> imageList, final boolean ascendingOrder) {
        if (!imageList.isEmpty()) {
            Collections.sort(imageList, new Comparator<Image>() {
                @Override
                public int compare(Image o1, Image o2) {
                    if (ascendingOrder) {
                        return o1.getDate().compareTo(o2.getDate());
                    } else {
                        return o2.getDate().compareTo(o1.getDate());
                    }
                }
            });
        }
    }

    public static void sortBySize(List<Image> imageList, final boolean ascendingOrder) {
        if (!imageList.isEmpty()) {
            Collections.sort(imageList, new Comparator<Image>() {
                @Override
                public int compare(Image o1, Image o2) {
                    if (ascendingOrder) {
                        return Long.compare(o1.getSize(), o2.getSize());
                    } else {
                        return Long.compare(o2.getSize(), o1.getSize());
                    }
                }
            });
        }
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
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
        WallpaperManager manager = WallpaperManager.getInstance(context);
        if (manager != null && path != null) {
            File file = new File(path);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                manager.setStream(fis);
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
        return path.substring(path.lastIndexOf("/") + 1);
    }

    /**
     * 获取图片生产时间
     */
    public static String getImageDate(String path) {
        File file = new File(path);
        return String.valueOf(file.lastModified());
    }

    /**
     * 获取最后修改日期
     */
    @SuppressLint("SimpleDateFormat")
    public static String getLastModified(String path, String pattern) {
        long time = new File(path).lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date d = new Date(time);
        return formatter.format(d);
    }

    public static long getImageSize(String path) {
        File file = new File(path);
        return file.length();
    }

    public static String getImageSizeByMb(String path) {
        float length = (float) getFileSize(path) / 1024 / 1024;
        String size = String.valueOf(length);
        return size.substring(0, 4) + " MB";
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
     * 删除文件
     */
    public static void deleteFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }

    /**
     * 获取从用户点击图片资源后返回的 URI ，并直接将文件缓存到应用缓存目录下
     *
     * @param path    相册或文件管理器返回的路径
     */
    public static String saveToCache(String path, int num) {
        String filePath;
        String originalDate;
        String createDate = SystemUtil.getCreateDate(path);
        if (!TextUtils.isEmpty(createDate)) {
            originalDate = createDate;
        } else {
            originalDate = SystemUtil.getLastModified(path, "yyyy-MM-dd HH:mm:ss");
        }
        String dirPath = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES) + "/JustLike/images";
        File mkDir = new File(dirPath);
        if (!mkDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            mkDir.mkdirs();
        }
        String date = SystemUtil.getCurrentDate("yyyyMMdd_HHmmss");
        String name = "/IMG_" + date + "_" + num;
        String suffix = path.substring(path.lastIndexOf("."));
        String fileName = name + suffix;
        filePath = dirPath + fileName;
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
        return filePath;
    }

    /**
     * 加载缓存文件
     */
    public static boolean getLocalFiles(List<Image> imageList, String path, String... type) {
        File files = new File(path);
        if (files.exists()) {
            File[] fileList = files.listFiles();
            if (fileList == null) return false;
            for (File file : fileList) {
                if (file.isFile()) {
                    for (String fileType : type) {
                        if (file.getName().substring(file.getName().lastIndexOf(".") + 1)
                                .toLowerCase().equals(fileType)) {
                            String filePath = file.getAbsolutePath();
                            Image image = new Image(filePath);
                            image.setDate(getImageDate(filePath));
                            image.setName(getImageName(filePath));
                            image.setSize(getImageSize(filePath));
                            image.setEventFlag(PreviewActivity.DELETE_EVENT);
                            imageList.add(image);
                        }
                    }
                } else if (file.isDirectory()) {
                    getLocalFiles(imageList, file.getAbsolutePath(), type);
                }
            }
            return true;
        }
        return false;
    }

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
        //广播监听下载的各个状态
        private BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                checkStatus();
            }
        };

        public DownloadUtils(Context context) {
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
                            int flag = FileUtil.setWallpaper(mContext, path);
                            if (flag == -1) {
                                Toast.makeText(mContext, "设置失败", Toast.LENGTH_SHORT).show();
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
}
