package com.aaron.justlike.another;

import android.os.Parcel;
import android.os.Parcelable;

import com.kc.unsplash.models.Photo;
import com.kc.unsplash.models.Urls;

import java.net.URL;
import java.util.Comparator;

public class Image implements Parcelable{

    private String mPath; // 用于在文件管理器中选择图片后传回的路径
    private String mFileName;
    private String mCreateDate;
    private long mSize;

    public Image() {

    }

    public Image(String path) {
        mPath = path;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getCreateDate() {
        return mCreateDate;
    }

    public void setCreateDate(String createDate) {
        mCreateDate = createDate;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        // 接收到对象后解序列化
        @Override
        public Image createFromParcel(Parcel source) {
            Image image = new Image();
            image.mPath = source.readString();
            return image;
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // 将数据写出
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
    }
}
