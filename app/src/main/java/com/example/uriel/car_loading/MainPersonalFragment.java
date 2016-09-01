package com.example.uriel.car_loading;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.uriel.car_loading.chagePassword.ChagePasswordOne;

/**
 * Created by uriel on 2016/5/20.
 * 个人中心界面
 */
public class MainPersonalFragment extends Fragment implements View.OnClickListener{

    View view;

    private LinearLayout personalChangePassword;
    private LinearLayout personalBinding;
    private LinearLayout personalHelp;
    private LinearLayout personalFeedback;
    private LinearLayout personalExit;
    private SharedPreferences sharedPreferences;
    private CircleImageView personalImage;
    private int[] image = {R.mipmap.img0,R.mipmap.img1,R.mipmap.img2,R.mipmap.img3,R.mipmap.img4,R.mipmap.img5,R.mipmap.img6,R.mipmap.img7,R.mipmap.img8,R.mipmap.img9,};

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.personal,container,false);
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
        personalChangePassword = (LinearLayout) view.findViewById(R.id.personal_change_password);
        personalBinding = (LinearLayout) view.findViewById(R.id.personal_binding);
        personalHelp = (LinearLayout) view.findViewById(R.id.personal_help);
        personalFeedback = (LinearLayout) view.findViewById(R.id.personal_feedback);
        personalExit = (LinearLayout) view.findViewById(R.id.personal_exit);
        personalImage = (CircleImageView) view.findViewById(R.id.personal_image);

    }
    //给组件添加点击事件
    private void initEvents() {
        //设置标题栏高度
        view.findViewById(R.id.personal_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this.getContext());
        personalImage.setImageResource(image[(int)(Math.random()*10)]);
        personalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalImage.setImageResource(image[(int)(Math.random()*10)]);
            }
        });
        personalChangePassword.setOnClickListener(this);
        personalBinding.setOnClickListener(this);
        personalHelp.setOnClickListener(this);
        personalFeedback.setOnClickListener(this);
        personalExit.setOnClickListener(this);
    }
    //点击事件监听
    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.personal_change_password:
                //修改密码
                intent = new Intent(this.getActivity(), ChagePasswordOne.class);
                break;
            case R.id.personal_binding:
                intent = new Intent(this.getActivity(), BindingActivity.class);
                break;
            case R.id.personal_help:
               intent = new Intent(this.getActivity(), HelpActivity.class);
                break;
            case R.id.personal_feedback:
                intent = new Intent(this.getActivity(), FeadBackAcctivity.class);
                break;
            case R.id.personal_exit:
                sharedPreferences = this.getActivity().getSharedPreferences("mima", this.getActivity().MODE_PRIVATE);
                intent = new Intent(this.getActivity(), LoginActivity.class);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("phone","");
                editor.putString("mima","");
                editor.putInt("shibie", 0);
                editor.commit();
                this.getActivity().finish();
                break;
        }
        this.getActivity().startActivity(intent);
    }
}
