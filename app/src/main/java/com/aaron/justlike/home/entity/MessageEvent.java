package com.aaron.justlike.home.entity;

import com.aaron.justlike.another.Image;

import java.util.List;

public class MessageEvent {

    private int position;
    private List<Image> list;

    public MessageEvent(int position, List<Image> list) {
        this.position = position;
        this.list = list;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<Image> getList() {
        return list;
    }

    public void setList(List<Image> list) {
        this.list = list;
    }
}
