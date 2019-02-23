package com.aaron.justlike.http.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserLinks implements Parcelable {
    /**
     * self : https://api.unsplash.com/photos/LBI7cgq3pbM
     * html : https://unsplash.com/photos/LBI7cgq3pbM
     * download : https://unsplash.com/photos/LBI7cgq3pbM/download
     * download_location : https://api.unsplash.com/photos/LBI7cgq3pbM/download
     */

    @SerializedName("self")
    private String self;
    @SerializedName("html")
    private String html;
    @SerializedName("download")
    private String download;
    @SerializedName("download_location")
    private String downloadLocation;

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

    public String getDownloadLocation() {
        return downloadLocation;
    }

    public void setDownloadLocation(String downloadLocation) {
        this.downloadLocation = downloadLocation;
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
        dest.writeString(this.downloadLocation);
    }

    public UserLinks() {
    }

    protected UserLinks(Parcel in) {
        this.self = in.readString();
        this.html = in.readString();
        this.download = in.readString();
        this.downloadLocation = in.readString();
    }

    public static final Parcelable.Creator<UserLinks> CREATOR = new Parcelable.Creator<UserLinks>() {
        @Override
        public UserLinks createFromParcel(Parcel source) {
            return new UserLinks(source);
        }

        @Override
        public UserLinks[] newArray(int size) {
            return new UserLinks[size];
        }
    };
}
