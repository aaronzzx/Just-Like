package com.aaron.justlike.adapter;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.OnlineActivity;
import com.aaron.justlike.activity.OnlineImageActivity;
import com.aaron.justlike.util.AnimationUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.kc.unsplash.models.Photo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class OnlineImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 1;
    private LayoutInflater mLayoutInflater;
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
        mLayoutInflater = LayoutInflater.from(activity);
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
        if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(mLayoutInflater.inflate(R.layout.activity_online_footer, parent, false));
        } else if (viewType == TYPE_NORMAL) {
            View view = mLayoutInflater.inflate(R.layout.activity_online_recycler_item, parent, false);
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
        //noinspection ConstantConditions
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder && !mPhotoList.isEmpty()) {
            Photo photo = mPhotoList.get(position);
            String authorName = photo.getUser().getName();
            String authorImage = photo.getUser().getProfileImage().getLarge();
            String urls = photo.getUrls().getRegular();

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_place_holder)
                    .priority(Priority.HIGH);
            RequestOptions options1 = new RequestOptions()
                    .priority(Priority.HIGH);

            Glide.with(mActivity)
                    .load(authorImage)
                    .apply(options)
                    .into(((ViewHolder) holder).authorImage);
            ((ViewHolder) holder).authorName.setText(authorName);

            Glide.with(mActivity)
                    .load(urls)
                    .apply(options1)
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
                            ((ViewHolder) holder).fakeView.setImageDrawable(resource);
                            AnimationUtil.showViewByAlpha(((ViewHolder) holder).fakeView, 250);
                            return false;
                        }
                    })
                    .into(((ViewHolder) holder).fakeView);
            Glide.with(mActivity)
                    .load(urls)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            ((ViewHolder) holder).imageView.setImageDrawable(resource);
                            AnimationUtil.showViewByAlpha(((ViewHolder) holder).imageView, 2000);
                            return false;
                        }
                    })
                    .into(((ViewHolder) holder).imageView);
        } else if (holder instanceof FooterViewHolder) {

        }
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size() + 1;
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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
