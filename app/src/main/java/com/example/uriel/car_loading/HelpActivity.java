package com.example.uriel.car_loading;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.uriel.car_loading.registered.RegisteredOne;

/**
 *
 * 使用帮助界面
 *
 */
public class HelpActivity extends Activity implements OnPageChangeListener {

    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;

    // 记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);

        // 初始化页面
        initViews();

    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();
        // 初始化引导图片列表
        views.add(inflater.inflate(R.layout.help_one, null));
        views.add(inflater.inflate(R.layout.help_two, null));
        views.add(inflater.inflate(R.layout.help_three, null));

        // 初始化Adapter
        vpAdapter = new ViewPagerAdapter(views, this);

        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        // 绑定回调
        vp.setOnPageChangeListener(this);
    }

    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    // 当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    // 当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            HelpActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}