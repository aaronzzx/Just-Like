package com.aaron.justlike.mvp_presenter.online.preview;

import com.aaron.justlike.http.unsplash.entity.Photo;
import com.aaron.justlike.mvp_model.online.preview.IPreviewModel;
import com.aaron.justlike.mvp_model.online.preview.PreviewModel;
import com.aaron.justlike.mvp_view.online.IPreviewView;
import com.aaron.justlike.util.DownloadUtil;

import java.io.File;

public class PreviewPresenter implements IPreviewPresenter {

    private static final String PATH = "/storage/0/Pictures/JustLike/online";
    public static final int NORMAL = DownloadUtil.NORMAL;
    public static final int SET_WALLPAPER = DownloadUtil.SET_WALLPAPER;

    private IPreviewView mView;
    private IPreviewModel mModel;

    @Override
    public void attachView(IPreviewView view) {
        mView = view;
        mModel = new PreviewModel();
        File mkDir = new File(PATH);
        if (!mkDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            mkDir.mkdirs();
        }
    }

    @Override
    public void detachView() {
        mView = null;
        mModel = null;
    }

    @Override
    public void requestImage(Photo photo) {
        mView.onShowProgress();
        mView.onShowAuthorName(photo.getUser().getName());
        mView.onShowAuthorAvatar(photo.getUser().getProfileImage().getLarge());
        mView.onShowImageLikes(photo.getLikes() + " Likes");
        mView.onShowImageDate(photo.getCreatedAt().substring(0, 10));
        mView.onShowImage(photo.getUrls().getFull(), photo.getUrls().getRegular());
    }

    @Override
    public void requestMode(Photo photo, int mode) {
        String urls = photo.getLinks().getDownload();
        String photoId = photo.getId() + ".JPG";
        mModel.startDownload(urls, photoId, mode, new IPreviewModel.Callback() {
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
