package com.aaron.justlike.others.about;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.justlike.R;
import com.aaron.justlike.common.JustLike;
import com.aaron.justlike.common.bean.Message;
import com.aaron.justlike.common.util.AlipayUtil;
import com.aaron.justlike.common.util.SystemUtil;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

class AboutMessageAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int INTRODUCE = 0;
    private static final int FEEDBACK = 1;
    private static final int SOURCE_CODE = 2;
    private static final int GITHUB = 3;
    private static final int GIFT = 4;

    private Activity mActivity;
    private List<T> mList;

    AboutMessageAdapter(Activity activity, List<T> list) {
        mActivity = activity;
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_about_message_recycler_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            switch (holder.getAdapterPosition()) {
                case INTRODUCE:
                    Intent introduce = new Intent(Intent.ACTION_VIEW);
                    introduce.setData(Uri.parse("https://juejin.im/user/5c3f3b2b5188252580051f8c"));
                    mActivity.startActivity(introduce);
                    break;
                case FEEDBACK:
                    int[] widthHeightPixels = SystemUtil.getResolution(mActivity.getWindowManager());
                    String subject = "Just Like for Android " + SystemUtil.getVersionName(mActivity) + "\n"
                            + "Feedback(" + Build.BRAND + "-" + Build.MODEL + ")";
                    String text = "请尽可能详细描述您的问题或建议，请不要删除或修改下列设备信息。" + "\n"
                            + "Device: " + Build.BRAND + "-" + Build.MODEL + "\n"
                            + "Android Version: " + Build.VERSION.RELEASE + "(SDK=" + Build.VERSION.SDK_INT + ")" + "\n"
                            + "Resolution: " + widthHeightPixels[1] + "*" + widthHeightPixels[0] + "\n"
                            + "System Language: " + Locale.getDefault().getLanguage() + "(" + Locale.getDefault().getCountry() + ")" + "\n"
                            + "App Version: " + SystemUtil.getVersionName(mActivity);
                    Intent sendMail = new Intent(Intent.ACTION_SENDTO);
                    sendMail.setData(Uri.parse("mailto:aaronzheng9603@gmail.com"));
                    sendMail.putExtra(Intent.EXTRA_SUBJECT, subject);
                    sendMail.putExtra(Intent.EXTRA_TEXT, text);
                    mActivity.startActivity(sendMail);
                    break;
                case SOURCE_CODE:
                    Intent sourceCode = new Intent(Intent.ACTION_VIEW);
                    sourceCode.setData(Uri.parse("https://github.com/AaronZheng9603/Just-Like"));
                    mActivity.startActivity(sourceCode);
                    break;
                case GITHUB:
                    Intent github = new Intent(Intent.ACTION_VIEW);
                    github.setData(Uri.parse("https://github.com/AaronZheng9603"));
                    mActivity.startActivity(github);
                    break;
                case GIFT:
                    boolean isInstall = AlipayUtil.hasInstalledAlipayClient(mActivity);
                    if (isInstall) {
                        AlipayUtil.startAlipayClient(mActivity, "a7x09336v9f1fjutignso50");
                    } else {
                        Toast.makeText(JustLike.getContext(), "需要安装支付宝客户端", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mList.get(position);
        Glide.with(mActivity)
                .load(message.getIconId())
                .into(((ViewHolder) holder).icon);
        ((ViewHolder) holder).title.setText(message.getTitle());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView icon;
        TextView title;

        /**
         * @param view 子项布局的最外层布局，即父布局。
         */
        ViewHolder(View view) {
            super(view);
            itemView = view;
            icon = view.findViewById(R.id.activity_about_recycler_item_image);
            title = view.findViewById(R.id.activity_about_recycler_item_text);
        }
    }
}