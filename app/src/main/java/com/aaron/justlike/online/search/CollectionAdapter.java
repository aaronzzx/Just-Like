package com.aaron.justlike.online.search;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
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

import com.aaron.base.image.DefaultOption;
import com.aaron.base.image.ImageLoader;
import com.aaron.base.image.LoadListener;
import com.aaron.justlike.R;
import com.aaron.justlike.common.http.unsplash.entity.collection.Collection;
import com.aaron.justlike.common.widget.ViewWrapper;

import java.util.List;

class CollectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 1;

    private Context mContext;

    private List<Collection> mCollectionList;
    private SparseBooleanArray mAnimatedFlag = new SparseBooleanArray();

    CollectionAdapter(List<Collection> collectionList) {
        mCollectionList = collectionList;
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position == getItemCount() - 1) {
//            return TYPE_FOOTER;
//        } else {
//            return TYPE_NORMAL;
//        }
//    }

    void clearAnimatedFlag() {
        mAnimatedFlag.clear();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        /*if (viewType == TYPE_FOOTER) {
            return new FooterViewHolder(inflater.inflate(R.layout.activity_online_footer, parent, false));
        } else */if (viewType == TYPE_NORMAL) {
            View view = inflater.inflate(R.layout.fragment_collection_recycler_item, parent, false);
            // 为子项设置点击监听
            ViewHolder holder = new ViewHolder(view);
            holder.itemView.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();
                Collection collection = mCollectionList.get(position);
                Intent intent = new Intent(mContext, ElementsActivity.class);
                intent.putExtra("id", collection.getId());
                intent.putExtra("title", collection.getTitle());
                mContext.startActivity(intent);
//                ((Activity) mContext).overridePendingTransition(R.anim.activity_slide_in, android.R.anim.fade_out);
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
            String total = collection.getTotalPhotos() + " 照片";
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
            ImageLoader.load(mContext, new DefaultOption.Builder(photo)
                    .placeholder(placeHolder)
                    .crossFade(300)
                    .addListener(new LoadListener() {
                        @Override
                        public boolean onSuccess(Object resource) {
                            // 仅对初次加载的图片做饱和度动画，意味着刷新
                            if (!mAnimatedFlag.get(position, false)) {
                                Animator animator = ObjectAnimator.ofFloat(new ViewWrapper(((ViewHolder) holder).itemImage), "saturation", 0, 1);
                                animator.setDuration(2000).setInterpolator(new AccelerateDecelerateInterpolator());
                                animator.start();
                                mAnimatedFlag.put(position, true);
                            }
                            return false;
                        }

                        @Override
                        public boolean onFailure(Throwable e) {
                            return false;
                        }
                    })
                    .into(((ViewHolder) holder).itemImage));

            ImageLoader.load(mContext, new DefaultOption.Builder(authorImage)
                    .into(((ViewHolder) holder).authorImage));
        }
    }

    @Override
    public int getItemCount() {
        return mCollectionList.size();
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
}
