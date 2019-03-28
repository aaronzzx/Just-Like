package com.aaron.justlike.adapter.download;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.entity.Image;
import com.aaron.justlike.library.glide.GlideApp;

import java.util.List;

import androidx.annotation.NonNull;
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

        GlideApp.getInstance()
                .with(mContext)
                .asDrawable()
                .load(path)
                .placeHolder(R.color.colorBlue)
                .transition(300)
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

    public interface Callback {

        void onSearchByLocal(String path);

        void onSearchByOnline(String path);
    }
}
