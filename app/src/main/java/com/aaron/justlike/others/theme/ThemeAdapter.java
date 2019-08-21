package com.aaron.justlike.others.theme;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.base.image.DefaultOption;
import com.aaron.base.image.ImageLoader;
import com.aaron.justlike.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class ThemeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int WHITE = 0;
    static final int BLACK = 1;
    static final int GREY = 2;
    static final int GREEN = 3;
    static final int RED = 4;
    static final int PINK = 5;
    static final int BLUE = 6;
    static final int PURPLE = 7;
    static final int ORANGE = 8;
    static final int JUST_LIKE = 9;

    private Context mContext;
    private List<Integer> mList;
    //    private Callback mCallback;
    private int mCurrentCheck;

    ThemeAdapter(List<Integer> list,/* Callback callback,*/ int currentCheck) {
        mList = list;
//        mCallback = callback;
        mCurrentCheck = currentCheck;
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
            ((IThemeCommunicable) mContext).onTap(position, mCurrentCheck);
//            mCallback.onPress(position, mCurrentCheck);
            holder.checkbox.setVisibility(View.VISIBLE);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        // init current check
        if (position == mCurrentCheck) ((ViewHolder) holder).checkbox.setVisibility(View.VISIBLE);

        initThemeName((ViewHolder) holder, position);

        initThemeColor((ViewHolder) holder, position);

        ImageLoader.load(mContext, new DefaultOption.Builder(mList.get(position))
                .placeholder(R.color.colorBlue)
                .crossFade(50)
                .into(((ViewHolder) holder).imageView));
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
            case WHITE:
                themeName = "WHITE";
                break;
            case BLACK:
                themeName = "BLACK";
                break;
            case GREY:
                themeName = "GREY";
                break;
            case GREEN:
                themeName = "GREEN";
                break;
            case RED:
                themeName = "RED";
                break;
            case PINK:
                themeName = "PINK";
                break;
            case BLUE:
                themeName = "BLUE";
                break;
            case PURPLE:
                themeName = "PURPLE";
                break;
            case ORANGE:
                themeName = "ORANGE";
                break;
            case JUST_LIKE:
                themeName = "JUST LIKE";
                break;
        }
        holder.themeName.setText(themeName);
    }

    private void initThemeColor(ViewHolder holder, int position) {
        // 初始化主题颜色
        switch (position) {
            case WHITE:
                holder.background
                        .setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimaryWhite)));
                Context context = holder.itemView.getContext();
                holder.check.setColorFilter(context.getResources().getColor(R.color.colorAccentWhite));
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
            case ORANGE:
                holder.background
                        .setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimaryOrange)));
                break;
            case JUST_LIKE:
                holder.background
                        .setImageDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.colorPrimary)));
                break;
        }
    }
}
