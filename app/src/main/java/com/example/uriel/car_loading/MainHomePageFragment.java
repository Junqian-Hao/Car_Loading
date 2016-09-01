package com.example.uriel.car_loading;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by uriel on 2016/5/20.
 * 首页界面
 */
//天气预报
public class MainHomePageFragment extends Fragment implements AMapLocationListener, DownloadListener {

    private TextView homPageCity;
    private ImageView homPageImage;
    private TextView homPageWeather;
    private TextView homPageNowTemp;
    private TextView homPageTemp;
    private TextView homPageWind;
    private TextView homPageTrav;
    private TextView homPageCw;
    private static final String TAG = "MainHomePageFragment";

    //定位
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private WebView homePageWebview;
    View view;

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_page, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dingwei();//定位操作
        initView();//实例化控件
        initEvents();//天气预报
        web();//显示
    }

    private void initView() {
        homPageCity = (TextView) view.findViewById(R.id.home_page_city);
        homPageImage = (ImageView) view.findViewById(R.id.home_page_image);
        homPageWeather = (TextView) view.findViewById(R.id.home_page_weather);
        homPageNowTemp = (TextView) view.findViewById(R.id.home_page_now_temp);
        homPageTemp = (TextView) view.findViewById(R.id.home_page_temp);
        homPageWind = (TextView) view.findViewById(R.id.home_page_wind);
        homPageTrav = (TextView) view.findViewById(R.id.home_page_trav);
        homPageCw = (TextView) view.findViewById(R.id.home_page_cw);
        homePageWebview = (WebView) view.findViewById(R.id.home_page_webview);
    }

    //以下方法为定位所属
    private void dingwei() {
        mlocationClient = new AMapLocationClient(this.getContext());
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

    @Override
    public void onPause() {
        super.onPause();
        deactivate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        Log.i(TAG, "onLocationChanged: " + amapLocation.getErrorCode());

        if (amapLocation != null
                && amapLocation.getErrorCode() == 0) {
            String city = amapLocation.getCity().replace("市", "");
            String httpUrl = "http://apis.baidu.com/heweather/weather/free";
            String httpArg = "city=" + city;
            Log.i(TAG, "onLocationChanged: " + city);
            request(httpUrl, httpArg);

        } else {
            String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
            Log.e("AmapErr", errText);
        }

    }

    /**
     * 停止定位
     */
    public void deactivate() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    //天气预报
    private void initEvents() {
        //设置标题栏高度
        view.findViewById(R.id.home_page_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this.getContext());
    }

    public void request(String httpUrl, String httpArg) {
        final String urltxt = httpUrl + "?" + httpArg;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader reader = null;
                    String result = null;
                    StringBuffer sbf = new StringBuffer();
                    URL url = new URL(urltxt);
                    HttpURLConnection connection = (HttpURLConnection) url
                            .openConnection();
                    connection.setRequestMethod("GET");
                    // 填入apikey到HTTP header
                    connection.setRequestProperty("apikey", "f9c9d371393a1c7fea46bfa474ff8c07");
                    connection.connect();
                    InputStream is = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String strRead = null;
                    while ((strRead = reader.readLine()) != null) {
                        sbf.append(strRead);
                        sbf.append("\r\n");
                    }
                    reader.close();
                    result = sbf.toString();
                    Log.i("!!!", result);
                    Message message = new Message();
                    message.what = 0;
                    message.obj = result;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {


                switch (msg.what) {
                    case 0:
                        Log.i(TAG, "回调成功");
                        String resule = (String) msg.obj;
                        Log.i(TAG, "" + resule);
                        JSONObject jsonObject = new JSONObject(resule);
                        JSONArray jsonArray = jsonObject.getJSONArray("HeWeather data service 3.0");
                        homPageCity.setText(jsonArray.getJSONObject(0).getJSONObject("basic").getString("city"));
                        homPageWeather.setText(jsonArray.getJSONObject(0).getJSONObject("now").getJSONObject("cond").getString("txt"));
                        //当前天气代号
//                        jsonArray.getJSONObject(0).getJSONObject("now").getJSONObject("cond").getString("code");
                        homPageNowTemp.setText(jsonArray.getJSONObject(0).getJSONObject("now").getString("tmp") + "℃");
                        homPageTemp.setText(jsonArray.getJSONObject(0).getJSONArray("daily_forecast").getJSONObject(0).getJSONObject("tmp").getString("max") + "~" + jsonArray.getJSONObject(0).getJSONArray("daily_forecast").getJSONObject(0).getJSONObject("tmp").getString("min") + "℃");
                        homPageWind.setText(jsonArray.getJSONObject(0).getJSONObject("now").getJSONObject("wind").getString("dir"));
                        homPageTrav.setText(jsonArray.getJSONObject(0).getJSONObject("suggestion").getJSONObject("trav").getString("brf"));
                        homPageCw.setText(jsonArray.getJSONObject(0).getJSONObject("suggestion").getJSONObject("cw").getString("txt"));


                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //webview所属
    private void web() {

        homePageWebview.getSettings().setJavaScriptEnabled(true);
        homePageWebview.setDownloadListener(this);

        homePageWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent(MainHomePageFragment.this.getActivity(), SkipWebviewActivity.class);
                intent.putExtra("url", url);
                MainHomePageFragment.this.getActivity().startActivity(intent);
                return true;
            }
        });
        homePageWebview.loadUrl("http://q1.wangzhuanz.com/api/webview/bbs.html");
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    //    @Override
//    public void onClick(View v) {
//        v.onKeyDown()
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && homePageWebview.canGoBack()) {
//            homePageWebview.goBack();// 返回前一个页面
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
}
