package com.aaron.justlike.adapter.online;

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
import com.aaron.justlike.http.unsplash.entity.collection.Collection;
import com.aaron.justlike.library.glide.GlideApp;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private List<Collection> mCollectionList;
    private Callback<Collection> mCallback;

    public CollectionAdapter(List<Collection> collectionList, Callback<Collection> callback) {
        mCollectionList = collectionList;
        mCallback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.fragment_collection_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(v -> mCallback.onPress(mCollectionList.get(holder.getAdapterPosition())));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 取出 Activity 传过来的数据
//        Album album = mAlbums.get(position);
//        String title = album.getCollectionTitle();
//        String total = album.getElementTotal() + " 图片";
//        String path = album.getImagePath();
        Collection collection = mCollectionList.get(position);
        String title = collection.getTitle();
        String total = String.valueOf(collection.getTotalPhotos()) + " 照片";
        String authorImage = collection.getUser().getProfileImage().getLarge();
        String authorName = collection.getUser().getName();
        String photo = collection.getCoverPhoto().getUrls().getRegular();
        String color = collection.getCoverPhoto().getColor();
        Drawable placeHolder = new ColorDrawable(Color.parseColor(color));

        // 设置标题信息
        ((ViewHolder) holder).imageTitle.setText(title);
        ((ViewHolder) holder).imageTotal.setText(total);
        ((ViewHolder) holder).authorName.setText(authorName);

        // 加载集合的封面图
        GlideApp.getInstance()
                .with(mContext)
                .asDrawable()
                .load(photo)
                .placeHolder(placeHolder)
                .into(((ViewHolder) holder).itemImage);

        GlideApp.getInstance()
                .with(mContext)
                .asDrawable()
                .load(authorImage)
                .into(((ViewHolder) holder).authorImage);
    }

    @Override
    public int getItemCount() {
        return mCollectionList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView itemImage;
        TextView imageTotal;
        TextView imageTitle;
        ImageView authorImage;
        TextView authorName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemImage = itemView.findViewById(R.id.photo);
            imageTotal = itemView.findViewById(R.id.title);
            imageTitle = itemView.findViewById(R.id.count);
            authorImage = itemView.findViewById(R.id.author_image);
            authorName = itemView.findViewById(R.id.author_name);
        }
    }

    public interface Callback<T> {

        void onPress(T t);
    }
}
