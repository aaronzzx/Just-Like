package com.aaron.justlike.online.preview;

import android.content.Context;

import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;

class PreviewPresenter implements IPreviewContract.P {

    private IPreviewContract.V mView;
    private IPreviewContract.M mModel;

    PreviewPresenter(IPreviewContract.V view) {
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
        mModel.startDownload(context, urls, photoId, mode, new IPreviewContract.M.Callback() {
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
