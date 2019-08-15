package com.aaron.justlike.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;

import com.aaron.base.image.ImageLoader;
import com.aaron.justlike.R;
import com.aaron.justlike.common.adapter.SquareAdapter;
import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.common.event.PreviewEvent;
import com.aaron.justlike.main.preview.PreviewActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

class MainAdapter extends SquareAdapter {

    MainAdapter(List<Image> list) {
        super(list);
    }

    @Override
    protected void createHolder(SquareAdapter.ViewHolder holder, ViewGroup parent, int viewType) {
        // for Image onClick()
        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            EventBus.getDefault().postSticky(new PreviewEvent<>(PreviewEvent.FROM_MAIN_ACTIVITY, position, mList));
            mContext.startActivity(new Intent(mContext, PreviewActivity.class));
            ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // for Image onLongClick()
        holder.itemView.setOnLongClickListener(v -> {
            int position = holder.getAdapterPosition();
            new AlertDialog.Builder(mContext)
                    .setTitle("删除图片")
                    .setMessage("图片将从设备中删除")
                    .setPositiveButton("确定", (dialog, which) -> {
                        String path = mList.get(position).getPath();
                        mList.remove(position);
                        notifyDataSetChanged();
                        ((IMainCommunicable) mContext).onDelete(path, mList.size() == 0);
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                    })
                    .show();
            return true;
        });
    }

    @Override
    protected void bindHolder(SquareAdapter.ViewHolder holder, int position) {
        Image image = mList.get(position); // 从集合中找到 Image 对象
        String path = image.getPath();

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
//
//    @Override
//    public int getItemCount() {
//        return mList.size();
//    }

//    private static class ViewHolder extends RecyclerView.ViewHolder {
//        SquareView squareView;
//
//        ViewHolder(View view) {
//            super(view);
//            squareView = view.findViewById(R.id.square_view);
//        }
//    }
}
