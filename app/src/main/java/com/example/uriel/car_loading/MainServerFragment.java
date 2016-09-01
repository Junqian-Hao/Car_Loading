package com.example.uriel.car_loading;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.uriel.car_loading.gas_station.GasStationActivity;

/**
 * Created by uriel on 2016/5/20.
 * 服务界面
 */
public class MainServerFragment extends Fragment implements View.OnClickListener{

    View view;

    LinearLayout serverNavigation;
    LinearLayout serverIllegal;
    LinearLayout serverReservation;
    LinearLayout serverStation;
    LinearLayout serverCarinfo;
    LinearLayout serverArea;
    LinearLayout serverFast;
    LinearLayout serverAccident;
    private static final String TAG = "MainServerFragment";
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.server,container,false);
        return view;
    }
//继承了onActivityCreated()方法，相当于Activity里的onCreate()方法，模仿使用即可
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initEvents();
    }
//初始化组件
    private void initView() {
        serverNavigation = (LinearLayout) view.findViewById(R.id.server_navigation);
        serverIllegal = (LinearLayout) view.findViewById(R.id.server_illegal);
        serverReservation = (LinearLayout) view.findViewById(R.id.server_reservation);
        serverStation = (LinearLayout) view.findViewById(R.id.server_station);
        serverCarinfo = (LinearLayout) view.findViewById(R.id.server_carinfo);
        serverArea = (LinearLayout) view.findViewById(R.id.server_area);
        serverFast = (LinearLayout) view.findViewById(R.id.server_fast);
        serverAccident = (LinearLayout) view.findViewById(R.id.server_accident);

    }
//给组件添加点击事件
    private void initEvents() {
        //设置标题栏高度
        view.findViewById(R.id.server_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this.getContext());
        serverNavigation.setOnClickListener(this);
        serverIllegal.setOnClickListener(this);
        serverReservation.setOnClickListener(this);
        serverStation.setOnClickListener(this);
        serverCarinfo.setOnClickListener(this);
        serverArea.setOnClickListener(this);
        serverFast.setOnClickListener(this);
        serverAccident.setOnClickListener(this);
    }
//点击事件监听
    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.server_navigation:
                //导航
                intent = new Intent(this.getActivity(), NavigationActivity.class);
                break;
            case R.id.server_illegal:
                //违章查询
                intent = new Intent(this.getActivity(), illgealActivity.class);
                break;
            case R.id.server_reservation:
                //预约加油
//                将下边的illgealActivity换成需要跳转的Activity即可，下边一样
              intent = new Intent(this.getActivity(), ReservationActivity.class);
                break;
            case R.id.server_station:
                //附近加油站
                intent = new Intent(this.getActivity(), GasStationActivity.class);
                break;
            case R.id.server_carinfo:
                //车辆信息
                intent = new Intent(this.getActivity(), CarInfoActivity.class);
                break;
            case R.id.server_area:
                //违章高发地
                intent = new Intent(this.getActivity(), AreaActivity.class);
                break;
            case R.id.server_fast:
                //论坛
                intent = new Intent(this.getActivity(), FastActivity.class);
                break;
            case R.id.server_accident:
                //交通信息
                intent = new Intent(this.getActivity(), AccidentActivity.class);
                break;
        }
        this.getActivity().startActivity(intent);
    }

}
