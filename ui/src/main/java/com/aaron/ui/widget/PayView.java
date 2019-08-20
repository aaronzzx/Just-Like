package com.aaron.ui.widget;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aaron.ui.R;
import com.aaron.ui.R2;
import com.aaron.ui.util.DialogUtil;
import com.aaron.ui.util.KeyboardUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PayView extends FrameLayout {

    @BindView(R2.id.ui_ibtn_close)
    ImageButton mIbtnClose;
    @BindView(R2.id.ui_pwd_input)
    PwdInputView mPwdInputView;
    @BindView(R2.id.ui_num_keyboard)
    NumKeyboard mNumKeyboard;
    @BindView(R2.id.ui_tv_title)
    TextView mTvTitle;
    @BindView(R2.id.ui_tv_notice)
    TextView mTvNotice;

    private Dialog mDialog;

    public static void show(Context context, OnTextListener listener) {
        PayView payView = new PayView(context);
        payView.setOnTextListener(text -> {
            if (text.length() == 6) listener.onTextChange(text);
        });
        payView.show();
    }

    public PayView(Context context) {
        this(context, null);
    }

    public PayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void setOnTextListener(NumKeyboard.OnTextListener listener) {
        mNumKeyboard.setTextListener(listener);
    }

    public void setTitle(CharSequence text) {
        mTvTitle.setText(text);
    }

    public void setNotice(CharSequence text) {
        mTvNotice.setText(text);
    }

    public void showNotice(boolean show) {
        if (show) {
            mTvNotice.setVisibility(View.VISIBLE);
            animate();
        } else {
            mTvNotice.setVisibility(View.INVISIBLE);
        }
    }

    private void animateNotice() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mTvNotice, "translationX", -50, 50, 0, 0);
        animator.setDuration(300);
        animator.setInterpolator(new BounceInterpolator());
        animator.start();
    }

    public void clear() {
        mPwdInputView.setText("");
        mNumKeyboard.clear();
    }

    public void show() {
        mDialog.show();
    }

    public void hide() {
        mDialog.dismiss();
    }

    public interface OnTextListener extends NumKeyboard.OnTextListener {

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(Context context) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.ui_pay_view, this, true);
        mDialog = DialogUtil.createBottomSheetDialog(context, rootView, true);
        mDialog.setCanceledOnTouchOutside(false);

        ButterKnife.bind(rootView);
        KeyboardUtil.setSoftInputEnabled(((Activity) context), mPwdInputView, false);
        mIbtnClose.setOnClickListener(v -> mDialog.dismiss());
        mNumKeyboard.init(mPwdInputView);
    }
}
