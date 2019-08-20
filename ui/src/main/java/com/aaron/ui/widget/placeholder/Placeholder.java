package com.aaron.ui.widget.placeholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import com.aaron.base.base.BaseApp;

public class Placeholder implements IPlaceholder {

    private Context       mContext;
    private OnTapCallback mOnTapCallback;

    private View mNeedCtrlView;
    private View mEmptyView;
    private View mTapEventView;
    private boolean mFirst;

    private Placeholder(Builder builder) {
        mContext       = builder.context;
        mNeedCtrlView  = builder.needCtrlView;
        mOnTapCallback = builder.tapCallback;
        mFirst         = builder.first;
        handleEmptyView(builder);
        handleTapEvent();
    }

    @Override
    public void show() {
        mNeedCtrlView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hide() {
        mNeedCtrlView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
    }

    @Override
    public boolean isShowing() {
        return mEmptyView.getVisibility() == View.VISIBLE;
    }

    private void handleEmptyView(Builder builder) {
        if (builder.emptyViewId != 0) {
            mEmptyView = createEmptyView(builder.emptyViewId);
        } else {
            mEmptyView = addEmptyView((ViewGroup) mNeedCtrlView.getParent(), builder.emptyView);
        }
        if (builder.tapViewId != 0) {
            mTapEventView = mEmptyView.findViewById(builder.tapViewId);
        }
        mEmptyView.setVisibility(View.GONE);
    }

    private void handleTapEvent() {
        if (mOnTapCallback != null && mTapEventView != null) {
            mTapEventView.setOnClickListener(view -> mOnTapCallback.onTap());
        } else {
            mEmptyView.setOnClickListener(view -> mOnTapCallback.onTap());
        }
    }

    private View createEmptyView(int layoutId) {
        LayoutInflater inflater = LayoutInflater.from(BaseApp.getContext());
        ViewGroup parent        = (ViewGroup) mNeedCtrlView.getParent();
        View emptyView          = inflater.inflate(layoutId, parent, false);
        return addEmptyView(parent, emptyView);
    }

    private View addEmptyView(ViewGroup parent, View emptyView) {
        FrameLayout frameLayout     = new FrameLayout(mContext);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(parent.getLayoutParams());
        frameLayout.setLayoutParams(lp);

        parent.removeView(mNeedCtrlView);
        parent.addView(frameLayout, mFirst ? 0 : -1);
        frameLayout.addView(mNeedCtrlView);
        frameLayout.addView(emptyView);
        return emptyView;
    }

    @FunctionalInterface
    public interface OnTapCallback {
        void onTap();
    }

    public static class Builder {
        private Context       context;
        private OnTapCallback tapCallback;

        private View needCtrlView;
        private View emptyView;
        private int  emptyViewId;
        private int  tapViewId;
        private boolean first;

        public Builder(Context context, View emptyView) {
            this.context   = context;
            this.emptyView = emptyView;
        }

        public Builder(Context context, @LayoutRes int emptyViewId) {
            this.context     = context;
            this.emptyViewId = emptyViewId;
        }

        public Builder needCtrlView(View needCtrlView) {
            this.needCtrlView = needCtrlView;
            return this;
        }

        public Builder tapCallback(OnTapCallback tapCallback) {
            this.tapCallback = tapCallback;
            return this;
        }

        public Builder tapCallback(OnTapCallback tapCallback, @IdRes int tapViewId) {
            this.tapCallback = tapCallback;
            this.tapViewId   = tapViewId;
            return this;
        }

        public Builder addToFirst(boolean first) {
            this.first = first;
            return this;
        }

        public IPlaceholder build() {
            return new Placeholder(this);
        }
    }
}
