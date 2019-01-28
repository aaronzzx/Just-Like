package com.aaron.justlike.download.adapter;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.entity.Image;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadManagerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<T> mList;
    private Callback mCallback;

    public DownloadManagerAdapter(List<T> list, Callback callback) {
        mList = list;
        mCallback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.activity_download_manager_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        // local
        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            String path = ((Image) mList.get(position)).getPath();
            mCallback.onSearchByLocal(path);
        });

        // online
        holder.search.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            String path = ((Image) mList.get(position)).getPath();
            mCallback.onSearchByOnline(path);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        String path = ((Image) mList.get(position)).getPath();
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        String photoId = fileName.substring(0, fileName.indexOf("."));
        ((ViewHolder) holder).imageId.setText(photoId);

        RequestOptions options = new RequestOptions()
                .placeholder(R.color.colorGrey);
        Glide.with(mContext)
                .load(((Image) mList.get(position)).getPath())
                .apply(options)
                .into(new ImageViewTarget<Drawable>(((ViewHolder) holder).imageView) {
                    @Override
                    protected void setResource(@Nullable Drawable resource) {
                        if (resource != null) {
                            ColorMatrix matrix = new ColorMatrix();
                            matrix.setScale(0.65F, 0.65F, 0.65F, 1);
                            resource.setColorFilter(new ColorMatrixColorFilter(matrix));
                            ((ViewHolder) holder).imageView.setImageDrawable(resource);
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView imageView;
        TextView imageId;
        ImageView search;

        ViewHolder(View view) {
            super(view);
            itemView = view;
            imageView = view.findViewById(R.id.image_view);
            imageId = view.findViewById(R.id.image_id);
            search = view.findViewById(R.id.action_search);
        }
    }

    public interface Callback {

        void onSearchByLocal(String path);

        void onSearchByOnline(String path);
    }
}
