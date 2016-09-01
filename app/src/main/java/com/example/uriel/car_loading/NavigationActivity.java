package com.example.uriel.car_loading;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.xys.libzxing.zxing.activity.CaptureActivity;

//导航界面
public class NavigationActivity extends Activity implements LocationSource,
        AMapLocationListener, RouteSearch.OnRouteSearchListener, AMap.OnMapClickListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, GeocodeSearch.OnGeocodeSearchListener {
    private AMap aMap;
    private MapView mapView;
    private ProgressDialog progDialog2 = null;
    private DriveRouteResult mDriveRouteResult;
    private RouteSearch mRouteSearch;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private RadioGroup mGPSModeGroup;
    private final int ROUTE_TYPE_DRIVE = 2;
    private GeocodeSearch geocoderSearch;
    private LatLonPoint mStartPoint = null;//起点，
    private LatLonPoint mEndPoint = null;//终点，
    private ProgressDialog progDialog = null;// 搜索时进度条
    private String addressName;
    private int shibie = 0;
    private ImageButton navSerach;
    private ImageButton navSwitch;
    private LinearLayout navBts;
    private EditText editText;
    private Button button, button2;
    private static final String TAG = "NavigationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.navigation);
        //设置标题栏高度
        findViewById(R.id.navi_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this);
        navSerach = (ImageButton) findViewById(R.id.nav_serach);
        navSwitch = (ImageButton) findViewById(R.id.nav_switch);
        navBts = (LinearLayout) findViewById(R.id.nav_bts);
        editText = (EditText) findViewById(R.id.editText);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog2 = new ProgressDialog(this);
        init();
        navSerach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( editText != null&&  !editText.equals("")) {
                getLatlon(editText.getText().toString());

                }else
                {
                    Toast.makeText(NavigationActivity.this, "请输入正确的地址", Toast.LENGTH_SHORT).show();
                }
            }
        });
        navSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editText != null&&  !editText.equals("")) {

                if (shibie == 0) {
                    shibie = 1;

                } else {
                    shibie = 0;
                }
                getLatlon(editText.getText().toString());
                }else {
                    Toast.makeText(NavigationActivity.this, "请输入正确的地址", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("!!", "导航模式1点击");
                if (mEndPoint == null) {
                    Toast.makeText(NavigationActivity.this, "请先进行路线规划", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("!!", String.valueOf(mEndPoint));
                    daohangmoshi1();
                }
            }
        });

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEndPoint == null) {
                    Toast.makeText(NavigationActivity.this, "请先进行路线规划", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("!!", String.valueOf(mEndPoint));
                    daohangmoshi2();
                }
            }
        });
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        registerListener();
        mRouteSearch = new RouteSearch(this);
        //路径搜索结果监听接口设置。
        mRouteSearch.setRouteSearchListener(this);
        editText.findViewById(R.id.editText);
        aMap.setTrafficEnabled(true);
    }

    //设置监听
    private void registerListener() {
        aMap.setOnMapClickListener(NavigationActivity.this);
        aMap.setOnMarkerClickListener(NavigationActivity.this);
        aMap.setOnInfoWindowClickListener(NavigationActivity.this);
        aMap.setInfoWindowAdapter(NavigationActivity.this);

    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
//        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
//        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
        CameraUpdate cu = CameraUpdateFactory.zoomTo(15);
        // 设置地图的默认放大级别
        aMap.moveCamera(cu);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                mStartPoint = new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude());
                getAddress(mStartPoint);
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
                //修改view的可见属性
                // View.VISIBLE View可见
                //View.INVISIBLE View不可以见，但仍然占据可见时的大小和位置。
                //View.GONE View不可见，且不占据空间。
            }
        }
    }


    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    //进行路线规划的方法
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            Toast.makeText(NavigationActivity.this, "定位中，稍后再试...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mEndPoint == null) {
            Toast.makeText(NavigationActivity.this, "终点未设置", Toast.LENGTH_SHORT).show();
        }
        showProgressDialog();
//         RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
//                mStartPoint, mEndPoint);
        RouteSearch.FromAndTo fromAndTo;
        if (shibie == 0) {
            fromAndTo = new RouteSearch.FromAndTo(
                    mStartPoint, mEndPoint);
        } else {
            fromAndTo = new RouteSearch.FromAndTo(
                    mEndPoint, mStartPoint);
        }
        if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }


    /**
     * 显示进度条对话框
     */
    public void showDialog() {
        progDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog2.setIndeterminate(false);
        progDialog2.setCancelable(true);
        progDialog2.setMessage("正在获取地址");
        progDialog2.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (progDialog2 != null) {
            progDialog2.dismiss();
        }
    }

    /**
     * 响应地理编码
     */
    public void getLatlon(final String name) {
        showDialog();

        GeocodeQuery query = new GeocodeQuery(name, addressName);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {

        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    //将路径绘制到地图
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int i) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (i == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            this, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos());
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    aMap.setMyLocationStyle(new MyLocationStyle().strokeWidth(0));
                    aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
                    CameraUpdate cu = CameraUpdateFactory.zoomTo(15);
                    // 设置地图的默认放大级别
                    aMap.moveCamera(cu);
                }

            } else {
                Toast.makeText(NavigationActivity.this, "错误1", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(NavigationActivity.this, "错误2", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    //map被点击
    @Override
    public void onMapClick(LatLng latLng) {

    }

    //marker被点击
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    //窗口被点击
    @Override
    public void onInfoWindowClick(Marker marker) {

    }


    //WindowAdapter生成的
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    //WindowAdapter生成的
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void daohangmoshi1() {
        Intent intent = new Intent(this, Daohang.class);
        Bundle bundle = new Bundle();
        bundle.putDouble("qidianjingdu", mStartPoint.getLongitude());
        bundle.putDouble("qidianweidu", mStartPoint.getLatitude());
        Log.i("!!", String.valueOf(mEndPoint));
        bundle.putDouble("zhongdianjingdu", mEndPoint.getLatitude());
        bundle.putDouble("zhongdianweidu", mEndPoint.getLongitude());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void daohangmoshi2()
    {
        Intent intent = new Intent(this, Daohang2.class);
        Bundle bundle = new Bundle();
        bundle.putDouble("qidianjingdu", mStartPoint.getLongitude());
        bundle.putDouble("qidianweidu", mStartPoint.getLatitude());
        Log.i("!!", String.valueOf(mEndPoint));
        bundle.putDouble("zhongdianjingdu", mEndPoint.getLatitude());
        bundle.putDouble("zhongdianweidu", mEndPoint.getLongitude());
        intent.putExtras(bundle);
        startActivity(intent);

    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int i) {
        dismissDialog();
        if (i == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                RegeocodeAddress list = result.getRegeocodeAddress();
                addressName = list.getCity();
                Log.i("当前城市", addressName);
            } else {
                Toast.makeText(NavigationActivity.this, "gps错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(NavigationActivity.this, "gps错误", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int i) {
        dismissDialog();
        Log.i(TAG, "onGeocodeSearched: " + i);
        if (i == 1000) {
            navBts.setVisibility(View.VISIBLE);
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
//                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                        AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
//                geoMarker.setPosition(AMapUtil.convertToLatLng(address
//                        .getLatLonPoint()));
                mEndPoint = address.getLatLonPoint();
                addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
                        + address.getFormatAddress();
                Toast.makeText(NavigationActivity.this, addressName, Toast.LENGTH_SHORT).show();
                searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
            } else {
                Toast.makeText(NavigationActivity.this, "目的地查询失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(NavigationActivity.this, "请输入正确的地址", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            NavigationActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
