package com.aaron.justlike.adapter.online;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.http.unsplash.entity.Photo;
import com.aaron.justlike.ui.ViewWrapper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class OnlineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 1;
    private Context mContext;
    private List<Photo> mPhotoList;
    private Callback<Photo> mCallback;

    public OnlineAdapter(List<Photo> photoList, Callback<Photo> callback) {
        mPhotoList = photoList;
        mCallback = callback;
    }

    public boolean isFooterView(int position) {
        return position == getItemCount() - 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(layoutInflater.inflate(R.layout.activity_online_footer, parent, false));
        } else if (viewType == TYPE_NORMAL) {
            View view = layoutInflater.inflate(R.layout.activity_online_recycler_item, parent, false);
            // 为子项设置点击监听
            final ViewHolder holder = new ViewHolder(view);
            holder.itemView.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                mCallback.onPress(mPhotoList.get(position));
            });
            return holder;
        }
        //noinspection ConstantConditions
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder && !mPhotoList.isEmpty()) {
            Photo photo = mPhotoList.get(position);
            String authorName = photo.getUser().getName();
            String authorImage = photo.getUser().getProfileImage().getLarge();
            String regular = photo.getUrls().getRegular();
            String color = photo.getColor();
            Drawable placeHolder = new ColorDrawable(Color.parseColor(color));
            ((ViewHolder) holder).placeHolder.setImageDrawable(placeHolder);

            // load author image
            Glide.with(mContext)
                    .load(authorImage)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_place_holder))
                    .into(((ViewHolder) holder).authorImage);

            // set author name
            ((ViewHolder) holder).authorName.setText(authorName);

            loadImage((ViewHolder) holder, regular, placeHolder);
        }
    }

    private void loadImage(ViewHolder holder, String urls, Drawable placeHolder) {
        Glide.with(mContext)
                .load(urls)
                .apply(new RequestOptions().placeholder(placeHolder))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.imageView.setImageDrawable(resource);
                        ObjectAnimator animator = ObjectAnimator.ofFloat(new ViewWrapper(holder.imageView), "saturation", 0, 1)
                                .setDuration(2000);
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                holder.placeHolder.animate().setDuration(300).alpha(0).start();
                            }
                        });
                        animator.start();
                        return true;
                    }
                })
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size() + 1;
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView imageView;
        ImageView placeHolder;
        ImageView authorImage;
        TextView authorName;

        ViewHolder(View view) {
            super(view);
            itemView = view;
            imageView = view.findViewById(R.id.image_view);
            placeHolder = view.findViewById(R.id.place_holder);
            authorImage = view.findViewById(R.id.author_image);
            authorName = view.findViewById(R.id.author_name);
        }
    }

    public interface Callback<T> {

        void onPress(T t);
    }
}
