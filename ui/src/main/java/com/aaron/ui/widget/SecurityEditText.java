package com.aaron.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.StringRes;

import com.aaron.ui.R;
import com.aaron.ui.R2;
import com.aaron.ui.util.KeyboardUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SecurityEditText extends FrameLayout {

    @BindView(R2.id.ui_pwd_input)
    PwdInputView mPwdInput;

    public SecurityEditText(Context context) {
        this(context, null);
    }

    public SecurityEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public EditText getEditText() {
        return mPwdInput;
    }

    public void setText(CharSequence text) {
        mPwdInput.setText(text);
    }

    public void setText(@StringRes int text) {
        mPwdInput.setText(text);
    }

    public void enableSoftInput(boolean enable) {
        KeyboardUtil.setSoftInputEnabled((Activity) getContext(), mPwdInput, enable);
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.ui_security_edit_text, this, true);
        ButterKnife.bind(rootView);
        KeyboardUtil.setSoftInputEnabled(((Activity) context), mPwdInput, false);
    }
}
