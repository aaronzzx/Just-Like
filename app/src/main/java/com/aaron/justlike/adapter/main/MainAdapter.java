package com.aaron.justlike.adapter.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaron.justlike.R;
import com.aaron.justlike.entity.Image;
import com.aaron.justlike.library.glide.GlideApp;
import com.aaron.justlike.ui.SquareView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<T> mList;
    private Callback<T> mCallback;

    public MainAdapter(List<T> list, Callback<T> callback) {
        mList = list;
        mCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_main_recycler_item,
                parent, false);
        ViewHolder holder = new ViewHolder(view);

        // for Image onClick()
        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            mCallback.onPress(position, mList);
        });

        // for Image onLongClick()
        holder.itemView.setOnLongClickListener(v -> {
            int position = holder.getAdapterPosition();
            mCallback.onLongPress(position);
            return true;
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Image image = (Image) mList.get(position); // 从集合中找到 Image 对象
        String path = image.getPath();

        GlideApp.getInstance()
                .with(mContext)
                .asDrawable()
                .load(path)
                .placeHolder(R.color.colorBlue)
                .transition(100)
                .into(((ViewHolder) holder).squareView);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        SquareView squareView;

        ViewHolder(View view) {
            super(view);
            itemView = view;
            squareView = view.findViewById(R.id.square_view);
        }
    }

    /**
     * 发生点击事件时，回调 Activity
     */
    public interface Callback<T> {

        void onPress(int position, List<T> list);

        void onLongPress(int position);
    }
}
