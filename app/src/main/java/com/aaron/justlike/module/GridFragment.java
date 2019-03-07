package com.aaron.justlike.module;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaron.justlike.R;
import com.aaron.justlike.custom.MyGridLayoutManager;
import com.aaron.justlike.module.collection.entity.DeletEvent;
import com.aaron.justlike.module.main.adapter.MainAdapter;
import com.aaron.justlike.module.main.entity.DeleteEvent;
import com.aaron.justlike.module.main.entity.Image;
import com.aaron.justlike.module.main.entity.PreviewEvent;
import com.aaron.justlike.module.main.view.PreviewActivity;
import com.aaron.justlike.util.SystemUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class GridFragment extends Fragment implements MainAdapter.Callback<Image> {

    private Context mContext;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Image> mImageList = new ArrayList<>();

    public GridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentLayout = inflater.inflate(R.layout.fragment_grid, container, false);
        initView(parentLayout);
        return parentLayout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接收 main 模块 PreviewActivity 传过来的关于被删除图片的信息，并更新 UI
     */
    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onDeleteEvent(DeleteEvent event) {
        int position = event.getPosition();
        String path = event.getPath();
        mImageList.remove(position);
        mAdapter.notifyDataSetChanged();
        ((GridFragment.Callback) mContext).onDelete(path);
    }

    /**
     * 接收 collection 模块 PreviewActivity 传过来的关于被删除图片的信息，并更新 UI
     */
    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
    public void onDeletEvent(DeletEvent event) {
        int position = event.getPosition();
        String path = event.getPath();
        mImageList.remove(position);
        mAdapter.notifyDataSetChanged();
        ((GridFragment.Callback) mContext).onDelete(path);
    }

    /**
     * Adapter 中发生 onClick 行为，需要回调此方法
     *
     * @param position 被点击图片在 List 中的位置
     * @param list     存放 Image 实例 的 List
     */
    @Override
    public void onPress(int position, List<Image> list) {
        EventBus.getDefault().postSticky(new PreviewEvent<>(position, list));
        startActivity(new Intent(mContext, PreviewActivity.class));
    }

    /**
     * Adapter 中发生 onLongClick 行为，需要回调此方法
     *
     * @param position 被删图片的索引
     */
    @Override
    public void onLongPress(int position) {
        new AlertDialog.Builder(mContext)
                .setTitle("删除图片")
                .setMessage("图片将会被删除，无法撤销")
                .setPositiveButton("确定", (dialog, which) -> {
                    String path = mImageList.get(position).getPath();
                    mImageList.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    mAdapter.notifyItemRangeChanged(0, mImageList.size() - 1);
                    ((GridFragment.Callback) mContext).onDelete(path);
                })
                .setNegativeButton("取消", (dialog, which) -> {
                }).show();
    }

    /**
     * 由 Activity 调用
     */
    @MainThread
    public void update(List<Image> list) {
//        mImageList.clear();
        mImageList.addAll(0, list);
//        mAdapter.notifyDataSetChanged();
        mAdapter.notifyItemRangeInserted(0, list.size());
        mAdapter.notifyItemRangeChanged(list.size(), mImageList.size() - list.size());
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * 单击 Toolbar 回顶部，由 Activity 调用
     */
    public void backToTop() {
        // 查找当前屏幕内第一个可见的 View
        View firstVisibleItem = mRecyclerView.getChildAt(0);
        // 查找当前 View 在 RecyclerView 中处于哪个位置
        int itemPosition = mRecyclerView.getChildLayoutPosition(firstVisibleItem);
        if (itemPosition >= 48) {
            mRecyclerView.scrollToPosition(42);
        }
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void initView(View parent) {
        mRecyclerView = parent.findViewById(R.id.rv);
        MyGridLayoutManager layoutManager = new MyGridLayoutManager(mContext, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mRecyclerView.addItemDecoration(new YItemDecoration());
        mAdapter = new MainAdapter<>(mImageList, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    ((GridFragment.Callback) mContext).onHide();
                } else if (dy < 0) {
                    ((GridFragment.Callback) mContext).onShow();
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
                outRect.right = SystemUtils.dp2px(mContext, 2.8F); // 8px
            } else if (parent.getChildAdapterPosition(view) % 3 == 1) {
                outRect.left = SystemUtils.dp2px(mContext, 1.3F); // 4px
                outRect.right = SystemUtils.dp2px(mContext, 1.3F); // 4px
            } else if (parent.getChildAdapterPosition(view) % 3 == 2) {
                outRect.left = SystemUtils.dp2px(mContext, 2.8F); // 8px
                outRect.right = 0;
            }
        }
    }

    private class YItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.bottom = 0;
            outRect.top = SystemUtils.dp2px(mContext, 4.2F); // 12px
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

        void onDelete(String path);

        default void onHide() {

        }

        default void onShow() {

        }
    }
}
