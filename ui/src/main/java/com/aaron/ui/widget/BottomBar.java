package com.aaron.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.aaron.ui.R;
import com.blankj.utilcode.util.ConvertUtils;

public class BottomBar extends LinearLayout {

    private static final int COLOR_DIVIDER = Color.parseColor("#0A000000"); // black_divider_shadow
    private static final int COLOR_ITEM_DIVIDER = Color.parseColor("#1F000000"); // black_divider
    private static final int VISIBILITY_DIVIDER = VISIBLE;
    private static final int VISIBILITY_ITEM_DIVIDER = VISIBLE;
    private static final int PADDING_ROOT = ConvertUtils.dp2px(20);
    private static final int PADDING_ICON = ConvertUtils.dp2px(4);
    private static final int POSITION_LEFT = 1;
    private static final int POSITION_RIGHT = 2;
    private static final int POSITION_TOP = 3;
    private static final int POSITION_BOTTOM = 4;

    private View mViewDivider;
    private LinearLayout mLlBottomBar;
    private LinearLayout mLlLeft;
    private ImageTextView mItvLeft;
    private View mViewItemDivider;
    private LinearLayout mLlRight;
    private ImageTextView mItvRight;

    private int mDividerVisible;
    private Drawable mDividerBackground;
    private Drawable mRootBackground;
    private int mPaddingRoot;
    private String mLeftText;
    private Drawable mLeftIcon;
    private int mItemDividerVisible;
    private Drawable mItemDividerBackground;
    private String mRightText;
    private Drawable mRightIcon;
    private int mIconPadding;
    private int mIconTint;
    private int mIconPosition;
    private int mIconWidth;
    private int mIconHeight;

    private OnPressListener mPressListener;

    public BottomBar(Context context) {
        this(context, null);
    }

    public BottomBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttrs(context, attrs);
    }

    public void setOnPressListener(OnPressListener listener) {
        mPressListener = listener;
    }

    private void initView(Context context) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.ui_bottom_bar, this, true);
        mViewDivider = itemView.findViewById(R.id.ui_view_divider);
        mLlBottomBar = itemView.findViewById(R.id.ui_ll_bottom_bar);
        mLlLeft = itemView.findViewById(R.id.ui_ll_left);
        mItvLeft = itemView.findViewById(R.id.ui_itv_left);
        mViewItemDivider = itemView.findViewById(R.id.ui_view_divider_item);
        mLlRight = itemView.findViewById(R.id.ui_ll_right);
        mItvRight = itemView.findViewById(R.id.ui_itv_right);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BottomBar);
        mDividerVisible = ta.getInt(R.styleable.BottomBar_dividerVisible, VISIBILITY_DIVIDER);
        Drawable divider = ta.getDrawable(R.styleable.BottomBar_dividerBackground);
        mDividerBackground = divider != null ? divider : new ColorDrawable(COLOR_DIVIDER);
        mRootBackground = ta.getDrawable(R.styleable.BottomBar_rootBackground);
        mPaddingRoot = (int) ta.getDimension(R.styleable.BottomBar_paddingRoot, PADDING_ROOT);
        mLeftText = ta.getString(R.styleable.BottomBar_leftText);
        mLeftIcon = ta.getDrawable(R.styleable.BottomBar_leftIcon);
        mItemDividerVisible = ta.getInt(R.styleable.BottomBar_itemDividerVisible, VISIBILITY_ITEM_DIVIDER);
        Drawable itemDivider = ta.getDrawable(R.styleable.BottomBar_itemDividerBackground);
        mItemDividerBackground = itemDivider != null ? itemDivider : new ColorDrawable(COLOR_ITEM_DIVIDER);
        mRightText = ta.getString(R.styleable.BottomBar_rightText);
        mRightIcon = ta.getDrawable(R.styleable.BottomBar_rightIcon);
        mIconPadding = (int) ta.getDimension(R.styleable.BottomBar_iconPadding, PADDING_ICON);
        mIconTint = ta.getColor(R.styleable.BottomBar_iconTint, -1);
        mIconPosition = ta.getInt(R.styleable.BottomBar_iconPosition, POSITION_LEFT);
        mIconWidth = (int) ta.getDimension(R.styleable.BottomBar_iconWidth, -1);
        mIconHeight = (int) ta.getDimension(R.styleable.BottomBar_iconHeight, -1);
        ta.recycle();

        mViewDivider.setVisibility(mDividerVisible);
        mViewDivider.setBackground(mDividerBackground);
        mLlBottomBar.setBackground(mRootBackground);
        mLlLeft.setPadding(0, mPaddingRoot, 0, mPaddingRoot);
        mItvLeft.setText(mLeftText);
        mViewItemDivider.setVisibility(mItemDividerVisible);
        mViewItemDivider.setBackground(mItemDividerBackground);
        mLlRight.setPadding(0, mPaddingRoot, 0, mPaddingRoot);
        mItvRight.setText(mRightText);
        mItvLeft.setCompoundDrawablePadding(mIconPadding);
        mItvRight.setCompoundDrawablePadding(mIconPadding);
        initIcon();

        mLlLeft.setOnClickListener(v -> {
            if (mPressListener == null) return;
            mPressListener.onLeftPress(v);
        });
        mLlRight.setOnClickListener(v -> {
            if (mPressListener == null) return;
            mPressListener.onRightPress(v);
        });
    }

    private void initIcon() {
        switch (mIconPosition) {
            case POSITION_LEFT:
                mItvLeft.setIconLeftWidth(mIconWidth);
                mItvLeft.setIconLeftHeight(mIconHeight);
                mItvRight.setIconLeftWidth(mIconWidth);
                mItvRight.setIconLeftHeight(mIconHeight);
                mItvLeft.setIconLeftTint(mIconTint);
                mItvRight.setIconLeftTint(mIconTint);
                mItvLeft.setIconLeft(mLeftIcon);
                mItvRight.setIconLeft(mRightIcon);
                break;
            case POSITION_RIGHT:
                mItvLeft.setIconRightWidth(mIconWidth);
                mItvLeft.setIconRightHeight(mIconHeight);
                mItvRight.setIconRightWidth(mIconWidth);
                mItvRight.setIconRightHeight(mIconHeight);
                mItvLeft.setIconRightTint(mIconTint);
                mItvRight.setIconRightTint(mIconTint);
                mItvLeft.setIconRight(mLeftIcon);
                mItvRight.setIconRight(mRightIcon);
                break;
            case POSITION_TOP:
                mItvLeft.setIconTopWidth(mIconWidth);
                mItvLeft.setIconTopHeight(mIconHeight);
                mItvRight.setIconTopWidth(mIconWidth);
                mItvRight.setIconTopHeight(mIconHeight);
                mItvLeft.setIconTopTint(mIconTint);
                mItvRight.setIconTopTint(mIconTint);
                mItvLeft.setIconTop(mLeftIcon);
                mItvRight.setIconTop(mRightIcon);
                break;
            case POSITION_BOTTOM:
                mItvLeft.setIconBottomWidth(mIconWidth);
                mItvLeft.setIconBottomHeight(mIconHeight);
                mItvRight.setIconBottomWidth(mIconWidth);
                mItvRight.setIconBottomHeight(mIconHeight);
                mItvLeft.setIconBottomTint(mIconTint);
                mItvRight.setIconBottomTint(mIconTint);
                mItvLeft.setIconBottom(mLeftIcon);
                mItvRight.setIconBottom(mRightIcon);
                break;
        }
    }

    public interface OnPressListener {

        void onLeftPress(View v);

        void onRightPress(View v);
    }
}
