package com.aaron.justlike.module.collection.entity;

public class SelectEvent {

    private Collection collection;

    public SelectEvent(Collection collection) {
        this.collection = collection;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }
}
