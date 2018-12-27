package com.aaron.justlike.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.MainActivity;
import com.aaron.justlike.activity.MainImageActivity;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.extend.SquareView;
import com.aaron.justlike.util.AnimationUtil;
import com.aaron.justlike.util.FileUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private static final int DELETE_PHOTO = 2;
    private boolean mBanClick;
    private List<Image> mImageList;
    private MainActivity mActivity;
    private int[] placeHolders = {R.drawable.place_holder_1,
            R.drawable.place_holder_2, R.drawable.place_holder_3,
            R.drawable.place_holder_4, R.drawable.place_holder_5,
            R.drawable.place_holder_6, R.drawable.place_holder_7,
            R.drawable.place_holder_8, R.drawable.place_holder_9,
            R.drawable.place_holder_10};

    public MainAdapter(MainActivity activity, List<Image> imageList) {
        mActivity = activity;
        mImageList = imageList;
    }

    public void setBanClick(boolean flag) {
        mBanClick = flag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_main_recycler_item, parent, false);
        /*
         * 为子项设置点击监听
         */
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBanClick) {
                    int position = holder.getAdapterPosition();
                    // 将 Image 对象序列化传递给下一个活动，方便下一个活动取值
                    Intent intent = new Intent(mActivity, MainImageActivity.class);
                    intent.putExtra("position", position);
                    mActivity.startActivityForResult(intent, DELETE_PHOTO);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mBanClick) {
                    new AlertDialog.Builder(mActivity)
                            .setTitle("删除图片")
                            .setMessage("图片将从设备中删除")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int position = holder.getAdapterPosition();
                                    String path = mImageList.get(position).getPath();
                                    mImageList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(0, mImageList.size() - 1);
                                    FileUtils.deleteFile(path);
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Image image = mImageList.get(position); // 从集合中找到 Image 对象
        String path = image.getPath();
        RequestOptions options = new RequestOptions()
                .placeholder(R.color.colorGrey)
                .priority(Priority.HIGH);
        Glide.with(mActivity)
                .load(path)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ColorMatrix matrix = new ColorMatrix();
                        matrix.setSaturation(0);
                        resource.setColorFilter(new ColorMatrixColorFilter(matrix));
                        holder.fakeView.setImageDrawable(resource);
                        AlphaAnimation aa = new AlphaAnimation(0.5F, 1);
                        aa.setDuration(250);
                        aa.setFillAfter(true);
                        holder.fakeView.startAnimation(aa);
                        return false;
                    }
                })
                .into(holder.fakeView);
        Glide.with(mActivity)
                .load(path)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.squareView.setImageDrawable(resource);
                        AnimationUtil.showViewByAlpha(holder.squareView, 0, 1, 600);
                        return false;
                    }
                })
                .into(holder.squareView);
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        SquareView squareView;
        SquareView fakeView;

        /**
         * @param view 子项布局的最外层布局，即父布局。
         */
        ViewHolder(View view) {
            super(view);
            itemView = view;
            squareView = view.findViewById(R.id.square_view);
            fakeView = view.findViewById(R.id.fake_view);
        }
    }
}
