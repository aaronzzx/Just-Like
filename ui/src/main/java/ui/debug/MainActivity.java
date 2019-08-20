package ui.debug;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.aaron.base.base.BaseActivity;
import com.aaron.base.util.LogUtils;
import com.aaron.ui.R;
import com.aaron.ui.R2;
import com.aaron.ui.util.DialogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("all")
public class MainActivity extends BaseActivity {

    @BindView(R.id.ui_ll)
    LinearLayout mLl;
    @BindView(R.id.ui_test_rl)
    RelativeLayout mRl;
    @BindView(R.id.ui_test_fl)
    FrameLayout mFl;
    @BindView(R2.id.ui_btn_launch_dialog)
    Button mBtnLaunchDialog;
    @BindView(R2.id.ui_btn_launch_normal_activity)
    Button mBtnLaunchNormalActivity;
    @BindView(R2.id.ui_btn_launch_dialog_activity)
    Button mBtnLaunchDialogActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_main);
        ButterKnife.bind(this);
        LogUtils.e("----- onCreate -----");

        LogUtils.e(mFl.getChildCount());
        View view = LayoutInflater.from(this)
                .inflate(R.layout.ui_test_2, mFl, false);
//        mFl.addView(view);
        LogUtils.e(mFl.getChildCount());
        LogUtils.e(mFl.getChildAt(0));
        LogUtils.e(view);
        LogUtils.e(view.getParent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.e("----- onStart -----");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.e("----- onRestart -----");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e("----- onResume -----");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.e("----- onPause -----");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.e("----- onStop -----");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e("----- onDestroy -----");
    }

    @OnClick({R2.id.ui_btn_launch_dialog, R2.id.ui_btn_launch_normal_activity, R2.id.ui_btn_launch_dialog_activity})
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ui_btn_launch_dialog) {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.ui_dialog, null);
            DialogUtil.createDialog(this, dialogView).show();
        } else if (id == R.id.ui_btn_launch_normal_activity) {
            startActivity(new Intent(this, NormalActivity.class));
        } else if (id == R.id.ui_btn_launch_dialog_activity) {
            startActivity(new Intent(this, DialogActivity.class));
        }
    }
}
