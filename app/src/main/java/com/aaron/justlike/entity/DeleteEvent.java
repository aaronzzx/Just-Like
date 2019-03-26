package com.aaron.justlike.entity;

public class DeleteEvent {

    public static final int FROM_MAIN_ACTIVITY = 0;
    public static final int FROM_ELEMENT_ACTIVITY = 1;

    private int eventType;
    private int position;
    private String path;

    public DeleteEvent(int eventType, int position, String path) {
        this.eventType = eventType;
        this.position = position;
        this.path = path;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
