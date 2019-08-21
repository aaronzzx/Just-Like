package com.aaron.justlike.common.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.aaron.ui.widget.NewViewPager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

public class MainRefreshLayout extends SmartRefreshLayout {

    private NewViewPager mNewVp;

    public MainRefreshLayout(Context context) {
        super(context);
    }

    public MainRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void setStateRefreshing(boolean notify) {
        if (notify) mNewVp.setScrollable(false);
        super.setStateRefreshing(notify);
    }

    @Override
    public RefreshLayout finishRefresh(boolean success) {
        if (mNewVp != null) mNewVp.setScrollable(true);
        return super.finishRefresh(success);
    }

    public void setNewVp(NewViewPager newVp) {
        mNewVp = newVp;
    }
}