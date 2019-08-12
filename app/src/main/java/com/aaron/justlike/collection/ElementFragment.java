package com.aaron.justlike.collection;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.aaron.justlike.common.SquareFragment;
import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.common.event.DeleteEvent;
import com.aaron.justlike.common.event.PreviewEvent;
import com.aaron.justlike.main.PreviewActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ElementFragment extends SquareFragment {

    @Override
    public void executeEvent(DeleteEvent event) {
        if (event.getEventType() == DeleteEvent.FROM_MAIN_ACTIVITY) {
            return;
        }
        int position = event.getPosition();
        String path = event.getPath();
        mImageList.remove(position);
        mAdapter.notifyDataSetChanged();
        ((SquareFragment.Callback) mContext).onDelete(path, mImageList.size() == 0);
    }

    @Override
    public void onPress(int position, List<Image> list) {
        EventBus.getDefault().postSticky(new PreviewEvent<>(PreviewEvent.FROM_ELEMENT_ACTIVITY, position, list));
        startActivity(new Intent(mContext, PreviewActivity.class));
        ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onLongPress(int position) {
        new AlertDialog.Builder(mContext)
                .setTitle("删除图片")
                .setMessage("图片将从集合中删除")
                .setPositiveButton("确定", (dialog, which) -> {
                    String path = mImageList.get(position).getPath();
                    mImageList.remove(position);
//                    mAdapter.notifyItemRemoved(position);
//                    mAdapter.notifyItemRangeChanged(0, mImageList.size() - 1);
                    mAdapter.notifyDataSetChanged();
                    ((SquareFragment.Callback) mContext).onDelete(path, mImageList.size() == 0);
                })
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .show();
    }
}
