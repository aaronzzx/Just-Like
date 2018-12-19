package com.aaron.justlike.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.OnlineImageActivity;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.util.AnimationUtil;
import com.aaron.justlike.util.FileUtils;
import com.aaron.justlike.util.LogUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;
import com.kc.unsplash.Unsplash;
import com.kc.unsplash.models.Photo;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadManagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String CLIENT_ID = "18db24a3d59a1b2633897fa63f3f49455c2cbfa8a22e5b8520141cb2660fa816";
    private static final Unsplash UNSPLASH = new Unsplash(CLIENT_ID);
    private Activity mActivity;
    private List<Image> mImageList;

    public DownloadManagerAdapter(Activity activity, List<Image> imageList) {
        mActivity = activity;
        mImageList = imageList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_download_manager_recycler_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                String path = mImageList.get(position).getPath();
                Intent openImage = new Intent(Intent.ACTION_VIEW);
                openImage.setDataAndType(FileUtils
                        .getImageContentUri(mActivity, new File(path)), "image/*");
                mActivity.startActivity(openImage);
            }
        });
        holder.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                String path = mImageList.get(position).getPath();
                String fileName = path.substring(path.lastIndexOf("/") + 1);
                String photoId = fileName.substring(0, fileName.indexOf("."));
                loadImage(photoId, v);
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
                        matrix.setScale(0.65F, 0.65F, 0.65F, 1);
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

        private View itemView;
        private ImageView imageView;
        private TextView imageId;
        private ImageView search;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            imageView = view.findViewById(R.id.image_view);
            imageId = view.findViewById(R.id.image_id);
            search = view.findViewById(R.id.action_search);
        }
    }

    private void loadImage(final String photoId, final View view) {
        final ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("加载网络资源");
        dialog.setMessage("Loading...");
        dialog.show();
        UNSPLASH.getPhoto(photoId, new Unsplash.OnPhotoLoadedListener() {
            @Override
            public void onComplete(Photo photo) {
                dialog.dismiss();
                Intent intent = new Intent(mActivity, OnlineImageActivity.class);
                intent.putExtra("photo", photo);
                mActivity.startActivity(intent);
            }

            @Override
            public void onError(String error) {
                dialog.dismiss();
                Snackbar.make(view, "加载失败", Snackbar.LENGTH_LONG)
                        .setAction("重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadImage(photoId, view);
                            }
                        })
                        .show();
            }
        });
    }
}
