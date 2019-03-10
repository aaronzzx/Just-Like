package com.aaron.justlike.mvp_view.online.preview;

public interface IPreviewView {

    void onShowImage(String urls, String thumbnail);

    void onShowAuthorName(String args);

    void onShowAuthorAvatar(String urls);

    void onShowImageLikes(String args);

    void onShowImageDate(String args);

    void onShowProgress();

    void onSetWallpaper(String imagePath);

    void onShowMessage(String args);
}
