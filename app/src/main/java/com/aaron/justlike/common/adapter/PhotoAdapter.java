package com.aaron.justlike.common.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.justlike.R;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;

import java.util.List;

/**
 * @author Aaron aaronzheng9603@gmail.com
 */
public abstract class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

//    private static final int TYPE_NORMAL = 0;
//    private static final int TYPE_FOOTER = 1;

    protected Context mContext;

    protected List<Photo> mPhotoList;
    protected SparseBooleanArray mAnimatedFlag = new SparseBooleanArray();

    public PhotoAdapter(List<Photo> photoList) {
        mPhotoList = photoList;
    }

    public void clearAnimatedFlag() {
        mAnimatedFlag.clear();
    }

//    public boolean isFooterView(int position) {
//        return position == getItemCount() - 1;
//    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position == getItemCount() - 1) {
//            return TYPE_FOOTER;
//        } else {
//            return TYPE_NORMAL;
//        }
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
//        if (viewType == TYPE_FOOTER) {
//            return new FooterViewHolder(layoutInflater.inflate(R.layout.activity_online_footer, parent, false));
//        }
        View view = layoutInflater.inflate(R.layout.activity_online_recycler_item, parent, false);
        // 为子项设置点击监听
        final ViewHolder holder = new ViewHolder(view);
        createHolder(holder, parent, viewType);
        return holder;
    }

    protected abstract void createHolder(ViewHolder holder, ViewGroup parent, int viewType);

    protected abstract void bindHolder(ViewHolder holder, int position);

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder && !mPhotoList.isEmpty()) {
            bindHolder((ViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

//    protected static class FooterViewHolder extends RecyclerView.ViewHolder {
//        public FooterViewHolder(View itemView) {
//            super(itemView);
//        }
//    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public ImageView imageView;
        public ImageView placeHolder;
        public ImageView authorImage;
        public TextView authorName;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            imageView = view.findViewById(R.id.image_view);
            placeHolder = view.findViewById(R.id.place_holder);
            authorImage = view.findViewById(R.id.author_image);
            authorName = view.findViewById(R.id.author_name);
        }
    }
}
