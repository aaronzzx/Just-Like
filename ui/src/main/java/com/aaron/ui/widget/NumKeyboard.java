package com.aaron.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aaron.ui.R;
import com.aaron.ui.R2;
import com.aaron.ui.adapter.BaseRecyclerAdapter;
import com.blankj.utilcode.util.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NumKeyboard extends FrameLayout {

    @BindView(R2.id.ui_ll_root)
    LinearLayout mLlRoot;
    @BindView(R2.id.ui_ll_hide)
    LinearLayout mLlHide;
    @BindView(R2.id.ui_rv_keyboard)
    RecyclerView mRvKeyboard;

    private EditText mEtInput;
    private OnTextListener mTextListener;
    private InputAdapter mAdapter;

    public NumKeyboard(@NonNull Context context) {
        this(context, null);
    }

    public NumKeyboard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    @OnClick(R2.id.ui_ll_hide)
    public void onHide() {
        mLlRoot.setVisibility(GONE);
    }

    public void init(EditText et) {
        mEtInput = et;
        mEtInput.setOnClickListener(v -> {
            mLlRoot.setVisibility(VISIBLE);
        });
    }

    public void clear() {
        mAdapter.pwd = "";
    }

    public void setHideBtnVisibility(int visibility) {
        mLlHide.setVisibility(visibility);
    }

    public void setTextListener(OnTextListener listener) {
        mTextListener = listener;
    }

    private void initView(Context context) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.ui_num_keyboard, this, true);
        ButterKnife.bind(rootView);

        mRvKeyboard.addItemDecoration(new XItemDecoration());
        mRvKeyboard.addItemDecoration(new YItemDecoration());
        LinearLayoutManager layoutManager = new GridLayoutManager(context, 3);
        mRvKeyboard.setLayoutManager(layoutManager);
        mAdapter = new InputAdapter();
        mRvKeyboard.setAdapter(mAdapter);
    }

    public interface OnTextListener {
        void onTextChange(CharSequence text);
    }

    private class InputAdapter extends BaseRecyclerAdapter {
        String pwd = "";

        InputAdapter() {

        }

        @Override
        protected RecyclerView.ViewHolder createHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ui_recycler_item_num, parent, false);
            InputAdapter.ViewHolder holder = new InputAdapter.ViewHolder(itemView);
            holder.itemView.setOnClickListener(v -> {
                if (holder.getAdapterPosition() != 11) {
                    if (pwd.length() < 6) {
                        pwd += holder.tvNum.getText().toString();
                        if (mEtInput != null) mEtInput.setText(pwd);
                        if (mTextListener != null) mTextListener.onTextChange(pwd);
//                        if (BuildConfig.DEBUG_MODE && pwd.length() == 6) {
//                            ToastUtil.showShort(pwd);
//                        }
                    }
                } else {
                    if (!StringUtils.isEmpty(pwd)) {
                        pwd = pwd.substring(0, pwd.length() - 1);
                        if (mEtInput != null) mEtInput.setText(pwd);
                        if (mTextListener != null) mTextListener.onTextChange(pwd);
                    }
                }
            });
            return holder;
        }

        @Override
        protected void bindHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
            Context context = viewHolder.itemView.getContext();
            InputAdapter.ViewHolder holder = (InputAdapter.ViewHolder) viewHolder;
            if (position < 9) {
                holder.tvNum.setText(String.valueOf(position + 1));
                holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.ui_sel_rv_item));

            } else if (position == 9 || position == 11) {
                holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.ui_sel_rv_item_blank));
//                if (position == 9) holder.tvNum.setText("X");
                if (position == 11) holder.ivDelete.setVisibility(VISIBLE);

            } else if (position == 10) {
                holder.tvNum.setText(String.valueOf(0));
                holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.ui_sel_rv_item));
            }
        }

        @Override
        protected int itemCount() {
            return 12;
        }

        @Override
        protected int itemViewType(int position) {
            return 0;
        }

        @Override
        protected boolean hasEmptyView() {
            return false;
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvNum;
            private ImageView ivDelete;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNum = itemView.findViewById(R.id.ui_tv_num);
                ivDelete = itemView.findViewById(R.id.ui_iv_delete);
            }
        }
    }

    private static class XItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 3 == 1) {
                outRect.left = 1;
                outRect.right = 1;
            }
        }
    }

    private static class YItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.top = 1;
        }
    }
}
