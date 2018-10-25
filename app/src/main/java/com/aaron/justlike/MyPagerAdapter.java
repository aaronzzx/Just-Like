package com.aaron.justlike;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyPagerAdapter extends PagerAdapter {

    private List<Uri> mUriList;
    private DisplayImageActivity mActivity;
    private String mPath;
    private boolean isFullScreen;

    MyPagerAdapter(List<Uri> uriList, DisplayImageActivity activity, String path) {
        mUriList = uriList;
        mActivity = activity;
        mPath = path;
    }

    @Override
    public int getCount() {
        return mUriList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Uri uri = mUriList.get(position);
        PhotoView photoView = new PhotoView(mActivity);
        ViewGroup parent = (ViewGroup) photoView.getParent();
        if (parent != null) {
            parent.removeView(photoView);
        }
        Picasso.get()
                .load(uri)
                .resize(3000, 3000)
                .onlyScaleDown()
                .rotate(FileUtils.getBitmapDegree(FileUtils.getAbsolutePath(mPath)))
                .centerInside()
                .into(photoView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toolbar toolbar = mActivity.getToolbar();
                if (isFullScreen) {
                    /*
                     * 全屏状态下执行此代码块会退出全屏
                     */
                    mActivity.getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    toolbar.setVisibility(View.VISIBLE);
                    isFullScreen = false;
                } else {
                    /*
                     * 进入全屏,自动沉浸
                     */
                    mActivity.getWindow().getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    toolbar.setVisibility(View.GONE);
                    isFullScreen = true;
                }
            }
        });
        container.addView(photoView);
        return photoView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
