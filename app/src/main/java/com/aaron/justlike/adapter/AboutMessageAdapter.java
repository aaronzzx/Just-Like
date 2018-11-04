package com.aaron.justlike.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.another.AboutMessage;
import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AboutMessageAdapter extends RecyclerView.Adapter<AboutMessageAdapter.ViewHolder> {

    private Context mContext;
    private List<AboutMessage> mAboutMessageList;

    public AboutMessageAdapter(Context context, List<AboutMessage>aboutMessageList) {
        mContext = context;
        mAboutMessageList = aboutMessageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_about_message_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AboutMessage aboutMessage = mAboutMessageList.get(position);
        Glide.with(mContext)
                .load(aboutMessage.getIconId())
                .into(holder.imageView);
        holder.text.setText(aboutMessage.getText());
    }

    @Override
    public int getItemCount() {
        return mAboutMessageList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView text;
        /**
         * @param view 子项布局的最外层布局，即父布局。
         */
        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.activity_about_recycler_item_image);
            text = view.findViewById(R.id.activity_about_recycler_item_text);
        }
    }
}