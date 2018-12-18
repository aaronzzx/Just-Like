package com.aaron.justlike.adapter;

import android.app.Activity;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.util.AnimationUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadManagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mActivity;
    private List<Image> mImageList;

    public DownloadManagerAdapter(Activity activity, List<Image> imageList) {
        mActivity = activity;
        mImageList = imageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_download_manager_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        String path = mImageList.get(position).getPath();
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        String photoId = fileName.substring(0, fileName.indexOf("."));
        ((ViewHolder) holder).imageId.setText(photoId);
        Glide.with(mActivity)
                .load(mImageList.get(position).getPath())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ColorMatrix matrix = new ColorMatrix();
                        matrix.setScale(0.7F, 0.7F, 0.7F, 1);
                        resource.setColorFilter(new ColorMatrixColorFilter(matrix));
                        ((ViewHolder) holder).imageView.setImageDrawable(resource);
                        AnimationUtil.showViewByAlpha(((ViewHolder) holder).imageView,
                                0.5F, 1, 250);
                        return false;
                    }
                })
                .into(((ViewHolder) holder).imageView);
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView imageId;
        private ImageView search;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.image_view);
            imageId = view.findViewById(R.id.image_id);
            search = view.findViewById(R.id.ic_search);
        }
    }
}
