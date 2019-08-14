package com.aaron.justlike.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.base.image.ImageLoader;
import com.aaron.justlike.R;
import com.aaron.justlike.common.SquareFragment;
import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.common.event.PreviewEvent;
import com.aaron.justlike.common.widget.SquareView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Image> mList;

    public MainAdapter(List<Image> list) {
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_mine_recycler_item,
                parent, false);
        ViewHolder holder = new ViewHolder(view);

        // for Image onClick()
        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
//            mCallback.onPress(position, mList);
            EventBus.getDefault().postSticky(new PreviewEvent<>(PreviewEvent.FROM_MAIN_ACTIVITY, position, mList));
            mContext.startActivity(new Intent(mContext, PreviewActivity.class));
            ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // for Image onLongClick()
        holder.itemView.setOnLongClickListener(v -> {
            int position = holder.getAdapterPosition();
//            mCallback.onLongPress(position);
            new AlertDialog.Builder(mContext)
                    .setTitle("删除图片")
                    .setMessage("图片将从设备中删除")
                    .setPositiveButton("确定", (dialog, which) -> {
                        String path = mList.get(position).getPath();
                        mList.remove(position);
//                    mAdapter.notifyItemRemoved(position);
//                    mAdapter.notifyItemRangeChanged(0, mImageList.size() - 1);
                        notifyDataSetChanged();
                        ((SquareFragment.Callback) mContext).onDelete(path, mList.size() == 0);
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                    })
                    .show();
            return true;
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Image image = mList.get(position); // 从集合中找到 Image 对象
        String path = image.getPath();
        ViewHolder holder = (ViewHolder) viewHolder;

//        GlideApp.getInstance()
//                .with(mContext)
//                .asDrawable()
//                .load(path)
//                .placeHolder(R.color.colorBlue)
//                .transition(100)
//                .into(((ViewHolder) holder).squareView);
        Drawable placeholder = new ColorDrawable(mContext.getResources().getColor(R.color.colorBlue));
        ImageLoader.loadWithPlaceholder(mContext, path, placeholder, holder.squareView);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        SquareView squareView;

        ViewHolder(View view) {
            super(view);
            squareView = view.findViewById(R.id.square_view);
        }
    }
}
