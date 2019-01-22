package com.aaron.justlike.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.aaron.justlike.R;
import com.aaron.justlike.another.ISetToolbar;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.extend.SquareView;
import com.aaron.justlike.util.AnimationUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CollectionAddAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Image> mImages; // App 中所有图片的集合
    private List<String> mOnPaths = new LinkedList<>(); // 用户选择的图片的集合，回调 Activity
    private ISetToolbar mListener; // 回调 Activity
    private SparseBooleanArray mCheckStates = new SparseBooleanArray(); // 解决 View 复用混乱

    public CollectionAddAdapter(Context context, List<Image> images, ISetToolbar listener) {
        mContext = context;
        mImages = images;
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_collection_add_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        // 选取图片
        holder.itemView.setOnClickListener(v
                -> handleImageClick(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 加载视图
        String path = mImages.get(position).getPath();
        RequestOptions options = new RequestOptions()
                .placeholder(R.color.colorGrey)
                .priority(Priority.HIGH);
        Glide.with(mContext)
                .load(path)
                .apply(options)
                .into(((ViewHolder) holder).squareView);

        // 解决 View 复用混乱
        ((ViewHolder) holder).checkBox.setTag(position);
        ((ViewHolder) holder).checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int pos = (int) buttonView.getTag();
            if (isChecked) {
                mCheckStates.put(pos, true);
                // 重绘图片缩放状态
                // 加此 if 判断语句是为了避免图片二次缩放，因为在 onCreateViewHolder()
                // 方法中已经使用属性动画对点击图片进行缩放，再次缩放会造成图片过小
                if (((ViewHolder) holder).squareView.getScaleX() == 1.0F
                        && ((ViewHolder) holder).squareView.getScaleY() == 1.0F) {
                    ((ViewHolder) holder).squareView.setScaleX(0.8F);
                    ((ViewHolder) holder).squareView.setScaleY(0.8F);
                }
            } else {
                mCheckStates.delete(pos);
                // 重绘图片缩放状态
                ((ViewHolder) holder).squareView.setScaleX(1.0F);
                ((ViewHolder) holder).squareView.setScaleY(1.0F);
            }
        });
        ((ViewHolder) holder).checkBox.setChecked(mCheckStates.get(position, false));
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
        mListener.onSetToolbar(mOnPaths);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        SquareView squareView;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            squareView = itemView.findViewById(R.id.square_view);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
}
