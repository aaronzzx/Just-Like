package com.aaron.justlike.app.collection.entity;

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
}
