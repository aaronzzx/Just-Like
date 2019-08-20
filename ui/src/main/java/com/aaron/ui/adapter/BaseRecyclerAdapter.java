package com.aaron.ui.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter {

    public static final int POS_EMPTY_VIEW = -1;

    protected static final int TYPE_EMPTY_VIEW = -1;
    protected static final int TYPE_UNDEFINE = 0;

    public BaseRecyclerAdapter() {
        // Empty Constructor
    }

    protected abstract RecyclerView.ViewHolder createHolder(@NonNull ViewGroup parent, int viewType);

    protected abstract void bindHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position);

    protected abstract int itemCount();

    protected abstract int itemViewType(int position);

    protected abstract boolean hasEmptyView();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return createHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        bindHolder(viewHolder, position);
    }

    @Override
    public int getItemCount() {
        if (hasEmptyView()) {
            return 1;
        }
        return itemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (hasEmptyView()) {
            return TYPE_EMPTY_VIEW;
        }
        return itemViewType(position);
    }
}
