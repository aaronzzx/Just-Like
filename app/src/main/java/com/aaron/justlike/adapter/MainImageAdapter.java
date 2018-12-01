package com.aaron.justlike.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.MainImageActivity;
import com.aaron.justlike.activity.MainActivity;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.extend.SquareView;
import com.aaron.justlike.util.FileUtils;
import com.aaron.justlike.util.SystemUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class MainImageAdapter extends RecyclerView.Adapter<MainImageAdapter.ViewHolder> {

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

    public MainImageAdapter(MainActivity activity, List<Image> imageList) {
        mActivity = activity;
        mImageList = imageList;
    }

    public void setBanClick(boolean flag) {
        mBanClick = flag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
                            .setTitle("Warning")
                            .setMessage("确定删除图片吗？")
                            .setCancelable(false)
                            .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int position = holder.getAdapterPosition();
                                    String path = mImageList.get(position).getPath();
                                    String fileName = path.substring(path.lastIndexOf("/"));
                                    mImageList.remove(position);
//                                    MainActivity.getPathList().remove(position);
                                    notifyDataSetChanged();
                                    FileUtils.deleteFile(mActivity, fileName);
                                    mActivity.addHintOnBackground();
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image image = mImageList.get(position); // 从集合中找到 Image 对象
        String path = image.getPath();
        RequestOptions options = new RequestOptions()
                .placeholder(placeHolders[SystemUtils.getRandomNum(9)]);
//                .dontAnimate()
//                .override(10)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .centerCrop();
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory
                .Builder(100)
                .setCrossFadeEnabled(true).build();
        Glide.with(mActivity)
                .load(path)
//                .thumbnail(0.1F)
                .apply(options)
                .transition(DrawableTransitionOptions.with(factory))
                .into(holder.squareView);
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        SquareView squareView;

        /**
         * @param view 子项布局的最外层布局，即父布局。
         */
        ViewHolder(View view) {
            super(view);
            itemView = view;
            squareView = view.findViewById(R.id.square_view);
        }
    }
}
