package com.aaron.justlike.adapter.online;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.http.unsplash.entity.collection.Collection;
import com.aaron.justlike.library.glide.GlideApp;
import com.aaron.justlike.ui.ViewWrapper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static final int TYPE_NORMAL = 0;
    protected static final int TYPE_FOOTER = 1;

    private Context mContext;

    private List<Collection> mCollectionList;
    private Callback<Collection> mCallback;

    public CollectionAdapter(List<Collection> collectionList, Callback<Collection> callback) {
        mCollectionList = collectionList;
        mCallback = callback;
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
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(inflater.inflate(R.layout.activity_online_footer, parent, false));
        } else if (viewType == TYPE_NORMAL) {
            View view = inflater.inflate(R.layout.fragment_collection_recycler_item, parent, false);
            // 为子项设置点击监听
            ViewHolder holder = new ViewHolder(view);
            holder.itemView.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                mCallback.onPress(mCollectionList.get(position));
            });
            return holder;
        }
        //noinspection ConstantConditions
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder && !mCollectionList.isEmpty()) {
            Collection collection = mCollectionList.get(position);
            String title = collection.getTitle();
            String total = String.valueOf(collection.getTotalPhotos()) + " 照片";
            String authorImage = collection.getUser().getProfileImage().getLarge();
            String authorName = collection.getUser().getName();
            String photo = collection.getCoverPhoto().getUrls().getRegular();
            String color = collection.getCoverPhoto().getColor();
            Drawable placeHolder = new ColorDrawable(Color.parseColor(color));

            // 设置标题信息
            ((ViewHolder) holder).imageTitle.setText(title);
            ((ViewHolder) holder).imageTotal.setText(total);
            ((ViewHolder) holder).authorName.setText(authorName);

            // 加载集合的封面图
            GlideApp.getInstance()
                    .with(mContext)
                    .asDrawable()
                    .load(photo)
                    .placeHolder(placeHolder)
                    .transition(300)
                    .into(((ViewHolder) holder).itemImage);

            GlideApp.getInstance()
                    .with(mContext)
                    .asDrawable()
                    .load(authorImage)
                    .into(((ViewHolder) holder).authorImage);
            Animator animator = ObjectAnimator.ofFloat(new ViewWrapper(((ViewHolder) holder).itemImage), "saturation", 0, 1);
            animator.setDuration(2000).setInterpolator(new AccelerateInterpolator());
            animator.start();
        }
    }

    @Override
    public int getItemCount() {
        return mCollectionList.size() + 1;
    }

    protected static class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView itemImage;
        TextView imageTitle;
        TextView imageTotal;
        ImageView authorImage;
        TextView authorName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemImage = itemView.findViewById(R.id.photo);
            imageTitle = itemView.findViewById(R.id.title);
            imageTotal = itemView.findViewById(R.id.count);
            authorImage = itemView.findViewById(R.id.author_image);
            authorName = itemView.findViewById(R.id.author_name);
        }
    }

    public interface Callback<T> {

        void onPress(T t);
    }
}
