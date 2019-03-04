package com.aaron.justlike.app.main;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaron.justlike.R;
import com.aaron.justlike.app.main.adapter.MainAdapter;
import com.aaron.justlike.app.main.entity.Image;
import com.aaron.justlike.custom.MyGridLayoutManager;
import com.aaron.justlike.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class GridFragment extends Fragment implements MainAdapter.Callback<Image> {

    private RecyclerView mRecyclerView;

    private RecyclerView.Adapter mAdapter;
    private List<Image> mImageList = new ArrayList<>();

    public GridFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parent = inflater.inflate(R.layout.fragment_grid, container, false);
        initView(parent);
        return parent;
    }

    @Override
    public void onPress(int position, List<Image> list) {

    }

    @Override
    public void onLongPress(int position) {

    }

    public void update(List<Image> list) {
        mImageList.addAll(0, list);
        mAdapter.notifyItemRangeInserted(0, list.size());
        mAdapter.notifyItemRangeChanged(list.size(), mImageList.size() - list.size());
        mRecyclerView.scrollToPosition(0);
    }

    private void initView(View parent) {
        mRecyclerView = parent.findViewById(R.id.rv);
        MyGridLayoutManager layoutManager = new MyGridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new XItemDecoration());
        mRecyclerView.addItemDecoration(new YItemDecoration());
        mAdapter = new MainAdapter<>(mImageList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class XItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 3 == 0) {
                outRect.left = 0;
                outRect.right = SystemUtils.dp2px(getContext(), 2.8F); // 8px
            } else if (parent.getChildAdapterPosition(view) % 3 == 1) {
                outRect.left = SystemUtils.dp2px(getContext(), 1.3F); // 4px
                outRect.right = SystemUtils.dp2px(getContext(), 1.3F); // 4px
            } else if (parent.getChildAdapterPosition(view) % 3 == 2) {
                outRect.left = SystemUtils.dp2px(getContext(), 2.8F); // 8px
                outRect.right = 0;
            }
        }
    }

    private class YItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.bottom = 0;
            outRect.top = SystemUtils.dp2px(getContext(), 4.2F); // 12px
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 0;
            } else if (parent.getChildAdapterPosition(view) == 1) {
                outRect.top = 0;
            } else if (parent.getChildAdapterPosition(view) == 2) {
                outRect.top = 0;
            }
        }
    }
}
