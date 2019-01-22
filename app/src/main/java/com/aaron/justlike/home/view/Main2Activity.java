package com.aaron.justlike.home.view;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.aaron.justlike.R;
import com.aaron.justlike.adapter.MainAdapter;
import com.aaron.justlike.another.Image;
import com.aaron.justlike.extend.MyGridLayoutManager;
import com.aaron.justlike.home.presenter.IMainPresenter;
import com.aaron.justlike.home.presenter.MainPresenter;
import com.aaron.justlike.util.SystemUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.jaeger.library.StatusBarUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class Main2Activity extends AppCompatActivity implements IMainView {

    private IMainPresenter mPresenter;

    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private DrawerLayout mParentLayout;
    private NavigationView mSlideMenu;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mRefresh;
    private LinearLayout mParentOfToolbar;

    private List<Image> mImageList;
    private RecyclerView.ItemDecoration mXItemDecoration = new RecyclerView.ItemDecoration() {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 3 == 0) {
                outRect.left = 0;
                outRect.right = SystemUtils.dp2px(Main2Activity.this, 2.8F); // 8px
            } else if (parent.getChildAdapterPosition(view) % 3 == 1) {
                outRect.left = SystemUtils.dp2px(Main2Activity.this, 1.3F); // 4px
                outRect.right = SystemUtils.dp2px(Main2Activity.this, 1.3F); // 4px
            } else if (parent.getChildAdapterPosition(view) % 3 == 2) {
                outRect.left = SystemUtils.dp2px(Main2Activity.this, 2.8F); // 8px
                outRect.right = 0;
            }
        }
    };
    private RecyclerView.ItemDecoration mYItemDecoration = new RecyclerView.ItemDecoration() {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.bottom = 0;
            outRect.top = SystemUtils.dp2px(Main2Activity.this, 4.2F); // 12px
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 0;
            } else if (parent.getChildAdapterPosition(view) == 1) {
                outRect.top = 0;
            } else if (parent.getChildAdapterPosition(view) == 2) {
                outRect.top = 0;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        attachPresenter(new MainPresenter(this));
        initView();
        mPresenter.requestImage(MainPresenter.SORT_BY_DATE, MainPresenter.ASCENDING_ORDER);
    }

    /**
     * 释放资源
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 当 Activity 销毁时调用 Presenter 的 detachView() 方法
        // 将 MainView 引用赋值为 null
        mPresenter.detachView();
    }

    /**
     * 连接 Presenter
     */
    @Override
    public void attachPresenter(IMainPresenter presenter) {
        mPresenter = presenter;
    }

    /**
     * 初始化控件
     */
    @Override
    public void initView() {
        mAppBarLayout = findViewById(R.id.activity_main_appbar_layout);
        mToolbar = findViewById(R.id.activity_main_toolbar);
        mParentLayout = findViewById(R.id.drawer_layout);
        mSlideMenu = findViewById(R.id.nav_view);
        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new MyGridLayoutManager(this, 3);
        mRefresh = findViewById(R.id.swipe_refresh);
        mParentOfToolbar = findViewById(R.id.activity_main_linear_layout);

        setSupportActionBar(mToolbar);
        enableHomeAsUp();
        StatusBarUtil.setTransparentForDrawerLayout(this, mParentLayout); // 修改状态栏
        mRefresh.setColorSchemeResources(R.color.colorPrimary);
        // 初始化 RecyclerView 状态
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(mXItemDecoration);
        mRecyclerView.addItemDecoration(mYItemDecoration);
    }

    /**
     * 展示图片
     */
    @Override
    public void showImage(List<Image> imageList) {
        mRecyclerView.setAdapter(new MainAdapter(this, imageList));
    }

    /**
     * 展示侧滑菜单
     */
    @Override
    public void showSlideMenu() {

    }

    /**
     * 展示下拉刷新
     */
    @Override
    public void showRefresh() {

    }

    /**
     * 启用全屏模式
     */
    @Override
    public void enableFullScreen() {

    }

    /**
     * 退出全屏模式
     */
    @Override
    public void exitFullScreen() {

    }

    /**
     * 启用滑动菜单并设置图标
     */
    private void enableHomeAsUp() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_drawer_menu);
        }
    }
}
