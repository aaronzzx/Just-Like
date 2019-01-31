package com.aaron.justlike.app.collection.entity;

import org.litepal.crud.LitePalSupport;

public class Element extends LitePalSupport {

    private int id;
    private String title;
    private String path;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
