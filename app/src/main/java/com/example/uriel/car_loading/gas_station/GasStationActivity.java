package com.example.uriel.car_loading.gas_station;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.uriel.car_loading.R;
import com.example.uriel.car_loading.StatusBarHeight;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by uriel on 2016/5/22.
 * 附近加油站
 */
public class GasStationActivity extends Activity implements AdapterView.OnItemClickListener,AMapLocationListener {
    private ListView gasStation;
    private SimpleAdapter gesStationViewAdapter;
    private List<Map<String,Object>> gesStationList;
    private ImageView gasStationBack;
    public static final String DEF_CHATSET = "UTF-8";
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
    public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
    public static final String APPKEY = "4e07ef68782261188b687732fe8799a0";
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private String city;
    private static final String TAG = "GasStationActivity";


    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0:
                    String result;
                    result = (String) msg.obj;

                    Log.i(TAG, "handleMessage: 回调执行");
                    gesStationViewAdapter = new SimpleAdapter(GasStationActivity.this,gesStationList,R.layout.gas_station_item,
                            new String[]{"gasItemNumber","gasItemName","gasItemAddress","gasOne","gasTwo","gasThree"},
                            new int[] {R.id.gas_item_number,R.id.gas_item_name,R.id.gas_item_address,R.id.gas_one,R.id.gas_two,R.id.gas_three});
                    gasStation.setAdapter(gesStationViewAdapter);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView( R.layout.gas_station);



        dingwei();

        initView();

        initEvents();

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

    private void initView() {
        gasStation = (ListView)findViewById(R.id.ges_station);
        gesStationList= new ArrayList<Map<String,Object>>();

        gasStationBack = (ImageView) findViewById(R.id.gas_station_back);
    }

    private void initEvents() {

        //设置标题栏高度
        findViewById(R.id.gas_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this);

        gasStationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GasStationActivity.this.finish();
            }
        });
    }

    private List<Map<String,Object>> setGasStationList(){

        return gesStationList;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            GasStationActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public  void net(final String strUrl, final Map params, final String method) throws Exception {

        new Thread(new Runnable() {
            URL url;
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                String rs = null;
                try {
                    StringBuffer sb = new StringBuffer();
                    if (method == null || method.equals("GET")) {
                        final  String strUrl2 = strUrl + "?" + urlencode(params);
                        url = new URL(strUrl2);
                    }
                    conn = (HttpURLConnection) url.openConnection();
                    if (method == null || method.equals("GET")) {
                        url = new URL(strUrl);
                        conn.setRequestMethod("GET");
                    } else {
                        url = new URL(strUrl);
                        conn.setRequestMethod("POST");
                        conn.setDoOutput(true);
                    }
                    conn.setRequestProperty("User-agent", userAgent);
                    conn.setUseCaches(false);
                    conn.setConnectTimeout(DEF_CONN_TIMEOUT);
                    conn.setReadTimeout(DEF_READ_TIMEOUT);
                    conn.setInstanceFollowRedirects(false);
                    conn.connect();
                    if (params != null && method.equals("POST")) {
                        try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                            out.writeBytes(urlencode(params));
                        }
                    }
                    InputStream is = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
                    String strRead = null;
                    while ((strRead = reader.readLine()) != null) {
                        sb.append(strRead);
                    }
                    rs = sb.toString();
                    Log.i(TAG, "run: "+ rs);
                    rs = rs.replace("93#", "jiusan");
                    rs = rs.replace("97#", "jiuqi");
                    rs = rs.replace("0#车柴", "checai");
                    Gson gson = new Gson();
                    Json js = gson.fromJson(rs,Json.class);
                    for (int i = 0;i < js.result.data.length;i ++){
                        Map<String,Object> map = new HashMap<String,Object>();
                        map.put("gasItemNumber",i+1);
                        map.put("gasItemName",js.result.data[i].name);
                        map.put("gasItemAddress",js.result.data[i].address);
                        map.put("gasOne",js.result.data[i].gastprice.jiusan + "元");
                        map.put("gasTwo",js.result.data[i].gastprice.jiuqi + "元");
                        map.put("gasThree",js.result.data[i].gastprice.checai + "元");
                        gesStationList.add(map);
                    }

                    reader.close();
                    Message message = new Message();
                    message.what = 0;
                    message.obj = rs;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //将map型转为请求参数型
    public static String urlencode(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
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
        Log.i(TAG, "onLocationChanged: " + amapLocation.getErrorCode());

        if (amapLocation != null
                && amapLocation.getErrorCode() == 0) {
            Log.w("$$$", "回调执行");
             city = amapLocation.getCity();
            Log.i(TAG, "onLocationChanged: " + city);

            String result = null;
            String url = "http://apis.juhe.cn/oil/region";//请求接口地址
            Map params = new HashMap();//请求参数

            params.put("city", city);//城市名urlencode utf8;
            params.put("keywords", "");//关键字urlencode utf8;
            params.put("page", "");//页数，默认1
            params.put("format", "");//格式选择1或2，默认1
            params.put("key", APPKEY);//应用APPKEY(应用详细页查询)
            try {
                net(url, params, "GET");
            } catch (Exception e) {
                e.printStackTrace();
            }
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
}
