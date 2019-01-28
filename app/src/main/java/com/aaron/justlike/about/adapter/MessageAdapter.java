package com.aaron.justlike.about.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaron.justlike.R;
import com.aaron.justlike.another.AboutMessage;
import com.aaron.justlike.util.SystemUtils;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mActivity;
    private List<AboutMessage> mAboutMessageList;

    public MessageAdapter(Activity activity, List<AboutMessage> aboutMessageList) {
        mActivity = activity;
        mAboutMessageList = aboutMessageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_about_message_recycler_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            switch (holder.getAdapterPosition()) {
                case 0:
                    Intent introduce = new Intent(Intent.ACTION_VIEW);
                    introduce.setData(Uri.parse("https://www.jianshu.com/p/011e616982f4"));
                    mActivity.startActivity(introduce);
                    break;
                case 1:
                    int[] resolutionArray = SystemUtils.getResolution(mActivity.getWindowManager());
                    String subject = "Just Like for Android " + SystemUtils.getVersionName(mActivity) + "\n"
                            + "Feedback(" + Build.BRAND + "-" + Build.MODEL + ")";
                    String text = "请尽可能详细描述您的问题或建议，请不要删除或修改下列设备信息。" + "\n"
                            + "Device: " + Build.BRAND + "-" + Build.MODEL + "\n"
                            + "Android Version: " + Build.VERSION.RELEASE + "(SDK=" + Build.VERSION.SDK_INT + ")" + "\n"
                            + "Resolution: " + resolutionArray[1] + "*" + resolutionArray[0] + "\n"
                            + "System Language: " + Locale.getDefault().getLanguage() + "(" + Locale.getDefault().getCountry() + ")" + "\n"
                            + "App Version: " + SystemUtils.getVersionName(mActivity);
                    Intent sendEMail = new Intent(Intent.ACTION_SENDTO);
                    sendEMail.setData(Uri.parse("mailto:aaronzheng9603@gmail.com"));
                    sendEMail.putExtra(Intent.EXTRA_SUBJECT, subject);
                    sendEMail.putExtra(Intent.EXTRA_TEXT, text);
                    mActivity.startActivity(sendEMail);
                    break;
                case 2:
                    Intent sourceCode = new Intent(Intent.ACTION_VIEW);
                    sourceCode.setData(Uri.parse("https://github.com/AaronZheng9603/Just-Like"));
                    mActivity.startActivity(sourceCode);
                    break;
                case 3:
                    Intent github = new Intent(Intent.ACTION_VIEW);
                    github.setData(Uri.parse("https://github.com/AaronZheng9603"));
                    mActivity.startActivity(github);
                    break;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AboutMessage aboutMessage = mAboutMessageList.get(position);
        Glide.with(mActivity)
                .load(aboutMessage.getIconId())
                .into(holder.imageView);
        holder.text.setText(aboutMessage.getText());
    }

    @Override
    public int getItemCount() {
        return mAboutMessageList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView imageView;
        TextView text;

        /**
         * @param view 子项布局的最外层布局，即父布局。
         */
        ViewHolder(View view) {
            super(view);
            itemView = view;
            imageView = view.findViewById(R.id.activity_about_recycler_item_image);
            text = view.findViewById(R.id.activity_about_recycler_item_text);
        }
    }
}