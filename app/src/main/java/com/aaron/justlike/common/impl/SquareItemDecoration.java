package com.aaron.justlike.common.impl;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.justlike.common.JustLike;
import com.aaron.justlike.common.util.SystemUtil;

/**
 * 正方形图片列表共用的间距类
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
public final class SquareItemDecoration {

    public static class XItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 3 == 0) {
                outRect.left = 0;
                outRect.right = SystemUtil.dp2px(JustLike.getContext(), 2.8F); // 8px
            } else if (parent.getChildAdapterPosition(view) % 3 == 1) {
                outRect.left = SystemUtil.dp2px(JustLike.getContext(), 1.3F); // 4px
                outRect.right = SystemUtil.dp2px(JustLike.getContext(), 1.3F); // 4px
            } else if (parent.getChildAdapterPosition(view) % 3 == 2) {
                outRect.left = SystemUtil.dp2px(JustLike.getContext(), 2.8F); // 8px
                outRect.right = 0;
            }
        }
    }

    public static class YItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.bottom = 0;
            outRect.top = SystemUtil.dp2px(JustLike.getContext(), 4.2F); // 12px
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 0;
            } else if (parent.getChildAdapterPosition(view) == 1) {
                outRect.top = 0;
            } else if (parent.getChildAdapterPosition(view) == 2) {
                outRect.top = 0;
            }
        }
    }

    private SquareItemDecoration() {}
}
