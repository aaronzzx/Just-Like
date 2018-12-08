package com.aaron.justlike.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.OnlineActivity;
import com.aaron.justlike.activity.OnlineImageActivity;
import com.aaron.justlike.util.AnimationUtil;
import com.aaron.justlike.util.FileUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.Transition;
import com.kc.unsplash.models.Photo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class OnlineImageAdapter extends RecyclerView.Adapter<OnlineImageAdapter.ViewHolder> {

    private List<Photo> mPhotoList;
    private OnlineActivity mActivity;
    private int[] placeHolders = {R.drawable.place_holder_1,
            R.drawable.place_holder_2, R.drawable.place_holder_3,
            R.drawable.place_holder_4, R.drawable.place_holder_5,
            R.drawable.place_holder_6, R.drawable.place_holder_7,
            R.drawable.place_holder_8, R.drawable.place_holder_9,
            R.drawable.place_holder_10};

    public OnlineImageAdapter(OnlineActivity activity, List<Photo> photoList) {
        mActivity = activity;
        mPhotoList = photoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_online_recycler_item, parent, false);
        // 为子项设置点击监听
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(mActivity, OnlineImageActivity.class);
                intent.putExtra("photo", mPhotoList.get(position));
                mActivity.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Photo photo = mPhotoList.get(position);
        String authorName = photo.getUser().getName();
        String authorImage = photo.getUser().getProfileImage().getLarge();
        String urlsForImageView = photo.getUrls().getThumb();
        String urlsForFakeView = photo.getUrls().getRegular();
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_place_holder);
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory
                .Builder(300)
                .setCrossFadeEnabled(true).build();
        Glide.with(mActivity)
                .load(authorImage)
                .apply(options)
                .transition(DrawableTransitionOptions.with(factory))
                .into(holder.authorImage);
        holder.authorName.setText(authorName);
        RequestOptions options1 = new RequestOptions()
//                .centerCrop()
//                .placeholder(placeHolders[1])
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(mActivity)
                .load(urlsForImageView)
//                .apply(options1)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ColorMatrix matrix = new ColorMatrix();
                        matrix.setSaturation(0);
                        resource.setColorFilter(new ColorMatrixColorFilter(matrix));
                        holder.imageView.setImageDrawable(resource);
                        AlphaAnimation aa = new AlphaAnimation(0, 1);
                        aa.setDuration(300);
                        aa.setFillAfter(true);
                        holder.imageView.startAnimation(aa);
                        return false;
                    }
                })
                .into(holder.imageView);
        Glide.with(mActivity)
                .load(urlsForFakeView)
//                .apply(options1)
                .into(new ImageViewTarget<Drawable>(holder.fakeView) {
                    @Override
                    protected void setResource(@Nullable Drawable resource) {
                        AnimationUtil.showFakeView(holder.fakeView, resource);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView imageView;
        ImageView fakeView;
        ImageView authorImage;
        TextView authorName;

        /**
         * @param view 子项布局的最外层布局，即父布局。
         */
        ViewHolder(View view) {
            super(view);
            itemView = view;
            imageView = view.findViewById(R.id.image_view);
            fakeView = view.findViewById(R.id.fake_view);
            authorImage = view.findViewById(R.id.author_image);
            authorName = view.findViewById(R.id.author_name);
        }
    }
}
