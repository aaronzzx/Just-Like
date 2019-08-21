package com.aaron.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aaron.ui.R;
import com.aaron.ui.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopBar extends FrameLayout {

    private static final float DEFAULT_TITLE_SIZE = 18;
    private static final int DEFAULT_TITLE_STYLE = Typeface.BOLD;
    private static final int DEFAULT_TITLE_COLOR = Color.parseColor("#DE000000"); // black
    private static final int DEFAULT_BACK_VISIBILITY = View.GONE;
    private static final int DEFAULT_CLOSE_VISIBILITY = View.GONE;

    @BindView(R2.id.ui_toolbar)
    Toolbar mToolbar;
    @BindView(R2.id.ui_tv_title)
    TextView mTvTitle;
    @BindView(R2.id.ui_btn_back)
    Button mBtnBack;
    @BindView(R2.id.ui_btn_close)
    Button mBtnClose;

    private CharSequence mText;
    private float mTextSize;
    private int mTextStyle;
    private int mTextColor;
    private Drawable mBackIcon;
    private int mBackVisibility;
    private int mCloseVisibility;
    private OnBackTapListener mOnBackTapListener;
    private OnCloseTapListener mOnCloseTapListener;
    private OnTapListener mOnTapListener;

    public TopBar(@NonNull Context context) {
        this(context, null);
    }

    public TopBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            if (!(context instanceof AppCompatActivity)) {
                throw new IllegalArgumentException("Context must be AppCompatActivity.");
            }
        }
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.ui_topbar, this, true);
        ButterKnife.bind(rootView);
        init(context, attrs);
    }

    public void setBackIcon(Drawable icon) {
        mBackIcon = icon;
        mBtnBack.setBackground(mBackIcon);
    }

    public void setText(CharSequence text) {
        mText = text;
        mTvTitle.setText(mText);
        postInvalidate();
    }

    public void setText(@StringRes int textId) {
        mText = getResources().getString(textId);
        mTvTitle.setText(mText);
    }

    public void setTextColor(int color) {
        mTextColor = color;
        mTvTitle.setTextColor(mTextColor);
    }

    public void showBack(boolean flag) {
        if (flag) {
            mBackVisibility = VISIBLE;
        } else {
            mBackVisibility = GONE;
        }
        mBtnBack.setVisibility(mBackVisibility);
    }

    public void showClose(boolean flag) {
        if (flag) {
            mCloseVisibility = VISIBLE;
        } else {
            mCloseVisibility = GONE;
        }
        mBtnClose.setVisibility(mCloseVisibility);
    }

    public void setOnBackTapListener(OnBackTapListener listener) {
        mOnBackTapListener = listener;
    }

    public void setOnCloseTapListener(OnCloseTapListener listener) {
        mOnCloseTapListener = listener;
    }

    public void setOnTapListener(OnTapListener listener) {
        mOnTapListener = listener;
    }

    public void setOnMenuTapListener(@MenuRes int resId, Toolbar.OnMenuItemClickListener listener) {
        mToolbar.inflateMenu(resId);
        mToolbar.setOnMenuItemClickListener(listener);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBar);
        mText = ta.getString(R.styleable.TopBar_string);
        mTextSize = ta.getDimension(R.styleable.TopBar_stringSize, DEFAULT_TITLE_SIZE);
        mTextStyle = ta.getInteger(R.styleable.TopBar_stringStyle, DEFAULT_TITLE_STYLE);
        mTextColor = ta.getColor(R.styleable.TopBar_stringColor, DEFAULT_TITLE_COLOR);
        mBackIcon = ta.getDrawable(R.styleable.TopBar_backIcon);
        mBackVisibility = ta.getInteger(R.styleable.TopBar_backVisibility, DEFAULT_BACK_VISIBILITY);
        mCloseVisibility = ta.getInteger(R.styleable.TopBar_closeVisibility, DEFAULT_CLOSE_VISIBILITY);
        ta.recycle();

        mTvTitle.setText(mText);
        mTvTitle.setTextSize(mTextSize);
        mTvTitle.setTextColor(mTextColor);
        mTvTitle.setTypeface(Typeface.defaultFromStyle(mTextStyle));
        if (mBackIcon != null) mBtnBack.setBackground(mBackIcon);
        mBtnBack.setVisibility(mBackVisibility);
        mBtnClose.setVisibility(mCloseVisibility);

        if (isInEditMode()) return;
        AppCompatActivity activity = (AppCompatActivity) getContext();
        activity.setSupportActionBar(mToolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);
        // button init
        mBtnBack.setOnClickListener(view -> {
            if (mOnBackTapListener == null) {
                activity.onBackPressed();
            } else {
                mOnBackTapListener.onBackClick(view);
            }
        });
        mBtnClose.setOnClickListener(view -> {
            if (mOnCloseTapListener == null) {
                activity.finish();
            } else {
                mOnCloseTapListener.onCloseClick(view);
            }
        });
        mToolbar.setOnClickListener(v -> {
            if (mOnTapListener != null) {
                mOnTapListener.onTap(v);
            }
        });
    }

    public interface OnBackTapListener {
        void onBackClick(View view);
    }

    public interface OnCloseTapListener {
        void onCloseClick(View view);
    }

    public interface OnTapListener {
        void onTap(View v);
    }
}
