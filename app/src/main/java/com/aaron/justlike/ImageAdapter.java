package com.aaron.justlike;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<Image> mImageList;
    private MainActivity mActivity;
    private static final int DELETE_PHOTO = 3;

    ImageAdapter(MainActivity activity, List<Image> imageList) {
        mActivity = activity;
        mImageList = imageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item, parent, false);
        /*
         * 为子项设置点击监听
         */
        final ViewHolder holder = new ViewHolder(view);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Image image = mImageList.get(position);
                Intent intent = new Intent(mActivity, DisplayImageActivity.class);
                // 将 Image 对象序列化传递给下一个活动，方便下一个活动取值
                intent.putExtra("image", image);
                intent.putExtra("position", position);
                mActivity.startActivityForResult(intent, DELETE_PHOTO);
            }
        });
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("Warning")
                        .setMessage("确定删除图片吗？")
                        .setIcon(R.mipmap.ic_warn)
                        .setCancelable(false)
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int position = holder.getAdapterPosition();
                                Uri uri = mImageList.get(position).getUri();
                                String absolutePath = FileUtils.getAbsolutePath(uri.getPath());
                                String fileName = absolutePath.substring(absolutePath.lastIndexOf("/"));
                                mImageList.remove(position);
                                MainActivity.getUriList().remove(position);
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
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image image = mImageList.get(position); // 从集合中找到 Image 对象
        /*
         * 使用 Picasso 图片加载库来加载图片
         */
        Picasso.get()
                .load(image.getUri()) // 加载数据源
                .resize(600, 600) // 压缩图片
                .onlyScaleDown() // 设置 resize() 方法仅在图片分辨率超过参数值才起作用
                // 将某些方向不正的图片旋转回来
                .rotate(getRotateDegree(image, MainActivity.isClick()))
                /* 由于 ImageView 已经指定 centerCrop,
                   所以这里指定成 centerInside 后图片便不会因为拉伸而变形。*/
                .centerInside()
                .into(holder.imageImage);
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
        String path = image.getUri().getPath(); // 获取图片原始路径
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
                String absolutePath = path.substring(image.getUri().getPath().indexOf("/s"));
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
        View imageView;
        ImageView imageImage;
        /**
         * @param view 子项布局的最外层布局，即父布局。
         */
        ViewHolder(View view) {
            super(view);
            imageView = view;
            imageImage = view.findViewById(R.id.image_image);
        }
    }
}
