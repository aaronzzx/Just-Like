package com.aaron.justlike.adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaron.justlike.util.FileUtils;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.R;
import com.aaron.justlike.extend.SquareView;
import com.aaron.justlike.activity.DisplayImageActivity;
import com.aaron.justlike.activity.MainActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private boolean mBanClick;
    private List<Image> mImageList;
    private MainActivity mActivity;
    private static final int DELETE_PHOTO = 3;

    public ImageAdapter(MainActivity activity, List<Image> imageList) {
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
                    Image image = mImageList.get(position);

                    Intent intent = new Intent(mActivity, DisplayImageActivity.class);
                    // 将 Image 对象序列化传递给下一个活动，方便下一个活动取值
                    intent.putExtra("image", image);
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
                                    MainActivity.getPathList().remove(position);
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
                .placeholder(R.drawable.place_holder)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();
        /*DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory
                .Builder(100)
                .setCrossFadeEnabled(true).build();*/
        Glide.with(mActivity)
                .load(path)
                .thumbnail(0.1F)
                .apply(options)
//                .transition(DrawableTransitionOptions.with(factory))
                .into(holder.squareView);
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }


    /**
     * 获取需要旋转的角度，将返回的值给 Picasso 的 rotate() 方法
     *
     * @param image   Image对象
     * @param isClick 通过点击判断路径来源
     * @return 返回需要旋转的角度
     */
    private int getRotateDegree(Image image, boolean isClick) {
        int degree;
        String path = image.getPath(); // 获取图片原始路径
        /*
         * 判断是点击添加还是缓存加载
         */
        if (isClick) {
            // 如果路径是 /e 开头则是 FileProvider 返回
            if (path.startsWith("/e")) {
                String absolutePath = path.replace("/external_files",
                        "/storage/emulated/0");
                degree = FileUtils.getBitmapDegree(absolutePath);
            } else { // 否则路径正常截取
                String absolutePath = path.substring(image.getPath().indexOf("/s"));
                degree = FileUtils.getBitmapDegree(absolutePath);
            }
        } else { // 由于缓存的文件通过 FileProvider 提供路径，所以需要自己修改成绝对路径
            String fileName = path.substring(path.lastIndexOf("/"));
            String absolutePath = "/storage/emulated/0/Android/data/com.aaron.justlike/cache"
                    + fileName;
            degree = FileUtils.getBitmapDegree(absolutePath);
        }
        return degree;
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
