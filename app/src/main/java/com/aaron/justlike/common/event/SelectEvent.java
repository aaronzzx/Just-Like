package com.aaron.justlike.common.event;

import com.aaron.justlike.common.bean.Collection;

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
