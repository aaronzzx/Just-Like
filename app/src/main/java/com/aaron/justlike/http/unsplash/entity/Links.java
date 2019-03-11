package com.aaron.justlike.http.unsplash.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Links implements Parcelable {
    /**
     * self : https://api.unsplash.com/users/poorkane
     * html : https://unsplash.com/poorkane
     * photos : https://api.unsplash.com/users/poorkane/photos
     * likes : https://api.unsplash.com/users/poorkane/likes
     * portfolio : https://api.unsplash.com/users/poorkane/portfolio
     */

    @SerializedName("self")
    private String self;
    @SerializedName("html")
    private String html;
    @SerializedName("photos")
    private String photos;
    @SerializedName("likes")
    private String likes;
    @SerializedName("portfolio")
    private String portfolio;

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

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(String portfolio) {
        this.portfolio = portfolio;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.self);
        dest.writeString(this.html);
        dest.writeString(this.photos);
        dest.writeString(this.likes);
        dest.writeString(this.portfolio);
    }

    public Links() {
    }

    protected Links(Parcel in) {
        this.self = in.readString();
        this.html = in.readString();
        this.photos = in.readString();
        this.likes = in.readString();
        this.portfolio = in.readString();
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
