package com.aaron.justlike.common.event;

import java.util.List;

public class PreviewEvent<T> {

    public static final int FROM_MAIN_ACTIVITY = 0;
    public static final int FROM_ELEMENT_ACTIVITY = 1;

    private int eventType;
    private int position;
    private List<T> list;

    public PreviewEvent(int eventType, int position, List<T> list) {
        this.eventType = eventType;
        this.position = position;
        this.list = list;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
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
