package com.robot.tuling.ui;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.robot.tuling.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunfusheng on 2015/2/6.
 */
public class DetailActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.webView)
    WebView webView;


    private String mUrl = "";
    private WebSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        initActionBar();
        initData();
        initView();
    }

    private void initActionBar() {
        toolbar.setTitle("新闻内容");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void initData() {
        mUrl = getIntent().getStringExtra("url");
    }

    private void initView() {
        settings = webView.getSettings();
        settings.setJavaScriptEnabled(true); //如果访问的页面中有Javascript，则WebView必须设置支持Javascript
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(true); //支持缩放
        settings.setBuiltInZoomControls(true); //支持手势缩放
        settings.setDisplayZoomControls(false); //是否显示缩放按钮

        // >= 19(SDK4.4)启动硬件加速，否则启动软件加速
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            settings.setLoadsImagesAutomatically(false);
        }

        settings.setUseWideViewPort(true); //将图片调整到适合WebView的大小
        settings.setLoadWithOverviewMode(true); //自适应屏幕
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setSaveFormData(true);
        settings.setSupportMultipleWindows(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //优先使用缓存

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); //可使滚动条不占位
        webView.setHorizontalScrollbarOverlay(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.requestFocus();

        webView.loadUrl(mUrl);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                toolbar.setTitle("正在加载...");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.setLayerType(View.LAYER_TYPE_NONE, null);
                if (!settings.getLoadsImagesAutomatically()) {
                    settings.setLoadsImagesAutomatically(true);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                toolbar.setTitle("加载失败");
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                toolbar.setTitle(title);
            }
        });

    }

}

