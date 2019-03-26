package com.aaron.justlike.mvp.presenter.online.preview;

import android.content.Context;

import com.aaron.justlike.http.unsplash.entity.Photo;
import com.aaron.justlike.mvp.model.online.preview.IPreviewModel;
import com.aaron.justlike.mvp.model.online.preview.PreviewModel;
import com.aaron.justlike.mvp.view.online.IPreviewView;

public class PreviewPresenter implements IPreviewPresenter {

    public static final int NORMAL = 0;
    public static final int SET_WALLPAPER = 1;

    private IPreviewView mView;
    private IPreviewModel mModel;

    @Override
    public void attachView(IPreviewView view) {
        mView = view;
        mModel = new PreviewModel();
    }

    @Override
    public void detachView() {
        mView = null;
        mModel = null;
    }

    @Override
    public void requestImage(Photo photo) {
        mView.onHideProgressStatus();
        mView.onShowProgress();
        mView.onShowAuthorName(photo.getUser().getName());
        mView.onShowAuthorAvatar(photo.getUser().getProfileImage().getLarge());
        mView.onShowImageLikes(photo.getLikes() + " Likes");
        mView.onShowImageDate(photo.getCreatedAt().substring(0, 10));
        mView.onShowImage(photo.getUrls().getFull(), photo.getUrls().getRegular());
    }

    @Override
    public void requestMode(Context context, Photo photo, int mode) {
        String urls = photo.getUrls().getRaw();
        String photoId = photo.getId() + ".JPG";
        mModel.startDownload(context, urls, photoId, mode, new IPreviewModel.Callback() {
            @Override
            public void onResponse(String args) {
                mView.onShowMessage(args);
            }

            @Override
            public void onWallpaper(String imagePath) {
                mView.onSetWallpaper(imagePath);
            }
        });
    }
}
