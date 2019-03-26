package com.aaron.justlike.fragment;

import com.aaron.justlike.common.SquareFragment;

import androidx.appcompat.app.AlertDialog;

public class MainFragment extends SquareFragment {

    @Override
    public void onLongPress(int position) {
        new AlertDialog.Builder(mContext)
                .setTitle("删除图片")
                .setMessage("图片将从设备中删除")
                .setPositiveButton("确定", (dialog, which) -> {
                    String path = mImageList.get(position).getPath();
                    mImageList.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    mAdapter.notifyItemRangeChanged(0, mImageList.size() - 1);
                    ((SquareFragment.Callback) mContext).onDelete(path);
                })
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .show();
    }
}
