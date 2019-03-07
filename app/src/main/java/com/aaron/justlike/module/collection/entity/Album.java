package com.aaron.justlike.module.collection.entity;

import java.util.Objects;

public class Album {

    private String mCollectionTitle;
    private String mElementTotal;
    private String mImagePath;
    private long mCreateAt;

    public long getCreateAt() {
        return mCreateAt;
    }

    public void setCreateAt(long createAt) {
        mCreateAt = createAt;
    }

    public String getCollectionTitle() {
        return mCollectionTitle;
    }

    public void setCollectionTitle(String collectionTitle) {
        mCollectionTitle = collectionTitle;
    }

    public String getElementTotal() {
        return mElementTotal;
    }

    public void setElementTotal(String elementTotal) {
        mElementTotal = elementTotal;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return mCreateAt == album.mCreateAt &&
                Objects.equals(mCollectionTitle, album.mCollectionTitle) &&
                Objects.equals(mElementTotal, album.mElementTotal) &&
                Objects.equals(mImagePath, album.mImagePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mCollectionTitle, mElementTotal, mImagePath, mCreateAt);
    }
}
