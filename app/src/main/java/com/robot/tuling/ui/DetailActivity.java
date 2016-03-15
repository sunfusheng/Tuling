package com.robot.tuling.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.robot.tuling.R;
import com.robot.tuling.util.IsNullOrEmpty;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunfusheng on 2015/2/6.
 */
public class DetailActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.wv_weibo)
    WebView wvWeibo;
    private String URL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        initActionBar();
        initData();
    }

    private void initActionBar() {
        toolbar.setTitle("新闻内容");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        if (getIntent() != null && getIntent().hasExtra("url")) {
            URL = getIntent().getStringExtra("url");
            if (IsNullOrEmpty.isEmpty(URL)) {
                Toast.makeText(this, "暂无新闻内容", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        WebSettings webSettings = wvWeibo.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        wvWeibo.loadUrl(URL);
        wvWeibo.setWebViewClient(new webViewClient());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wvWeibo.canGoBack()) {
            wvWeibo.goBack();
            return true;
        }
        finish();
        return false;
    }

    private class webViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

}
