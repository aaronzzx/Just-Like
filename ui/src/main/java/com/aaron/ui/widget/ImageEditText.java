package com.aaron.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.aaron.ui.R;

@SuppressLint("AppCompatCustomView")
public class ImageEditText extends EditText {

    // left
    private Drawable mDrawable;
    private int mDrawableTint;
    private int mDrawableWidth;
    private int mDrawableHeight;

    public ImageEditText(Context context) {
        this(context, null);
    }

    public ImageEditText(Context context, @Nullable AttributeSet attrs) {
        // 自定义 EditText ，如果要递归调用本类构造，defStyleAttr 必须为 android.R.attr.editTextStyle ，
        // 否则点击无法响应，也就无法输入了
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ImageEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mDrawableWidth != -1 && mDrawableHeight != -1) {
            mDrawable.setBounds(0, 0, mDrawableWidth, mDrawableHeight);

        } else {
            setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, null, null);
            return;
        }
        setCompoundDrawables(mDrawable, null, null, null);
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    public void setDrawable(int drawableRes) {
        mDrawable = getResources().getDrawable(drawableRes);
    }

    public int getDrawableTint() {
        return mDrawableTint;
    }

    public void setDrawableTint(int drawableTint) {
        mDrawableTint = drawableTint;
    }

    public int getDrawableWidth() {
        return mDrawableWidth;
    }

    public void setDrawableWidth(int drawableWidth) {
        mDrawableWidth = drawableWidth;
    }

    public int getDrawableHeight() {
        return mDrawableHeight;
    }

    public void setDrawableHeight(int drawableHeight) {
        mDrawableHeight = drawableHeight;
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ImageEditText);
        // left
        mDrawable = ta.getDrawable(R.styleable.ImageEditText_drawable);
        mDrawableTint = ta.getColor(R.styleable.ImageEditText_drawableTint, -1);
        mDrawableWidth = (int) ta.getDimension(R.styleable.ImageEditText_drawableWidth, -1);
        mDrawableHeight = (int) ta.getDimension(R.styleable.ImageEditText_drawableHeight, -1);
        ta.recycle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mDrawable != null && mDrawableTint != -1) {
                mDrawable.setTint(mDrawableTint);
            }
        }
    }
}
