package com.aaron.justlike.common.http.unsplash.entity.collection;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Collection implements Parcelable {
    /**
     * id : 296
     * title : I like a man with a beard.
     * description : Yeah even Santa...
     * published_at : 2016-01-27T18:47:13-05:00
     * updated_at : 2016-07-10T11:00:01-05:00
     * curated : false
     * total_photos : 12
     * private : false
     * share_key : 312d188df257b957f8b86d2ce20e4766
     * cover_photo : {"id":"C-mxLOk6ANs","width":5616,"height":3744,"color":"#E4C6A2","likes":12,"liked_by_user":false,"description":"A man drinking a coffee.","user":{"id":"xlt1-UPW7FE","username":"lionsdenpro","name":"Greg Raines","portfolio_url":"https://example.com/","bio":"Just an everyday Greg","location":"Montreal","total_likes":5,"total_photos":10,"total_collections":13,"profile_image":{"small":"https://images.unsplash.com/profile-1449546653256-0faea3006d34?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1449546653256-0faea3006d34?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1449546653256-0faea3006d34?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"}},"urls":{"raw":"https://images.unsplash.com/photo-1449614115178-cb924f730780","full":"https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy","regular":"https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=1080&fit=max","small":"https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=400&fit=max","thumb":"https://images.unsplash.com/photo-1449614115178-cb924f730780?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&w=200&fit=max"},"links":{"self":"https://api.unsplash.com/photos/C-mxLOk6ANs","html":"https://unsplash.com/photos/C-mxLOk6ANs","download":"https://unsplash.com/photos/C-mxLOk6ANs/download"}}
     * user : {"id":"IFcEhJqem0Q","updated_at":"2016-07-10T11:00:01-05:00","username":"fableandfolk","name":"Annie Spratt","portfolio_url":"http://mammasaurus.co.uk","bio":"Follow me on Twitter &amp; Instagram @anniespratt\r\nEmail me at hello@fableandfolk.com","location":"New Forest National Park, UK","total_likes":0,"total_photos":273,"total_collections":36,"profile_image":{"small":"https://images.unsplash.com/profile-1450003783594-db47c765cea3?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32","medium":"https://images.unsplash.com/profile-1450003783594-db47c765cea3?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64","large":"https://images.unsplash.com/profile-1450003783594-db47c765cea3?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"}}
     */

    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("published_at")
    private String publishedAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("curated")
    private boolean curated;
    @SerializedName("total_photos")
    private int totalPhotos;
    @SerializedName("private")
    private boolean privated;
    @SerializedName("share_key")
    private String shareKey;
    @SerializedName("cover_photo")
    private CoverPhoto coverPhoto;
    @SerializedName("user")
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isCurated() {
        return curated;
    }

    public void setCurated(boolean curated) {
        this.curated = curated;
    }

    public int getTotalPhotos() {
        return totalPhotos;
    }

    public void setTotalPhotos(int totalPhotos) {
        this.totalPhotos = totalPhotos;
    }

    public boolean isPrivated() {
        return privated;
    }

    public void setPrivated(boolean privated) {
        this.privated = privated;
    }

    public String getShareKey() {
        return shareKey;
    }

    public void setShareKey(String shareKey) {
        this.shareKey = shareKey;
    }

    public CoverPhoto getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(CoverPhoto coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.publishedAt);
        dest.writeString(this.updatedAt);
        dest.writeByte(this.curated ? (byte) 1 : (byte) 0);
        dest.writeInt(this.totalPhotos);
        dest.writeByte(this.privated ? (byte) 1 : (byte) 0);
        dest.writeString(this.shareKey);
        dest.writeParcelable(this.coverPhoto, flags);
        dest.writeParcelable(this.user, flags);
    }

    public Collection() {
    }

    protected Collection(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.publishedAt = in.readString();
        this.updatedAt = in.readString();
        this.curated = in.readByte() != 0;
        this.totalPhotos = in.readInt();
        this.privated = in.readByte() != 0;
        this.shareKey = in.readString();
        this.coverPhoto = in.readParcelable(CoverPhoto.class.getClassLoader());
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<Collection> CREATOR = new Parcelable.Creator<Collection>() {
        @Override
        public Collection createFromParcel(Parcel source) {
            return new Collection(source);
        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }
    };
}
