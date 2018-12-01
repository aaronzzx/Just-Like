package com.aaron.justlike.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.OnlineActivity;
import com.aaron.justlike.activity.OnlineImageActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.kc.unsplash.models.Photo;

import java.util.List;

import androidx.annotation.NonNull;
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
                intent.putExtra("urls", mPhotoList.get(position));
                mActivity.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = mPhotoList.get(position);
        String authorName = photo.getUser().getName();
        String authorImage = photo.getUser().getProfileImage().getMedium();
        String urls = photo.getUrls().getRegular();
        Glide.with(mActivity)
                .load(authorImage)
                .into(holder.authorImage);
        holder.authorName.setText(authorName);
        RequestOptions options = new RequestOptions()
//                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory
                .Builder(300)
                .setCrossFadeEnabled(true).build();
        Glide.with(mActivity)
                .load(urls/* + "&fm=jpg&w=720"*/)
//                .thumbnail(0.5F)
//                .apply(options)
                .transition(DrawableTransitionOptions.with(factory))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView imageView;
        ImageView authorImage;
        TextView authorName;

        /**
         * @param view 子项布局的最外层布局，即父布局。
         */
        ViewHolder(View view) {
            super(view);
            itemView = view;
            imageView = view.findViewById(R.id.image_view);
            authorImage = view.findViewById(R.id.author_image);
            authorName = view.findViewById(R.id.author_name);
        }
    }
}
