package com.aaron.justlike.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.MainImageActivity;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.util.AnimationUtil;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;

public class MainImageAdapter extends PagerAdapter {

    private List<Image> mPathList;
    private MainImageActivity mActivity;
    private boolean isFullScreen;

    public MainImageAdapter(MainImageActivity activity, List<Image> pathList) {
        mActivity = activity;
        mPathList = pathList;
    }

    @Override
    public int getCount() {
        return mPathList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final Toolbar toolbar = mActivity.findViewById(R.id.activity_display_image_toolbar);
        final LinearLayout bottomBar = mActivity.findViewById(R.id.bottom_bar);
        final ImageView share = mActivity.findViewById(R.id.action_share);
        final ImageView info = mActivity.findViewById(R.id.action_info);
        final ImageView set_wallpaper = mActivity.findViewById(R.id.action_set_wallpaper);
        final ImageView delete = mActivity.findViewById(R.id.action_delete);
        String path = mPathList.get(position).getPath();
        final PhotoView photoView = new PhotoView(mActivity);
        photoView.enable();
        photoView.setMaxScale(2.5F);
        ViewGroup parent = (ViewGroup) photoView.getParent();
        if (parent != null) {
            parent.removeView(photoView);
        }
        RequestOptions options = new RequestOptions()
                .override(3000, 3000)
                .centerInside();
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory
                .Builder(300)
                .setCrossFadeEnabled(true).build();
        Glide.with(mActivity)
                .load(path)
                .apply(options)
                .transition(DrawableTransitionOptions.with(factory))
                .into(photoView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    // 全屏状态下执行此代码块会退出全屏
                    AnimationUtil.exitFullScreen(mActivity, toolbar, 0);
                    AnimationUtil.setBottomBar(bottomBar, "show", 0, share,
                            info, set_wallpaper, delete);
                    isFullScreen = false;
                } else {
                    // 进入全屏,自动沉浸
                    AnimationUtil.setFullScreen(mActivity, toolbar, 0);
                    AnimationUtil.setBottomBar(bottomBar, "hide", 0, share,
                            info, set_wallpaper, delete);
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

    @Override
    public int getItemPosition(@NonNull Object object) {
        View view = (View) object;
        int currentPosition = mActivity.getCurrentPosition();
        if (currentPosition == (Integer) view.getTag()) {
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }
}
