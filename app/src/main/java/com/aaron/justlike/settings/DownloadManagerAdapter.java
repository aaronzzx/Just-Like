package com.aaron.justlike.settings;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.justlike.R;
import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.common.http.glide.GlideApp;
import com.aaron.justlike.common.util.FileUtil;

import java.io.File;
import java.util.List;

class DownloadManagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Image> mList;

    DownloadManagerAdapter(List<Image> list) {
        mList = list;
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
            String path = mList.get(position).getPath();
            Intent openImage = new Intent(Intent.ACTION_VIEW);
            openImage.setDataAndType(FileUtil.getImageContentUri(mContext, new File(path)), "image/*");
            mContext.startActivity(openImage);
        });

        // online
        holder.search.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            String path = mList.get(position).getPath();
            ((IDownloadCommunicable) mContext).onTap(v, path);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        String path = ((Image) mList.get(position)).getPath();
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        String photoId = fileName.substring(0, fileName.indexOf("."));
        ((ViewHolder) holder).imageId.setText(photoId);

        GlideApp.getInstance()
                .with(mContext)
                .asDrawable()
                .load(path)
                .placeHolder(R.color.colorBlue)
                .transition(50)
                .into(((ViewHolder) holder).imageView);
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
}
