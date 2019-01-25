package com.aaron.justlike.home.entity;

import java.util.List;

public class PreviewEvent<T> {

    private int position;
    private List<T> list;

    public PreviewEvent(int position, List<T> list) {
        this.position = position;
        this.list = list;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
