package com.aaron.justlike.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aaron.base.image.DefaultOption;
import com.aaron.base.image.ImageLoader;
import com.aaron.base.image.ScaleType;
import com.aaron.base.impl.OnClickListenerImpl;
import com.aaron.justlike.R;
import com.aaron.justlike.common.adapter.SquareAdapter;
import com.aaron.justlike.common.bean.Image;
import com.aaron.justlike.common.event.PreviewEvent;
import com.aaron.justlike.main.preview.PreviewActivity;
import com.aaron.ui.util.DialogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

class MainAdapter extends SquareAdapter {

    MainAdapter(List<Image> list) {
        super(list);
    }

    @Override
    protected void createHolder(SquareAdapter.ViewHolder holder, ViewGroup parent, int viewType) {
        // for Image onClick()
        holder.itemView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            EventBus.getDefault().postSticky(new PreviewEvent<>(PreviewEvent.FROM_MAIN_ACTIVITY, position, mList));
            mContext.startActivity(new Intent(mContext, PreviewActivity.class));
            ((Activity) mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // for Image onLongClick()
        holder.itemView.setOnLongClickListener(v -> {
            int position = holder.getAdapterPosition();
            View dialogView = LayoutInflater.from(mContext)
                    .inflate(R.layout.app_dialog_normal_alert, null);
            TextView tvTitle = dialogView.findViewById(R.id.app_tv_title);
            TextView tvContent = dialogView.findViewById(R.id.app_tv_content);
            Button btnLeft = dialogView.findViewById(R.id.app_btn_left);
            Button btnRight = dialogView.findViewById(R.id.app_btn_right);
            tvTitle.setText(R.string.app_notice);
            tvContent.setText(R.string.app_delete_image_forever);
            btnLeft.setText(R.string.app_cancel);
            btnRight.setText(R.string.app_confirm);
            int colorAccent = ((MainActivity) mContext).getColorAccent();
            btnRight.setTextColor(mContext.getResources().getColor(colorAccent));
            Dialog dialog = DialogUtil.createDialog(mContext, dialogView);
            btnLeft.setOnClickListener(new OnClickListenerImpl() {
                @Override
                public void onViewClick(View v, long interval) {
                    dialog.dismiss();
                }
            });
            btnRight.setOnClickListener(new OnClickListenerImpl() {
                @Override
                public void onViewClick(View v, long interval) {
                    dialog.dismiss();
                    String path = mList.get(position).getPath();
                    mList.remove(position);
                    notifyDataSetChanged();
                    ((IMainCommunicable) mContext).onDelete(path, mList.size() == 0);
                }
            });
            dialog.show();
            return true;
        });
    }

    @Override
    protected void bindHolder(SquareAdapter.ViewHolder holder, int position) {
        Image image = mList.get(position); // 从集合中找到 Image 对象
        String path = image.getPath();

        ImageLoader.load(mContext, new DefaultOption.Builder(path)
                .placeholder(R.color.colorBlue)
                .scaleType(ScaleType.CENTER_CROP)
                .into(holder.squareView));
    }
}
