package com.aaron.justlike.online.home;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.aaron.justlike.R;
import com.aaron.justlike.common.adapter.PhotoAdapter;
import com.aaron.justlike.common.event.PhotoEvent;
import com.aaron.justlike.common.http.glide.GlideApp;
import com.aaron.justlike.common.http.glide.request.Request;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.widget.ViewWrapper;
import com.aaron.justlike.online.preview.PreviewActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

class OnlineAdapter extends PhotoAdapter {

    OnlineAdapter(List<Photo> photoList) {
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
            GlideApp.getInstance()
                    .with(mContext)
                    .asDrawable()
                    .load(authorImage)
                    .placeHolder(R.drawable.ic_author_image)
                    .into(holder.authorImage);
            // load image
            GlideApp.getInstance()
                    .with(mContext)
                    .asDrawable()
                    .load(regular)
                    .placeHolder(placeHolder)
                    .transition(300)
                    .listener(new Request.Listener<Drawable>() {
                        @Override
                        public void onLoadFailed() {

                        }

                        @Override
                        public void onResourceReady(Drawable resource, boolean isFirstResource) {
                            // 仅对初次加载的图片做饱和度动画，意味着刷新
                            if (!mAnimatedFlag.get(position, false)) {
                                Animator animator = ObjectAnimator.ofFloat(new ViewWrapper(holder.imageView), "saturation", 0, 1);
                                animator.setDuration(2000).setInterpolator(new AccelerateDecelerateInterpolator());
                                animator.start();
                                mAnimatedFlag.put(position, true);
                            }
                        }
                    })
                    .into(holder.imageView);
        }
    }
}
