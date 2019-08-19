package com.aaron.justlike.online.search;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.aaron.base.image.DefaultOption;
import com.aaron.base.image.ImageLoader;
import com.aaron.base.image.LoadListener;
import com.aaron.justlike.R;
import com.aaron.justlike.common.adapter.PhotoAdapter;
import com.aaron.justlike.common.event.PhotoEvent;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.widget.ViewWrapper;
import com.aaron.justlike.online.preview.PreviewActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
class PhotoAdapterImpl extends PhotoAdapter {

    PhotoAdapterImpl(List<Photo> photoList) {
        super(photoList);
        mPhotoList = photoList;
    }

    @Override
    protected void createHolder(PhotoAdapter.ViewHolder holder, ViewGroup parent, int viewType) {
        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            Photo photo = mPhotoList.get(position);
            EventBus.getDefault().postSticky(new PhotoEvent(photo));
            mContext.startActivity(new Intent(mContext, PreviewActivity.class));
            ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    @Override
    protected void bindHolder(PhotoAdapter.ViewHolder holder, int position) {
        if (!mPhotoList.isEmpty()) {
            Photo photo = mPhotoList.get(position);
            String authorName = photo.getUser().getName();
            String authorImage = photo.getUser().getProfileImage().getLarge();
            String regular = photo.getUrls().getRegular();
            String color = photo.getColor();
            Drawable placeHolder = new ColorDrawable(Color.parseColor(color));

            // set author name
            holder.authorName.setText(authorName);
            // load author image
            ImageLoader.load(mContext, new DefaultOption.Builder(authorImage)
                    .placeholder(R.drawable.ic_author_image)
                    .into(holder.authorImage));
            // load image
            ImageLoader.load(mContext, new DefaultOption.Builder(regular)
                    .placeholder(placeHolder)
                    .crossFade(300)
                    .addListener(new LoadListener() {
                        @Override
                        public boolean onSuccess(Object resource) {
                            // 仅对初次加载的图片做饱和度动画，意味着刷新
                            if (!mAnimatedFlag.get(position, false)) {
                                Animator animator = ObjectAnimator.ofFloat(new ViewWrapper(holder.imageView), "saturation", 0, 1);
                                animator.setDuration(2000).setInterpolator(new AccelerateDecelerateInterpolator());
                                animator.start();
                                mAnimatedFlag.put(position, true);
                            }
                            return false;
                        }

                        @Override
                        public boolean onFailure(Throwable e) {
                            return false;
                        }
                    })
                    .into(holder.imageView));
        }
    }
}
