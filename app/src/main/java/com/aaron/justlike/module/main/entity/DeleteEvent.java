package com.aaron.justlike.module.main.entity;

public class DeleteEvent {

    private int position;
    private String path;

    public DeleteEvent(int position, String path) {
        this.position = position;
        this.path = path;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
