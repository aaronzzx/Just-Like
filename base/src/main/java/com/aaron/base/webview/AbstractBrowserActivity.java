package com.aaron.base.webview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.IntDef;
import com.aaron.base.base.BaseActivity;
import com.blankj.utilcode.util.StringUtils;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 简单网页浏览器的基类
 *
 * @author Aaron aaronzheng9603@gmail.com
 */
public abstract class AbstractBrowserActivity extends BaseActivity {

    public static final int DEFAULT = 0;
    public static final int SONIC   = 1;

    protected WebView mWebView;

    private SonicSession mSonicSession;

    protected abstract int layoutId();

    protected abstract int webViewId();

    protected abstract String url();

    @Mode protected abstract int mode();

    protected abstract void init();

    protected abstract void jsInterface();

    protected abstract WebChromeClient webChromeClient();

    protected abstract WebViewClient webViewClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId());
        initView();
        initWebView();
    }

    @Override
    protected void onDestroy() {
        if (null != mSonicSession) {
            mSonicSession.destroy();
            mSonicSession = null;
        }
        // 防止 WebView 内存泄漏
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void initView() {
        mWebView = findViewById(webViewId());
        init();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initWebView() {
        Intent intent = getIntent();
        String url = url();
        int mode = mode();
        if (StringUtils.isEmpty(url)) {
            if (mode != DEFAULT && mode != SONIC) {
                finish();
                return;
            }
        }

        // 开启硬件加速
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        jsInterface(); // JS Support
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
        // init WebView settings
        webSettings.setAllowContentAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBlockNetworkImage(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 开启 HTTP 和 HTTPS 混合模式
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // init sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
        }
        SonicSessionClientImpl sonicSessionClient = null;
        // if it's sonic mode , startup sonic session at first time
        if (mode != SONIC) { // sonic mode
            SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();
            sessionConfigBuilder.setSupportLocalServer(true);
            // create sonic session and run sonic flow
            mSonicSession = SonicEngine.getInstance().createSession(url, sessionConfigBuilder.build());
            if (null != mSonicSession) {
                mSonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
            }
        }

        // init WebView callback
        mWebView.setWebChromeClient(webChromeClient());
        mWebView.setWebViewClient(webViewClient());

        // WebView is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient.bindWebView(mWebView);
            sonicSessionClient.clientReady();
        } else { // default mode
            mWebView.loadUrl(url);
        }
    }

    @Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
    @IntDef({DEFAULT, SONIC})
    public @interface Mode {

    }
}
