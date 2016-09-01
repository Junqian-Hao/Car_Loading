package com.example.uriel.car_loading;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.amap.api.navi.AMapHudView;
import com.amap.api.navi.AMapHudViewListener;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.example.uriel.car_loading.util.TTSController;

import java.util.ArrayList;

//导航安全驾驶模式
public class Daohang extends Activity implements AMapHudViewListener, AMapNaviListener {

    public int[] a;
    private AMapHudView mAMapHudView;
    private TTSController mTtsManager;
    private AMapNavi mAMapNavi;

    //起点终点
    private NaviLatLng mNaviStart ;
    private NaviLatLng mNaviEnd ;
    //起点终点列表
    private ArrayList<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
    private ArrayList<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.daohang1);
        Bundle bundle = this.getIntent().getExtras();
        double qidianjingdu = bundle.getDouble("qidianjingdu");
        double qidianweidu = bundle.getDouble("qidianweidu");
        double zhongdianjingdu = bundle.getDouble("zhongdianjingdu");
        double zhongdianweidu = bundle.getDouble("zhongdianweidu");
        mNaviStart = new NaviLatLng(qidianweidu,qidianjingdu);
        mNaviEnd = new NaviLatLng(zhongdianjingdu,zhongdianweidu);

        mTtsManager = TTSController.getInstance(this);
        mTtsManager.init();
        mTtsManager.startSpeaking();

        //创建导航对象
        mAMapNavi = AMapNavi.getInstance(this);
        //添加导航事件回调监听
        mAMapNavi.addAMapNaviListener(mTtsManager);
        mAMapNavi.addAMapNaviListener(this);

        //设置模拟导航速度
//        mAMapNavi.setEmulatorNaviSpeed(150);

        mAMapHudView = (AMapHudView) findViewById(R.id.hudview);
        Log.i("!!", String.valueOf(mAMapHudView));
        mAMapHudView.setHudViewListener(this);
    }

    //-----------------HUD返回键按钮事件-----------------------
    @Override
    public void onHudViewCancel() {
        stopNavi();
        finish();
    }

    private void stopNavi() {

        //停止导航
        mAMapNavi.stopNavi();
        mTtsManager.stopSpeaking();
    }

    protected void onResume() {
        super.onResume();
        mAMapHudView.onResume();
        Log.i("!!", String.valueOf(mNaviEnd));
        Log.i("!!", String.valueOf(mNaviStart));
        mStartPoints.add(mNaviStart);
        mEndPoints.add(mNaviEnd);
        //计算驾车路径
        mAMapNavi.calculateDriveRoute(mStartPoints, mEndPoints, null, PathPlanningStrategy.DRIVING_FASTEST_TIME);

    }

    /**
     * 返回键监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            stopNavi();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause() {
        super.onPause();
        mAMapHudView.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAMapHudView.onDestroy();
        //停止导航
        mAMapNavi.stopNavi();
        //释放导航对象资源 退出时调用此接口释放导航资源，在调用此接口后不能再调用AMapNavi类里的其它接口。
        mAMapNavi.destroy();
        mTtsManager.destroy();
    }


    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {
    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    //步行或者驾车路径规划成功后的回调函数
    @Override
    public void onCalculateRouteSuccess() {
        //开始导航

        AMapNavi.getInstance(this).startNavi(NaviType.EMULATOR);//模拟导航
//        AMapNavi.getInstance(this).startNavi(NaviType.GPS);
    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }


    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }
}
