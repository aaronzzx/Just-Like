package com.aaron.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aaron.ui.R;
import com.aaron.ui.R2;
import com.blankj.utilcode.util.ConvertUtils;

import net.cachapa.expandablelayout.ExpandableLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CollapseLayout extends FrameLayout {

    private static final int DEFAULT_COLLAPSE_HEIGHT = ConvertUtils.dp2px(60);
    private static final int DEFAULT_COLLAPSE_PADDING = ConvertUtils.dp2px(16);
    private static final int DEFAULT_TITLE_COLOR = Color.parseColor("#DE000000"); // black
    private static final int DEFAULT_TITLE_SIZE = ConvertUtils.sp2px(14);
    private static final int DEFAULT_TITLE_STYLE = Typeface.NORMAL;
    private static final int DEFAULT_DRAWABLE_SIZE = ConvertUtils.dp2px(24);
    private static final int DEFAULT_DRAWABLE_LEFT_PADDING = ConvertUtils.dp2px(4);
    private static final int DEFAULT_DRAWABLE_COLOR = Color.parseColor("#FFAAAAAA"); // grey
    private static final int DEFAULT_DURATION = 300;
    private static final boolean DEFAULT_EXPANDED = false;

    @BindView(R2.id.ui_ll_title)
    LinearLayout mLlTitle;
    @BindView(R2.id.ui_tv_title)
    ImageTextView mItvTitle;
    @BindView(R2.id.ui_iv_expand)
    ImageView mIvExpand;
    @BindView(R2.id.ui_expandable_layout)
    ExpandableLayout mExpandableLayout;

    private int mCollapseHeight;
    private int mCollapsePadding;
    private int mCollapsePaddingLeft;
    private int mCollapsePaddingTop;
    private int mCollapsePaddingRight;
    private int mCollapsePaddingBottom;
    private String mTitle;
    private int mTitleColor;
    private float mTitleSize;
    private int mTitleStyle;
    private Drawable mDrawableLeft;
    private int mDrawableLeftWidth;
    private int mDrawableLeftHeight;
    private int mDrawableLeftPadding;
    private int mDrawableColor;
    private int mDuration;
    private boolean mExpanded;

    public CollapseLayout(Context context) {
        this(context, null);
    }

    public CollapseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.ui_collapse_layout, this, true);
        ButterKnife.bind(rootView);
        init(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mExpandableLayout.getChildCount() == 0) {
            // getChildAt(0) 是获取根布局，即目前此自定义 View - FrameLayout
            View child = getChildAt(1);
            this.removeView(child);
            mExpandableLayout.addView(child);
        }
    }

    public void setTitle(String title) {
        mTitle = title;
        mItvTitle.setText(mTitle);
    }

    public void collapse(boolean animate) {
        mExpandableLayout.collapse(animate);
        if (animate) {
            mIvExpand.animate()
                    .setDuration(mDuration)
                    .rotation(0)
                    .start();
        } else {
            mIvExpand.setRotation(0);
        }
        mExpanded = false;
    }

    public void expand(boolean animate) {
        mExpandableLayout.expand(animate);
        if (animate) {
            mIvExpand.animate()
                    .setDuration(mDuration)
                    .rotation(180)
                    .start();
        } else {
            mIvExpand.setRotation(180);
        }
        mExpanded = true;
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CollapseLayout);
        mCollapseHeight = (int) ta.getDimension(R.styleable.CollapseLayout_collapseHeight, DEFAULT_COLLAPSE_HEIGHT);
        mCollapsePadding = (int) ta.getDimension(R.styleable.CollapseLayout_collapsePadding, 0);
        mCollapsePaddingLeft = (int) ta.getDimension(R.styleable.CollapseLayout_collapsePaddingLeft, DEFAULT_COLLAPSE_PADDING);
        mCollapsePaddingTop = (int) ta.getDimension(R.styleable.CollapseLayout_collapsePaddingTop, DEFAULT_COLLAPSE_PADDING);
        mCollapsePaddingRight = (int) ta.getDimension(R.styleable.CollapseLayout_collapsePaddingRight, DEFAULT_COLLAPSE_PADDING);
        mCollapsePaddingBottom = (int) ta.getDimension(R.styleable.CollapseLayout_collapsePaddingBottom, DEFAULT_COLLAPSE_PADDING);
        mTitle = ta.getString(R.styleable.CollapseLayout_title);
        mTitleColor = ta.getColor(R.styleable.CollapseLayout_titleColor, DEFAULT_TITLE_COLOR);
        mTitleSize = ta.getDimension(R.styleable.CollapseLayout_titleSize, DEFAULT_TITLE_SIZE);
        mTitleStyle = ta.getInteger(R.styleable.CollapseLayout_titleStyle, DEFAULT_TITLE_STYLE);
        mDrawableLeft = ta.getDrawable(R.styleable.CollapseLayout_drawableLeft);
        mDrawableLeftWidth = (int) ta.getDimension(R.styleable.CollapseLayout_drawableLeftWidth, DEFAULT_DRAWABLE_SIZE);
        mDrawableLeftHeight = (int) ta.getDimension(R.styleable.CollapseLayout_drawableLeftHeight, DEFAULT_DRAWABLE_SIZE);
        mDrawableLeftPadding = (int) ta.getDimension(R.styleable.CollapseLayout_drawableLeftPadding, DEFAULT_DRAWABLE_LEFT_PADDING);
        mDrawableColor = ta.getColor(R.styleable.CollapseLayout_drawableColor, DEFAULT_DRAWABLE_COLOR);
        mDuration = ta.getInteger(R.styleable.CollapseLayout_duration, DEFAULT_DURATION);
        mExpanded = ta.getBoolean(R.styleable.CollapseLayout_expanded, DEFAULT_EXPANDED);
        ta.recycle();

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLlTitle.getLayoutParams();
        lp.height = mCollapseHeight;
        mLlTitle.setLayoutParams(lp);
        if (mCollapsePadding == 0) {
            mLlTitle.setPadding(mCollapsePaddingLeft,
                    mCollapsePaddingTop,
                    mCollapsePaddingRight,
                    mCollapsePaddingBottom);
        } else {
            mLlTitle.setPadding(mCollapsePadding, mCollapsePadding, mCollapsePadding, mCollapsePadding);
        }
        mItvTitle.setText(mTitle);
        mItvTitle.setTypeface(Typeface.defaultFromStyle(mTitleStyle));
        mItvTitle.setTextColor(mTitleColor);
        mItvTitle.setTextSize(ConvertUtils.px2sp(mTitleSize));
        if (mDrawableLeft != null) {
            mItvTitle.setIconLeft(mDrawableLeft);
            mItvTitle.setIconLeftWidth(mDrawableLeftWidth);
            mItvTitle.setIconLeftHeight(mDrawableLeftHeight);
            mItvTitle.setCompoundDrawablePadding(mDrawableLeftPadding);
            mItvTitle.setIconLeftTint(mDrawableColor);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable d = mIvExpand.getDrawable();
            d.setTint(mDrawableColor);
            mIvExpand.setImageDrawable(d);
        }
        mExpandableLayout.setDuration(mDuration);
        mExpandableLayout.setExpanded(mExpanded);

        mLlTitle.setOnClickListener(view -> {
            if (mExpandableLayout.isExpanded()) {
                mExpandableLayout.collapse(true);
                mIvExpand.animate()
                        .setDuration(mDuration)
                        .rotation(0)
                        .start();

            } else {
                mExpandableLayout.expand(true);
                mIvExpand.animate()
                        .setDuration(mDuration)
                        .rotation(180)
                        .start();
            }
        });
    }
}
