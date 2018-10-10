package com.aaron.justlike;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {

    private String mPath;

    private Uri mUri; // 用于在文件管理器中选择图片后传回的 URI 数据

    Image() {

    }

    Image(Uri uri) {
        mUri = uri;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        // 接收到对象后解序列化
        @Override
        public Image createFromParcel(Parcel source) {
            Image image = new Image();
            image.mPath = source.readString();
            image.mUri = Uri.parse(image.mPath);
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
        dest.writeString(mUri.toString());
    }
}
