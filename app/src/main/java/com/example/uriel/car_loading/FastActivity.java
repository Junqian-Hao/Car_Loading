package com.example.uriel.car_loading;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by 郝俊谦 on 2016/5/25.
 */
public class FastActivity extends AreaActivity implements DownloadListener {
    private WebView areaWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum);
        //设置标题栏高度
        findViewById(R.id.forum_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this);
        areaWebView = (WebView) findViewById(R.id.forum_webView);

        areaWebView.getSettings().setJavaScriptEnabled(true);
        areaWebView.setDownloadListener(this);
        areaWebView.setWebViewClient(new WebViewClient());

        areaWebView.loadUrl("http://q1.wangzhuanz.com/bbs/forum.php?forumlist=1&mobile=2");

    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && areaWebView.canGoBack()) {
            areaWebView.goBack();// 返回前一个页面
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
