package com.aaron.justlike.settings;

import com.aaron.justlike.R;
import com.aaron.justlike.common.bean.Library;
import com.aaron.justlike.common.bean.Message;

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

        public static final String[] TITLE = {"介绍", "反馈", "源码", "GITHUB", "捐赠(支付宝)"};

        public static final int[] ICON_ID = {
                R.drawable.ic_introduce,
                R.drawable.ic_email,
                R.drawable.ic_source_code,
                R.drawable.ic_github,
                R.drawable.ic_gift};

        public static final String[] LIBRARY_NAME = {
                "Glide",
                "CircleImageView",
                "StatusBarUtil",
                "PhotoView",
                "Matisse",
                "uCrop",
                "FloatingActionButton",
                "EventBus",
                "Gson",
                "OkHttp",
                "Retrofit",
                "RxJava",
                "RxAndroid",
                "Aria"};

        public static final String[] LIBRARY_AUTHOR = {
                "bumptech",
                "hdodenhof",
                "Jaeger",
                "bm-x",
                "zhihu",
                "Yalantis",
                "Clans",
                "greenrobot",
                "Google",
                "square",
                "square",
                "ReactiveX",
                "ReactiveX",
                "AriaLyy"};

        public static final String[] LIBRARY_INTRODUCE = {
                "An image loading and caching library for Android focused on smooth scrolling",
                "A circular ImageView for Android",
                "A util for setting status bar style on Android App",
                "PhotoView 图片浏览缩放控件",
                "A well-designed local image and video selector for Android",
                "Image Cropping Library for Android",
                "Android Floating Action Button based on Material Design specification",
                "Event bus for Android and Java that simplifies communication between Activities, Fragments, Threads, Services, etc. Less code, better quality. ",
                "A Java serialization/deserialization library to convert Java Objects into JSON and back",
                "An HTTP+HTTP/2 client for Android and Java applications.",
                "Type-safe HTTP client for Android and Java by Square, Inc.",
                "RxJava – Reactive Extensions for the JVM – a library for composing asynchronous and event-based programs using observable sequences for the Java VM.",
                "RxJava bindings for Android",
                "一个简洁的下载器，使用注解进行回调监听"};
    }
}
