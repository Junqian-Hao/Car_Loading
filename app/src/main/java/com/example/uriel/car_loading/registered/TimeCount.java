package com.example.uriel.car_loading.registered;

import android.os.CountDownTimer;
import android.widget.Button;

/**
 * Created by 郝俊谦 on 2016/4/27.
 * 实现按钮计时
 */
public class TimeCount extends CountDownTimer {
   Button checking ;
    public TimeCount(long millisInFuture, long countDownInterval) {
        //参数依次为总时长,和计时的时间间隔
        super(millisInFuture, countDownInterval);
    }

    //获得要计时地按钮
    public void setButton(Button checking)
    {
        this.checking = checking;
    }

    @Override
    public void onTick(long millisUntilFinished)//计时过程显示
    {

        checking.setClickable(false);
        checking.setText(millisUntilFinished /1000+"秒");
    }

    @Override
    public void onFinish()//计时完毕时触发
    {
        checking.setText("重新验证");
        checking.setClickable(true);
    }
}
