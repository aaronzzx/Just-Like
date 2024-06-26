package com.aaron.justlike.collection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.base.image.DefaultOption;
import com.aaron.base.image.ImageLoader;
import com.aaron.justlike.R;
import com.aaron.justlike.common.bean.Album;

import java.util.List;

class CollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Album> mAlbums;
//    private OnPressCallback mCallback;

    CollectionAdapter(/*OnPressCallback callback, */List<Album> albums) {
        mAlbums = albums;
//        mCallback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.activity_collection_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(v -> {
//            mCallback.onPress(holder.getAdapterPosition())
            ((ICollectionCommunicable) mContext).onTap(v, holder.getAdapterPosition());
        });
        holder.itemView.setOnLongClickListener(v -> {
//            mCallback.onLongPress(holder.getAdapterPosition());
            ((ICollectionCommunicable) mContext).onLongTap(v, holder.getAdapterPosition());
            return true;
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 取出 Activity 传过来的数据
        Album album = mAlbums.get(position);
        String title = album.getCollectionTitle();
        String total = album.getElementTotal() + " 图片";
        String path = album.getImagePath();

        // 设置标题信息
        ((ViewHolder) holder).imageTitle.setText(title);
        ((ViewHolder) holder).imageTotal.setText(total);

        // 加载集合的封面图
        ImageLoader.load(mContext, new DefaultOption.Builder(path)
                .placeholder(R.color.colorBlue)
                .crossFade(300)
                .into(((ViewHolder) holder).itemImage));
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView itemImage;
        TextView imageTotal;
        TextView imageTitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemImage = itemView.findViewById(R.id.collection_image);
            imageTotal = itemView.findViewById(R.id.collection_image_total);
            imageTitle = itemView.findViewById(R.id.collection_image_title);
        }
    }

//    public interface OnPressCallback {
//
//        void onPress(int position);
//
//        void onLongPress(int position);
//    }
}
