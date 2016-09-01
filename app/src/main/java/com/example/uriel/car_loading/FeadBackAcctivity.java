package com.example.uriel.car_loading;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by 郝俊谦 on 2016/5/31.
 */
public class FeadBackAcctivity extends Activity {
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feadback);//设置标题栏高度
        findViewById(R.id.feadback_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this);
        button = (Button) findViewById(R.id.fead_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FeadBackAcctivity.this, "感谢您对车联网小组的支持!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
