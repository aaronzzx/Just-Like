package com.aaron.justlike.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.activity.OnlineWallpaperActivity;
import com.aaron.justlike.another.Splash;
import com.aaron.justlike.util.SystemUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.kc.unsplash.models.Photo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OnlineImageAdapter extends RecyclerView.Adapter<OnlineImageAdapter.ViewHolder> {

    private List<Splash> mSplashList;
    private OnlineWallpaperActivity mActivity;
    private int[] placeHolders = {R.drawable.place_holder_1,
            R.drawable.place_holder_2, R.drawable.place_holder_3,
            R.drawable.place_holder_4, R.drawable.place_holder_5,
            R.drawable.place_holder_6, R.drawable.place_holder_7,
            R.drawable.place_holder_8, R.drawable.place_holder_9,
            R.drawable.place_holder_10};

    public OnlineImageAdapter(OnlineWallpaperActivity activity, List<Splash> splashList) {
        mActivity = activity;
        mSplashList = splashList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_online_recycler_item, parent, false);
        /*
         * 为子项设置点击监听
         */
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    int position = holder.getAdapterPosition();
                    // 将 Image 对象序列化传递给下一个活动，方便下一个活动取值
//                    Intent intent = new Intent(mActivity, );
//                    intent.putExtra("position", position);
//                    mActivity.startActivity(intent);
//                    mActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Splash splash = mSplashList.get(position);
        Photo photo = splash.getPhoto();
        String authorName = photo.getUser().getName();
        String authorImage = photo.getUser().getProfileImage().getMedium();
        String urls = photo.getUrls().getRaw() + "&fm=jpg&w=600&q=75";
        Glide.with(mActivity)
                .load(authorImage)
                .into(holder.authorImage);
        holder.authorName.setText(authorName);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        DrawableCrossFadeFactory factory = new DrawableCrossFadeFactory
                .Builder(300)
                .setCrossFadeEnabled(true).build();
        Glide.with(mActivity)
                .load(urls)
//                .thumbnail(0.6F)
                .apply(options)
                .transition(DrawableTransitionOptions.with(factory))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mSplashList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView imageView;
        ImageView authorImage;
        TextView authorName;

        /**
         * @param view 子项布局的最外层布局，即父布局。
         */
        ViewHolder(View view) {
            super(view);
            itemView = view;
            imageView = view.findViewById(R.id.image_view);
            authorImage = view.findViewById(R.id.author_image);
            authorName = view.findViewById(R.id.author_name);
        }
    }
}
