package com.aaron.justlike.common.widget.imageSelector;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.base.image.DefaultOption;
import com.aaron.base.image.ImageLoader;
import com.aaron.justlike.R;
import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.common.manager.ThemeManager;
import com.aaron.justlike.common.util.AnimationUtil;
import com.aaron.justlike.common.widget.SquareView;

import java.util.ArrayList;
import java.util.List;

public class SelectorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Image> mImages; // App 中所有图片的集合
    private List<String> mSelectedList;
    private List<String> mResponse = new ArrayList<>();
    private Callback mCallback; // 回调 Activity

    private ThemeManager.Theme mTheme;

    // 解决 View 复用混乱
    private SparseBooleanArray mCheckStates = new SparseBooleanArray();
    private SparseBooleanArray mEnableStates = new SparseBooleanArray();

    public SelectorAdapter(List<Image> images, ThemeManager.Theme theme, Callback callback) {
        mImages = images;
        mTheme = theme;
        mCallback = callback;
    }

    public void setSelectedImage(List<String> selectedList) {
        mSelectedList = selectedList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.activity_selector_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view, mTheme);
        // 选取图片
        holder.itemView.setOnClickListener(v -> {
            handleImageClick(holder);
            mCallback.onPress(mResponse);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 加载视图
        ViewHolder viewHolder = (ViewHolder) holder;
        String path = mImages.get(position).getPath();

        ImageLoader.load(mContext, new DefaultOption.Builder(path)
                .placeholder(R.color.colorBlue)
                .crossFade(100)
                .into(((ViewHolder) holder).squareView));
        // 解决 View 复用混乱
        selectBefore(viewHolder, position);
        selectRightNow(viewHolder, position);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    private void handleImageClick(ViewHolder holder) {
        String path = mImages.get(holder.getAdapterPosition()).getPath();
        if (holder.checkBox.isChecked()) {
            // 图片被选中状态下执行
            holder.checkBox.setChecked(false);
            AnimationUtil.setImageLarge(holder.squareView);
            // 将被取消选中的图片移除回调集合
//            mOnPaths.remove(path);
            mResponse.remove(path);
        } else {
            // 图片没被选中状态下执行
            holder.checkBox.setChecked(true);
            AnimationUtil.setImageSmall(holder.squareView);
            // 将选中的图片路径添加进回调集合
//            mOnPaths.add(path);
            mResponse.add(path);
        }
    }

    private void selectBefore(ViewHolder viewHolder, int position) {
        // 将之前已选的图片标记为不可选
        if (mSelectedList != null) {
            for (String selected : mSelectedList) {
                String path = mImages.get(position).getPath();
                if (path.equals(selected)) mEnableStates.put(position, false);
            }
        }
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
            int pos = (int) buttonView.getTag();
            if (!buttonView.isEnabled()) return;
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
        boolean flag = mCheckStates.get(position, false);
        if (flag) {
            viewHolder.squareView.setScaleX(0.8F);
            viewHolder.squareView.setScaleY(0.8F);
        }
        viewHolder.checkBox.setChecked(flag);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        SquareView squareView;
        CheckBox checkBox;

        ViewHolder(@NonNull View itemView, ThemeManager.Theme theme) {
            super(itemView);
            this.itemView = itemView;
            squareView = itemView.findViewById(R.id.square_view);
            checkBox = itemView.findViewById(R.id.checkbox);
            setCheckBox(theme);
        }

        private void setCheckBox(ThemeManager.Theme theme) {
            if (theme == null) return;
            Drawable normal = itemView.getContext().getResources().getDrawable(R.drawable.checkbox_false);
            Drawable unClicked = itemView.getContext().getResources().getDrawable(R.drawable.checkbox_unclicked);
            Drawable checked = null;
            switch (theme) {
                case JUST_LIKE:
                    checked = itemView.getContext().getResources().getDrawable(R.drawable.checkbox_true_classic);
                    break;
                case WHITE:
                    checked = itemView.getContext().getResources().getDrawable(R.drawable.checkbox_true_white);
                    break;
                case BLACK:
                    checked = itemView.getContext().getResources().getDrawable(R.drawable.checkbox_true_black);
                    break;
                case GREY:
                    checked = itemView.getContext().getResources().getDrawable(R.drawable.checkbox_true_grey);
                    break;
                case GREEN:
                    checked = itemView.getContext().getResources().getDrawable(R.drawable.checkbox_true_green);
                    break;
                case RED:
                    checked = itemView.getContext().getResources().getDrawable(R.drawable.checkbox_true_red);
                    break;
                case PINK:
                    checked = itemView.getContext().getResources().getDrawable(R.drawable.checkbox_true_pink);
                    break;
                case BLUE:
                    checked = itemView.getContext().getResources().getDrawable(R.drawable.checkbox_true_blue);
                    break;
                case PURPLE:
                    checked = itemView.getContext().getResources().getDrawable(R.drawable.checkbox_true_purple);
                    break;
                case ORANGE:
                    checked = itemView.getContext().getResources().getDrawable(R.drawable.checkbox_true_brown);
                    break;
            }
            checkBox.setButtonDrawable(getSelector(normal, unClicked, checked));
        }

        private StateListDrawable getSelector(Drawable normal, Drawable unClicked, Drawable checked) {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_checked}, checked);
            stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, unClicked);
            stateListDrawable.addState(new int[]{}, normal);
            return stateListDrawable;
        }
    }

    public interface Callback {

        void onPress(List<String> response);
    }
}
