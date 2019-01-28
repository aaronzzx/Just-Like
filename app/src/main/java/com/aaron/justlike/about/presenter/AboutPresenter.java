package com.aaron.justlike.about.presenter;

import com.aaron.justlike.about.entity.Library;
import com.aaron.justlike.about.entity.Message;
import com.aaron.justlike.about.view.IAboutView;

import java.util.ArrayList;
import java.util.List;

public class AboutPresenter implements IAboutPresenter {

    private List<Message> mMessageList = new ArrayList<>();
    private List<Library> mLibraryList = new ArrayList<>();

    private IAboutView<Message, Library> mView;

    public AboutPresenter(IAboutView<Message, Library> view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public void requestMessage(int[] iconId, String[] title) {
        if (iconId.length != 0 && iconId.length == title.length) {
            for (int i = 0; i < iconId.length; i++) {
                Message message = new Message(iconId[i], title[i]);
                mMessageList.add(message);
            }
            mView.onShowMessage(mMessageList);
        }
    }

    @Override
    public void requestLibrary(String[] name, String[] author, String[] introduce) {
        if (name.length == 0 || author.length == 0 || introduce.length == 0) {
            return;
        }
        if (name.length == author.length && name.length == introduce.length) {
            for (int i = 0; i < name.length; i++) {
                Library library = new Library(name[i], author[i], introduce[i]);
                mLibraryList.add(library);
            }
            mView.onShowLibrary(mLibraryList);
        }
    }

    public static class Element {

        private static final String INTRODUCE = "介绍";
        private static final String FEEDBACK = "反馈";
        private static final String SORCE_CODE = "源码";
        private static final String GITHUB = "GITHUB";

        private static final String GLIDE = "Glide";
        private static final String GLIDE_AUTHOR = "bumptech";
        private static final String GLIDE_INTRODUCE = "An image loading and caching library " +
                "for Android focused on smooth scrolling";

        private static final String CIRCLEIMAGEVIEW = "CircleImageView";
        private static final String CIRCLEIMAGEVIEW_AUTHOR = "hdodenhof";
        private static final String CIRCLEIMAGEVIEW_INTRODUCE = "A circular ImageView for Android";

        private static final String STATUSBARUTIL = "StatusBarUtil";
        private static final String STATUSBARUTIL_AUTHOR = "Jaeger";
        private static final String STATUSBARUTIL_INTRODUCE = "A util for setting status bar " +
                "style on Android App";

        private static final String PHOTOVIEW = "PhotoView";
        private static final String PHOTOVIEW_AUTHOR = "bm-x";
        private static final String PHOTOVIEW_INTRODUCE = "PhotoView 图片浏览缩放控件";

        private static final String MATISSE = "Matisse";
        private static final String MATISSE_AUTHOR = "zhihu";
        private static final String MATISSE_INTRODUCE = "A well-designed local image and video " +
                "selector for Android";

        private static final String UCROP = "uCrop";
        private static final String UCROP_AUTHOR = "Yalantis";
        private static final String UCROP_INTRODUCE = "Image Cropping Library for Android";

        private static final String ANDROID_UNSPLASH = "AndroidUnsplash(Unofficial)";
        private static final String ANDROID_UNSPLASH_AUTHOR = "KeenenCharles";
        private static final String ANDROID_UNSPLASH_INTRODUCE = "An unofficial Splash API library for Android";

        private static final String FLOATINGACTIONBUTTON = "FloatingActionButton";
        private static final String FLOATINGACTIONBUTTON_AUTHOR = "Clans";
        private static final String FLOATINGACTIONBUTTON_INTRODUCE = "Android Floating Action " +
                "Button based on Material Design specification";
    }
}
