package com.aaron.justlike.adapter.theme;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ThemeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int DEFAULT = 0;
    public static final int WHITE = 1;
    public static final int BLACK = 2;
    public static final int GREY = 3;
    public static final int GREEN = 4;
    public static final int RED = 5;
    public static final int PINK = 6;
    public static final int BLUE = 7;
    public static final int PURPLE = 8;
    public static final int BROWN = 9;

    private Context mContext;
    private List<Drawable> mList;
    private Callback mCallback;

    public ThemeAdapter(List<Drawable> list, Callback callback) {
        mList = list;
        mCallback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.activity_theme_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            mCallback.onPress(position);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        initThemeName((ViewHolder) holder, position);

        initThemeColor((ViewHolder) holder, position);

        RequestOptions options = new RequestOptions()
                .placeholder(R.color.colorBlue);
        Glide.with(mContext)
                .load(mList.get(position))
                .apply(options)
                .into(new ImageViewTarget<Drawable>(((ViewHolder) holder).imageView) {
                    @Override
                    protected void setResource(@Nullable Drawable resource) {
                        if (resource != null) {
                            ColorMatrix matrix = new ColorMatrix();
                            matrix.setScale(0.65F, 0.65F, 0.65F, 1);
                            resource.setColorFilter(new ColorMatrixColorFilter(matrix));
                            ((ViewHolder) holder).imageView.setImageDrawable(resource);
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView imageView;
        TextView themeName;
        ViewGroup checkbox;
        CircleImageView background;
        ImageView check;

        ViewHolder(View view) {
            super(view);
            itemView = view;
            imageView = view.findViewById(R.id.image_view);
            themeName = view.findViewById(R.id.theme_name);
            checkbox = view.findViewById(R.id.checkbox);
            background = view.findViewById(R.id.background);
            check = view.findViewById(R.id.check);
        }
    }

    private void initThemeName(ViewHolder holder, int position) {
        String themeName = null;
        // 初始化主题名称
        switch (position) {
            case DEFAULT:
                themeName = "默认";
                break;
            case WHITE:
                themeName = "白色";
                break;
            case BLACK:
                themeName = "黑色";
                break;
            case GREY:
                themeName = "灰色";
                break;
            case GREEN:
                themeName = "绿色";
                break;
            case RED:
                themeName = "红色";
                break;
            case PINK:
                themeName = "粉色";
                break;
            case BLUE:
                themeName = "蓝色";
                break;
            case PURPLE:
                themeName = "紫色";
                break;
            case BROWN:
                themeName = "棕色";
                break;
        }
        holder.themeName.setText(themeName);
    }

    private void initThemeColor(ViewHolder holder, int position) {
        // 初始化主题颜色
        switch (position) {
            case DEFAULT:
                holder.background
                        .setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimary)));
                break;
            case WHITE:
                holder.background
                        .setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimaryWhite)));
                holder.check.setColorFilter(Color.BLACK);
                break;
            case BLACK:
                holder.background
                        .setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimaryBlack)));
                break;
            case GREY:
                holder.background
                        .setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimaryGrey)));
                break;
            case GREEN:
                holder.background
                        .setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimaryGreen)));
                break;
            case RED:
                holder.background
                        .setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimaryRed)));
                break;
            case PINK:
                holder.background
                        .setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimaryPink)));
                break;
            case BLUE:
                holder.background
                        .setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimaryBlue)));
                break;
            case PURPLE:
                holder.background
                        .setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimaryPurple)));
                break;
            case BROWN:
                holder.background
                        .setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimaryBrown)));
                break;
        }
    }

    public interface Callback {

        void onPress(int position);
    }
}
