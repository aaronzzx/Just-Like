package com.aaron.justlike.common.http.unsplash.entity.collection;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Links implements Parcelable {
    /**
     * self : https://api.unsplash.com/photos/C-mxLOk6ANs
     * html : https://unsplash.com/photos/C-mxLOk6ANs
     * download : https://unsplash.com/photos/C-mxLOk6ANs/download
     */

    @SerializedName("self")
    private String self;
    @SerializedName("html")
    private String html;
    @SerializedName("download")
    private String download;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.self);
        dest.writeString(this.html);
        dest.writeString(this.download);
    }

    public Links() {
    }

    protected Links(Parcel in) {
        this.self = in.readString();
        this.html = in.readString();
        this.download = in.readString();
    }

    public static final Parcelable.Creator<Links> CREATOR = new Parcelable.Creator<Links>() {
        @Override
        public Links createFromParcel(Parcel source) {
            return new Links(source);
        }

        @Override
        public Links[] newArray(int size) {
            return new Links[size];
        }
    };
}
