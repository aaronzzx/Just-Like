package com.aaron.justlike.common.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Image implements Parcelable {

    private String date;
    private String name;
    private String path;
    private long size;
    private int eventFlag;

    public Image() {

    }

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

    public int getEventFlag() {
        return eventFlag;
    }

    public void setEventFlag(int eventFlag) {
        this.eventFlag = eventFlag;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.name);
        dest.writeLong(this.size);
        dest.writeInt(this.eventFlag);
        dest.writeString(this.path);
    }

    protected Image(Parcel in) {
        this.date = in.readString();
        this.name = in.readString();
        this.size = in.readLong();
        this.eventFlag = in.readInt();
        this.path = in.readString();
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
