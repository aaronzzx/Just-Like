package com.aaron.justlike.common;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.mine.MineAdapter;
import com.aaron.justlike.entity.DeleteEvent;
import com.aaron.justlike.entity.Image;
import com.aaron.justlike.ui.MyGridLayoutManager;
import com.aaron.justlike.util.SystemUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public abstract class SquareFragment extends Fragment implements MineAdapter.Callback<Image> {

    protected Context mContext;

    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    protected List<Image> mImageList = new ArrayList<>();

    public SquareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        // Inflate the layout for this fragment
        View parentLayout = inflater.inflate(R.layout.fragment_square, container, false);
        initView(parentLayout);
        return parentLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接收 PreviewActivity 传过来的关于被删除图片的信息，并更新 UI
     */
    @Subscribe()
    public void onDeleteEvent(DeleteEvent event) {
        executeEvent(event);
    }

    /**
     * 必须由子类实现，根据不同子类执行逻辑
     */
    public abstract void executeEvent(DeleteEvent event);

    /**
     * Adapter 中发生 onClick 行为，需要回调此方法
     *
     * @param position 被点击图片在 List 中的位置
     * @param list     存放 Image 实例的 List
     */
    @Override
    public abstract void onPress(int position, List<Image> list);

    /**
     * Adapter 中发生 onLongClick 行为，需要回调此方法，由子类实现
     *
     * @param position 被删图片的索引
     */
    @Override
    public abstract void onLongPress(int position);

    /**
     * 由 Activity 调用
     */
    public void updateForAdd(List<Image> list) {
        mImageList.addAll(0, list);
        mAdapter.notifyItemRangeInserted(0, list.size());
        mAdapter.notifyItemRangeChanged(list.size(), mImageList.size() - list.size());
        mRecyclerView.scrollToPosition(0);
    }

    public void update(List<Image> list) {
        mImageList.clear();
        mImageList.addAll(0, list);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * Called by activity
     */
    public void backToTop() {
        // 查找当前屏幕内第一个可见的 View
        View firstVisibleItem = mRecyclerView.getChildAt(0);
        // 查找当前 View 在 RecyclerView 中处于哪个位置
        int itemPosition = mRecyclerView.getChildLayoutPosition(firstVisibleItem);
        if (itemPosition >= 48) {
            mRecyclerView.scrollToPosition(36);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void initView(View parent) {
        mRecyclerView = parent.findViewById(R.id.rv);
        ((DefaultItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        MyGridLayoutManager layoutManager = new MyGridLayoutManager(mContext, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mRecyclerView.addItemDecoration(new YItemDecoration());
        mAdapter = new MineAdapter<>(mImageList, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    ((SquareFragment.Callback) mContext).onHide();
                } else if (dy < 0) {
                    ((SquareFragment.Callback) mContext).onShow();
                }
            }
        });
    }

    private class XItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 3 == 0) {
                outRect.left = 0;
                outRect.right = SystemUtil.dp2px(mContext, 2.8F); // 8px
            } else if (parent.getChildAdapterPosition(view) % 3 == 1) {
                outRect.left = SystemUtil.dp2px(mContext, 1.3F); // 4px
                outRect.right = SystemUtil.dp2px(mContext, 1.3F); // 4px
            } else if (parent.getChildAdapterPosition(view) % 3 == 2) {
                outRect.left = SystemUtil.dp2px(mContext, 2.8F); // 8px
                outRect.right = 0;
            }
        }
    }

    private class YItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.bottom = 0;
            outRect.top = SystemUtil.dp2px(mContext, 4.2F); // 12px
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 0;
            } else if (parent.getChildAdapterPosition(view) == 1) {
                outRect.top = 0;
            } else if (parent.getChildAdapterPosition(view) == 2) {
                outRect.top = 0;
            }
        }
    }

    public interface Callback {

        void onDelete(String path, boolean isEmpty);

        default void onHide() {

        }

        default void onShow() {

        }
    }
}
