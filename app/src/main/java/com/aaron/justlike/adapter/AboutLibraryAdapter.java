package com.aaron.justlike.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.another.AboutLibrary;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AboutLibraryAdapter extends RecyclerView.Adapter<AboutLibraryAdapter.ViewHolder> {

    private Context mContext;
    private List<AboutLibrary> mAboutLibraryList;

    public AboutLibraryAdapter(Context context, List<AboutLibrary> aboutLibraryList) {
        mContext = context;
        mAboutLibraryList = aboutLibraryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_about_library_recycler_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        pictureSelector.setData(Uri.parse("https://github.com/LuckSiege/PictureSelector"));
                        mContext.startActivity(pictureSelector);
                        break;
                    case 5:
                        Intent uCrop = new Intent(Intent.ACTION_VIEW);
                        uCrop.setData(Uri.parse("https://github.com/Yalantis/uCrop"));
                        mContext.startActivity(uCrop);
                        break;
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AboutLibrary aboutLibrary = mAboutLibraryList.get(position);
        holder.libraryName.setText(aboutLibrary.getLibraryName());
        holder.libraryAuthor.setText(aboutLibrary.getLibraryAuthor());
        holder.libraryDetails.setText(aboutLibrary.getLibraryDetails());
    }

    @Override
    public int getItemCount() {
        return mAboutLibraryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        TextView libraryName;
        TextView libraryAuthor;
        TextView libraryDetails;
        /**
         * @param view 子项布局的最外层布局，即父布局。
         */
        ViewHolder(View view) {
            super(view);
            itemView = view;
            libraryName = view.findViewById(R.id.activity_about_library_recycler_item_name);
            libraryAuthor = view.findViewById(R.id.activity_about_library_recycler_item_author);
            libraryDetails = view.findViewById(R.id.activity_about_library_recycler_item_details);
        }
    }
}
