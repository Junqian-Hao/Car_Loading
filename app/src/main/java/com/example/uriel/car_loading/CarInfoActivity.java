package com.example.uriel.car_loading;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

/**
 * Created by 郝俊谦 on 2016/5/24.
 */
//车辆信息
public class CarInfoActivity extends Activity {
    private SharedPreferences sharedPreferences;
    public WebView webView;
    public ImageView imageView;
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_info);
        //设置标题栏高度
        findViewById(R.id.car_info_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this);
        imageView = (ImageView) findViewById(R.id.car_info_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.car_info_back:
                        CarInfoActivity.this.finish();
                        break;
                }
            }
        });
        webView = (WebView) findViewById(R.id.car_info_webView);
        webView.getSettings().setJavaScriptEnabled(true);
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        Log.i("jingweudu", String.valueOf(location.getLatitude())+location.getLongitude());
        webView.setWebViewClient(new WebViewClient());
//        webView.loadUrl("http://q1.wangzhuanz.com/api/test1.php");//显示弹窗
//        webView.loadUrl("http://q1.wangzhuanz.com/api/webview/test1.php");//显示违章高发路段
        webView.loadUrl("http://q1.wangzhuanz.com/api/webview/getUser.html");
        sharedPreferences = getSharedPreferences("mima", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone","" );
        webView.addJavascriptInterface(new WebAppInterface(phone), "Android");
        final WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        webView.setWebChromeClient(new WebChromeClient()
        {

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result)
            {
                // TODO Auto-generated method stub
                return super.onJsAlert(view, url, message, result);
            }

        });
    }

    class WebAppInterface
    {
        String mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(String c)
        {
            mContext = c;
        }

        /** Show a toast from the web page */
        // 如果target 大于等于API 17，则需要加上如下注解
        @JavascriptInterface
        public String showToast()
        {
            return mContext;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
