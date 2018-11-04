package com.aaron.justlike.another;

public class AboutMessage {

    private int mIconId;
    private String mText;

    public AboutMessage(int iconId, String text) {
        mIconId = iconId;
        mText = text;
    }

    public int getIconId() {
        return mIconId;
    }

    public void setIconId(int iconId) {
        mIconId = iconId;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }
}
