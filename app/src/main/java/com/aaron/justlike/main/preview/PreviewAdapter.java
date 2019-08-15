package com.aaron.justlike.main.preview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.aaron.base.image.ImageLoader;
import com.aaron.justlike.common.bean.Image;
import com.bm.library.PhotoView;

import java.util.List;

class PreviewAdapter extends PagerAdapter {

    private List<Image> mList;
    private int mPosition;

    PreviewAdapter(List<Image> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Context context = container.getContext();
        mPosition = position;
        String path = mList.get(position).getPath();
        // 设置 PhotoView
        PhotoView photoView = new PhotoView(context);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        photoView.enable();
        photoView.setMaxScale(2.5F);
        ViewGroup parent = (ViewGroup) photoView.getParent();
        if (parent != null) {
            parent.removeView(photoView);
        }

//        GlideApp.getInstance()
//                .with(context)
//                .asDrawable()
//                .load(path)
//                .transition(200)
//                .into(photoView);
        ImageLoader.loadWithAnim(context, path, photoView);
        photoView.setOnClickListener(((IPreviewCommunicable) context)::onTap);
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
        if (mPosition == (Integer) view.getTag()) {
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }
}
