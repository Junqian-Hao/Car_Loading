package com.example.uriel.car_loading;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by 郝俊谦 on 2016/5/30.
 * 交通事件
 */
public class AccidentActivity extends Activity implements AMapLocationListener {
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private static final String TAG = "AccidentActivity";

    private ListView accident;
    private SimpleAdapter accidentViewAdapter;
    private List<Map<String,Object>> accidentList;
    private ImageView accidentBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accident);
        initView();

        initEvents();
        dingwei();
    }

    private void initView() {
        accident = (ListView) findViewById(R.id.accident);
        accidentBack = (ImageView) findViewById(R.id.accident_back);
        accidentList = new ArrayList<Map<String,Object>>();

    }

    private void initEvents() {
        //设置标题栏高度
        findViewById(R.id.accident_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this);
        accidentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccidentActivity.this.finish();
            }
        });
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


    @Override
    protected void onPause() {
        super.onPause();
        deactivate();
    }

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
    public void onLocationChanged(AMapLocation amapLocation) {
        Log.i(TAG, "onLocationChanged: 定位结果识别码" + amapLocation.getErrorCode());

        if (amapLocation != null
                && amapLocation.getErrorCode() == 0) {
            double latitude = amapLocation.getLatitude();
            double longitude = amapLocation.getLongitude();
            String city = amapLocation.getCity();
            chaxun(latitude,longitude);
//            chaxun(longitude,latitude);
            Log.i(TAG, "onLocationChanged: 经度" + longitude + "纬度" + latitude + "城市" + city);



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

    public void chaxun(final double lat, final double lon) {
        new Thread(new Runnable() {
            @Override
            synchronized public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://q1.wangzhuanz.com/api/jiaotongshijian.php");

                    //发送post请求
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("lng", String.valueOf(lon)));
                    list.add(new BasicNameValuePair("lat", String.valueOf(lat)));
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
                    httpPost.setEntity(entity);
                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    //读回网络返回
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String response = EntityUtils.toString(httpEntity, "utf-8");

                    Log.i("!!!", response);

                    //回传主线程
                    Message message = new Message();
                    message.what = 0;
                    message.obj = response;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    android.os.Handler handler = new android.os.Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0:
                    String result = (String) msg.obj;
//                    accidentViewAdapter = new SimpleAdapter(this,getaccidentList(),R.layout.accident_item,
//                            new String[]{"accidentNumber","accidentTitle","accidentDescription","accidentTime"},
//                            new int[]{R.id.accident_item_number,R.id.accident_title,R.id.accident_description,R.id.accident_time});
                    accident.setAdapter(accidentViewAdapter);
                    Log.i(TAG, "handleMessage: " + result);
                    break;
            }
        }
    };

}
