package com.aaron.justlike.app.about.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.app.about.entity.Library;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LibraryAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<T> mList;

    public LibraryAdapter(Context context, List<T> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_about_library_recycler_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            switch (holder.getAdapterPosition()) {
                case 0:
                    Intent glide = new Intent(Intent.ACTION_VIEW);
                    glide.setData(Uri.parse("https://github.com/bumptech/glide"));
                    mContext.startActivity(glide);
                    break;
                case 1:
                    Intent circleImageView = new Intent(Intent.ACTION_VIEW);
                    circleImageView.setData(Uri.parse("https://github.com/hdodenhof/CircleImageView"));
                    mContext.startActivity(circleImageView);
                    break;
                case 2:
                    Intent statusBarUtil = new Intent(Intent.ACTION_VIEW);
                    statusBarUtil.setData(Uri.parse("https://github.com/laobie/StatusBarUtil"));
                    mContext.startActivity(statusBarUtil);
                    break;
                case 3:
                    Intent photoView = new Intent(Intent.ACTION_VIEW);
                    photoView.setData(Uri.parse("https://github.com/bm-x/PhotoView"));
                    mContext.startActivity(photoView);
                    break;
                case 4:
                    Intent pictureSelector = new Intent(Intent.ACTION_VIEW);
                    pictureSelector.setData(Uri.parse("https://github.com/zhihu/Matisse"));
                    mContext.startActivity(pictureSelector);
                    break;
                case 5:
                    Intent uCrop = new Intent(Intent.ACTION_VIEW);
                    uCrop.setData(Uri.parse("https://github.com/Yalantis/uCrop"));
                    mContext.startActivity(uCrop);
                    break;
                case 6:
                    Intent androidUnsplash = new Intent(Intent.ACTION_VIEW);
                    androidUnsplash.setData(Uri.parse("https://github.com/KeenenCharles/AndroidUnplash"));
                    mContext.startActivity(androidUnsplash);
                    break;
                case 7:
                    Intent fab = new Intent(Intent.ACTION_VIEW);
                    fab.setData(Uri.parse("https://github.com/Clans/FloatingActionButton"));
                    mContext.startActivity(fab);
                    break;
                case 8:
                    Intent eventBus = new Intent(Intent.ACTION_VIEW);
                    eventBus.setData(Uri.parse("https://github.com/greenrobot/EventBus"));
                    mContext.startActivity(eventBus);
                    break;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Library library = (Library) mList.get(position);
        ((ViewHolder) holder).name.setText(library.getName());
        ((ViewHolder) holder).author.setText(library.getAuthor());
        ((ViewHolder) holder).introduce.setText(library.getIntroduce());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView name;
        TextView author;
        TextView introduce;

        /**
         * @param view 子项布局的最外层布局，即父布局。
         */
        ViewHolder(View view) {
            super(view);
            itemView = view;
            name = view.findViewById(R.id.activity_about_library_recycler_item_name);
            author = view.findViewById(R.id.activity_about_library_recycler_item_author);
            introduce = view.findViewById(R.id.activity_about_library_recycler_item_details);
        }
    }
}
