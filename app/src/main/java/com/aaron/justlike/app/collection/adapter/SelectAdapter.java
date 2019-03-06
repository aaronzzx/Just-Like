package com.aaron.justlike.app.collection.adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.aaron.justlike.R;
import com.aaron.justlike.app.main.entity.Image;
import com.aaron.justlike.custom.SquareView;
import com.aaron.justlike.util.AnimationUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Image> mImages; // App 中所有图片的集合
    private List<String> mOnPaths = new LinkedList<>(); // 用户选择的图片的集合，回调 Activity
    private List<String> mSelectedList;
    private Callback mCallback; // 回调 Activity
    private SparseBooleanArray mCheckStates = new SparseBooleanArray(); // 解决 View 复用混乱
    private SparseBooleanArray mEnableStates = new SparseBooleanArray();

    public SelectAdapter(List<Image> images, Callback callback) {
        mImages = images;
        mCallback = callback;
    }

    public void receiveSelected(List<String> selectedList) {
        mSelectedList = selectedList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.activity_collection_add_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        // 将之前已选的图片标记为不可选
        if (mSelectedList != null) {
            holder.checkBox.post(() -> {
                int position = holder.getAdapterPosition();
                String path = mImages.get(position).getPath();
                for (String selected : mSelectedList) {
                    if (path.equals(selected)) {
                        mEnableStates.put(position, false);
                        holder.checkBox.setEnabled(false);
                        holder.itemView.setClickable(false);
                        holder.squareView.setScaleX(0.8F);
                        holder.squareView.setScaleY(0.8F);
                    }
                }
            });
        }
        // 选取图片
        holder.itemView.setOnClickListener(v -> handleImageClick(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 加载视图
        ViewHolder viewHolder = (ViewHolder) holder;
        String path = mImages.get(position).getPath();

        RequestOptions options = new RequestOptions()
                .placeholder(R.color.colorGrey)
                .priority(Priority.HIGH);
        Glide.with(mContext)
                .load(path)
                .apply(options)
                .into(((ViewHolder) holder).squareView);

        // 解决 View 复用混乱
        selectBefore(viewHolder, position);
        selectRightNow(viewHolder, position);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    private void handleImageClick(ViewHolder holder) {
        if (holder.checkBox.isChecked()) {
            // 图片被选中状态下执行
            holder.checkBox.setChecked(false);
            AnimationUtil.setImageLarge(holder.squareView);
            // 将被取消选中的图片移除回调集合
            mOnPaths.remove(mImages.get(holder.getAdapterPosition()).getPath());
        } else {
            // 图片没被选中状态下执行
            holder.checkBox.setChecked(true);
            AnimationUtil.setImageSmall(holder.squareView);
            // 将选中的图片路径添加进回调集合
            mOnPaths.add(mImages.get(holder.getAdapterPosition()).getPath());
        }
        // 回调 Activity,进行创建集合的下一步工作
        mCallback.onSetTitle(mOnPaths);
    }

    private void selectBefore(ViewHolder viewHolder, int position) {
        if (!mEnableStates.get(position, true)) {
            viewHolder.checkBox.setEnabled(false);
            viewHolder.itemView.setClickable(false);
            viewHolder.squareView.setScaleX(0.8F);
            viewHolder.squareView.setScaleY(0.8F);
        } else {
            viewHolder.checkBox.setEnabled(true);
            viewHolder.itemView.setClickable(true);
            viewHolder.squareView.setScaleX(1.0F);
            viewHolder.squareView.setScaleY(1.0F);
        }
    }

    private void selectRightNow(ViewHolder viewHolder, int position) {
        viewHolder.checkBox.setTag(position);
        viewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("SelectAdapter", "-----selectRightNow()-----");
            int pos = (int) buttonView.getTag();
            if (isChecked) {
                mCheckStates.put(pos, true);
                // 重绘图片缩放状态
                // 加此 if 判断语句是为了避免图片二次缩放，因为在 onCreateViewHolder()
                // 方法中已经使用属性动画对点击图片进行缩放，再次缩放会造成图片过小
                if (viewHolder.squareView.getScaleX() == 1.0F
                        && viewHolder.squareView.getScaleY() == 1.0F) {
                    viewHolder.squareView.setScaleX(0.8F);
                    viewHolder.squareView.setScaleY(0.8F);
                }
            } else {
                mCheckStates.delete(pos);
                // 重绘图片缩放状态
                viewHolder.squareView.setScaleX(1.0F);
                viewHolder.squareView.setScaleY(1.0F);
            }
        });
        viewHolder.checkBox.setChecked(mCheckStates.get(position, false));
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        SquareView squareView;
        CheckBox checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            squareView = itemView.findViewById(R.id.square_view);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }

    public interface Callback {

        void onSetTitle(List<String> paths);
    }
}
