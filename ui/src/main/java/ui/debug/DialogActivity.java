package ui.debug;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.aaron.base.util.LogUtils;
import com.aaron.ui.R;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity_dialog);
        LogUtils.e("----- onCreate -----");
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
}
