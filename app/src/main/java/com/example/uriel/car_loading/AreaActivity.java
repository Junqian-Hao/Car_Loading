package com.example.uriel.car_loading;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;

/**
 * Created by 郝俊谦 on 2016/5/25.
 * 违章高发地
 */
public class AreaActivity extends Activity implements AMapLocationListener {
    private String loca;
    private WebView webView;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private ImageView area_back;
    private static final String TAG = "AreaActivity";

    @JavascriptInterface
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area);
        //设置标题栏高度
        findViewById(R.id.area_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this);
        webView = (WebView) findViewById(R.id.area_webView);
        webView.getSettings().setJavaScriptEnabled(true);
        area_back = (ImageView) findViewById(R.id.area_back);
        area_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AreaActivity.this.finish();
            }
        });
        dingwei();
    }

    //以下方法为定位所属
    private void dingwei() {
        mlocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位参数
        mLocationOption.setOnceLocation(true);
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        mlocationClient.startLocation();
    }

    class WebAppInterface {
        String mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(String c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        // 如果target 大于等于API 17，则需要加上如下注解
        @JavascriptInterface
        public String showToast() {
            return mContext;
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    @JavascriptInterface
    public void onLocationChanged(AMapLocation amapLocation) {
        Log.i(TAG, "onLocationChanged: 定位回调");

        if (amapLocation != null
                && amapLocation.getErrorCode() == 0) {
            Log.w("$$$", "回调执行");
            Log.i(TAG, "onLocationChanged: 定位激活成功");
            loca = amapLocation.getLatitude() + "@" + amapLocation.getLongitude();
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl("http://q1.wangzhuanz.com/api/webview/Nweizhang.php");
            webView.addJavascriptInterface(new WebAppInterface(loca), "Android");
            final WebSettings settings = webView.getSettings();
            settings.setSupportZoom(true);
            webView.setWebChromeClient(new WebChromeClient() {

                @Override
                public boolean onJsAlert(WebView view, String url, String message,
                                         JsResult result) {
                    // TODO Auto-generated method stub
                    return super.onJsAlert(view, url, message, result);
                }

            });

        } else {
            String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
            Log.e("AmapErr", errText);
        }


    }

    /**
     * 停止定位
     */
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

}
