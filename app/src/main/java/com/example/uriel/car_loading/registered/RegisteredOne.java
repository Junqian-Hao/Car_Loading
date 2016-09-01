package com.example.uriel.car_loading.registered;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uriel.car_loading.LoginActivity;
import com.example.uriel.car_loading.R;
import com.example.uriel.car_loading.StatusBarHeight;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by uriel on 2016/4/28.
 */
//第一个注册界面
public class RegisteredOne extends Activity {
    private ImageView backOne;
    private Button nextOne;
    private TextView phone;
    private EditText registeredPhone, registeredYanzhengma;
    private Button btYanzhengma;
    private String phoneNum = "";
    private TimeCount timeCount;
    public static final int SHOUJIANHAOSHANGCHUAN = 1;
    int yanzhenma;
    String yanZhenMaUrl = "http://q1.wangzhuanz.com/api/loginV.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.registered_one);
        //设置标题栏高度
        findViewById(R.id.registered_one_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this);
        phone = (TextView) findViewById(R.id.registered_phone);
        phone.setText(phoneNum);
        btYanzhengma = (Button) findViewById(R.id.bt_yanzhengma);
        registeredPhone = (EditText) findViewById(R.id.registered_phone);
        registeredYanzhengma = (EditText) findViewById(R.id.registered_yanzhengma);

        //获得按钮计时的实例
        timeCount = new TimeCount(3000, 1000);
        //传入要实现计时的按钮
        timeCount.setButton(btYanzhengma);
        btYanzhengma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_yanzhengma:
                        shoujihaoshangchuan(registeredPhone.getText().toString());
                        //启动按钮计时
                        timeCount.start();
                        break;
                }
            }
        });

        //下一步按钮
        nextOne = (Button) findViewById(R.id.registered_next_one);
        nextOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNum = phoneNum + phone.getText();
                if (!(new Integer(yanzhenma).toString().equals(registeredYanzhengma.getText().toString()))) {
                    Toast.makeText(RegisteredOne.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    Toast.makeText(RegisteredOne.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent intent = new Intent(RegisteredOne.this, RegisteredTwo.class);
                    intent.putExtra("phone", registeredPhone.getText().toString());
                    RegisteredOne.this.startActivity(intent);
                }
            }
        });

        //返回按钮
        backOne = (ImageView) findViewById(R.id.registered_back_one);
        backOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisteredOne.this.finish();
            }
        });
    }

    //得到验证码
    public void shoujihaoshangchuan(final String shoujihao) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(yanZhenMaUrl);
                    yanzhenma = (int) (Math.random() * 10000);
                    Log.i("验证码", new Integer(yanzhenma).toString());

                    //发送post请求
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("phone", shoujihao));
                    list.add(new BasicNameValuePair("yanzhengma", new Integer(yanzhenma).toString()));
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
                    httpPost.setEntity(entity);
//               HttpResponse httpResponse = httpClient.execute(httpPost);


                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        Log.i("@@@@", "响应成功");
                    }
                    //读回网络返回

                    HttpEntity httpEntity = httpResponse.getEntity();
                    String response = EntityUtils.toString(httpEntity, "utf-8") + "验证码：" + new Integer(yanzhenma).toString();

                    JSONObject jsonObject = new JSONObject(response);
                    String asd = jsonObject.getString("message");
                    Log.i("message", asd);

                    Log.i("@@@@", response);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            RegisteredOne.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
