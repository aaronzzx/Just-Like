package com.aaron.justlike.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.justlike.R;
import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.common.widget.SquareView;

import java.util.List;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public abstract class SquareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context mContext;
    protected List<Image> mList;

    public SquareAdapter(List<Image> list) {
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_mine_recycler_item,
                parent, false);
        ViewHolder holder = new ViewHolder(view);
        createHolder(holder, parent, viewType);
        return holder;
    }

    protected abstract void createHolder(ViewHolder holder, ViewGroup parent, int viewType);

    protected abstract void bindHolder(ViewHolder holder, int position);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        bindHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SquareView squareView;

        public ViewHolder(View view) {
            super(view);
            squareView = view.findViewById(R.id.square_view);
        }
    }
}
