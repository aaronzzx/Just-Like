package com.aaron.justlike.app.main.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Image implements Parcelable {

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        // 接收到对象后解序列化
        @Override
        public Image createFromParcel(Parcel source) {
            Image image = new Image();
            image.path = source.readString();
            return image;
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
    private String date;
    private String name;
    private long size;

    public Image() {

    }

    private String path;

    public Image(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // 将数据写出
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return size == image.size &&
                Objects.equals(path, image.path) &&
                Objects.equals(name, image.name) &&
                Objects.equals(date, image.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, name, date, size);
    }
}
