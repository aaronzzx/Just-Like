package com.aaron.justlike.main;

import android.app.Activity;
import android.content.Intent;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;

import com.aaron.base.image.test.DefaultOption;
import com.aaron.base.image.test.ImageLoader;
import com.aaron.base.image.test.ScaleType;
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

//        Drawable placeholder = new ColorDrawable(mContext.getResources().getColor(R.color.colorBlue));
//        ImageLoader.loadWithPlaceholder(mContext, path, placeholder, holder.squareView);
        ImageLoader.get()
                .load(mContext, new DefaultOption.Builder(path)
                .placeholder(R.color.colorBlue)
                .scaleType(ScaleType.CIRCLE_CROP)
                .into(holder.squareView));
    }
}
