package com.aaron.justlike.main.entity;

public class ImageInfo {

    private String time;
    private String name;
    private String size;
    private String pixel;
    private String path;

    public ImageInfo(String time, String name, String size, String pixel, String path) {
        this.time = time;
        this.name = name;
        this.size = size;
        this.pixel = pixel;
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPixel() {
        return pixel;
    }

    public void setPixel(String pixel) {
        this.pixel = pixel;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
