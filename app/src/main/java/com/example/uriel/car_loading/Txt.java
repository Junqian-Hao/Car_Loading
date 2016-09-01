package com.example.uriel.car_loading;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by 郝俊谦 on 2016/5/21.
 */
public class Txt extends Activity {

    //推送弹出的界面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_info);
        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = getIntent().getExtras();
            String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            AlertDialog builder = new AlertDialog.Builder(this).setTitle(title).setIcon(R.drawable.logo).setMessage(content).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Txt.this.finish();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Txt.this.finish();
                }
            }).show();
        }
    }
}
