package com.aaron.justlike.adapter;

import android.content.Context;
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
        ViewHolder holder = new ViewHolder(view);
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
        TextView libraryName;
        TextView libraryAuthor;
        TextView libraryDetails;
        /**
         * @param view 子项布局的最外层布局，即父布局。
         */
        ViewHolder(View view) {
            super(view);
            libraryName = view.findViewById(R.id.activity_about_library_recycler_item_name);
            libraryAuthor = view.findViewById(R.id.activity_about_library_recycler_item_author);
            libraryDetails = view.findViewById(R.id.activity_about_library_recycler_item_details);
        }
    }
}
