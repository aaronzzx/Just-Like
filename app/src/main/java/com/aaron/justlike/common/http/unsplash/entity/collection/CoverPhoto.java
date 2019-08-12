package com.aaron.justlike.common.http.unsplash.entity.collection;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CoverPhoto implements Parcelable {
    /**
     * id : C-mxLOk6ANs
     * width : 5616
     * height : 3744
     * color : #E4C6A2
     * likes : 12
     * liked_by_user : false
     * description : A man drinking a coffee.
     * user : {"id":"xlt1-UPW7FE","username":"lionsdenpro","name":"Greg Raines","portfolio_url":"https://example.com/","bio":"Just an everyday Greg","location":"Montreal","total_likes":5,"total_photos":10,"total_collections":13,"profile_image":{"small":"https://images.unsplash.com/profile-1449546653256-0faea3006d34?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1449546653256-0faea3006d34?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1449546653256-0faea3006d34?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"}}
     * urls : {"raw":"https://images.unsplash.com/photo-1449614115178-cb924f730780","full":"https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy","regular":"https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=1080&fit=max","small":"https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=400&fit=max","thumb":"https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=200&fit=max"}
     * links : {"self":"https://api.unsplash.com/photos/C-mxLOk6ANs","html":"https://unsplash.com/photos/C-mxLOk6ANs","download":"https://unsplash.com/photos/C-mxLOk6ANs/download"}
     */

    @SerializedName("id")
    private String id;
    @SerializedName("width")
    private int width;
    @SerializedName("height")
    private int height;
    @SerializedName("color")
    private String color;
    @SerializedName("likes")
    private int likes;
    @SerializedName("liked_by_user")
    private boolean likedByUser;
    @SerializedName("description")
    private String description;
    @SerializedName("user")
    private User user;
    @SerializedName("urls")
    private Urls urls;
    @SerializedName("links")
    private Links links;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(boolean likedByUser) {
        this.likedByUser = likedByUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Urls getUrls() {
        return urls;
    }

    public void setUrls(Urls urls) {
        this.urls = urls;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.color);
        dest.writeInt(this.likes);
        dest.writeByte(this.likedByUser ? (byte) 1 : (byte) 0);
        dest.writeString(this.description);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.urls, flags);
        dest.writeParcelable(this.links, flags);
    }

    public CoverPhoto() {
    }

    protected CoverPhoto(Parcel in) {
        this.id = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.color = in.readString();
        this.likes = in.readInt();
        this.likedByUser = in.readByte() != 0;
        this.description = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.urls = in.readParcelable(Urls.class.getClassLoader());
        this.links = in.readParcelable(Links.class.getClassLoader());
    }

    public static final Parcelable.Creator<CoverPhoto> CREATOR = new Parcelable.Creator<CoverPhoto>() {
        @Override
        public CoverPhoto createFromParcel(Parcel source) {
            return new CoverPhoto(source);
        }

        @Override
        public CoverPhoto[] newArray(int size) {
            return new CoverPhoto[size];
        }
    };
}
