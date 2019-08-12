package com.aaron.justlike.online;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.justlike.R;
import com.aaron.justlike.common.http.glide.GlideApp;
import com.aaron.justlike.common.http.glide.request.Request;
import com.aaron.justlike.common.http.unsplash.entity.photo.Photo;
import com.aaron.justlike.common.widget.ViewWrapper;

import java.util.List;

public class OnlineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static final int TYPE_NORMAL = 0;
    protected static final int TYPE_FOOTER = 1;

    protected Context mContext;
    protected Callback<Photo> mCallback;

    protected List<Photo> mPhotoList;
    private SparseBooleanArray mAnimatedFlag = new SparseBooleanArray();

    public OnlineAdapter(List<Photo> photoList, Callback<Photo> callback) {
        mPhotoList = photoList;
        mCallback = callback;
    }

    public void clearAnimatedFlag() {
        mAnimatedFlag.clear();
    }

    public boolean isFooterView(int position) {
        return position == getItemCount() - 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(layoutInflater.inflate(R.layout.activity_online_footer, parent, false));
        } else if (viewType == TYPE_NORMAL) {
            View view = layoutInflater.inflate(R.layout.activity_online_recycler_item, parent, false);
            // 为子项设置点击监听
            final ViewHolder holder = new ViewHolder(view);
            holder.itemView.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                mCallback.onPress(mPhotoList.get(position));
            });
            return holder;
        }
        //noinspection ConstantConditions
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder && !mPhotoList.isEmpty()) {
            Photo photo = mPhotoList.get(position);
            String authorName = photo.getUser().getName();
            String authorImage = photo.getUser().getProfileImage().getLarge();
            String regular = photo.getUrls().getRegular();
            String color = photo.getColor();
            Drawable placeHolder = new ColorDrawable(Color.parseColor(color));

            // set author name
            ((ViewHolder) holder).authorName.setText(authorName);
            // load author image
            GlideApp.getInstance()
                    .with(mContext)
                    .asDrawable()
                    .load(authorImage)
                    .placeHolder(R.drawable.ic_author_image)
                    .into(((ViewHolder) holder).authorImage);
            // load image
            GlideApp.getInstance()
                    .with(mContext)
                    .asDrawable()
                    .load(regular)
                    .placeHolder(placeHolder)
                    .transition(300)
                    .listener(new Request.Listener<Drawable>() {
                        @Override
                        public void onLoadFailed() {

                        }

                        @Override
                        public void onResourceReady(Drawable resource, boolean isFirstResource) {
                            // 仅对初次加载的图片做饱和度动画，意味着刷新
                            if (!mAnimatedFlag.get(position, false)) {
                                Animator animator = ObjectAnimator.ofFloat(new ViewWrapper(((ViewHolder) holder).imageView), "saturation", 0, 1);
                                animator.setDuration(2000).setInterpolator(new AccelerateDecelerateInterpolator());
                                animator.start();
                                mAnimatedFlag.put(position, true);
                            }
                        }
                    })
                    .into(((ViewHolder) holder).imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size() + 1;
    }

    protected static class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView imageView;
        ImageView placeHolder;
        ImageView authorImage;
        TextView authorName;

        ViewHolder(View view) {
            super(view);
            itemView = view;
            imageView = view.findViewById(R.id.image_view);
            placeHolder = view.findViewById(R.id.place_holder);
            authorImage = view.findViewById(R.id.author_image);
            authorName = view.findViewById(R.id.author_name);
        }
    }

    public interface Callback<T> {

        void onPress(T t);
    }
}
