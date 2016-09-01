package com.example.uriel.car_loading;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uriel.car_loading.chagePassword.ChagePasswordOne;
import com.example.uriel.car_loading.registered.RegisteredOne;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录界面
 * Created by uriel on 2016/4/29.
 */
public class LoginActivity extends Activity {
    private TextView zhuce = null;
    private Button denglu = null;
    private EditText userNumber, password;
    private static final int SHOW_RESPONSE = 0;
    private static final int SHOW_RESPONSE2 = 1;
    private String response;
    private TextView loginForgetPassword;
    //标识用户是否成功登录
    volatile int shibie = 0;
    private SharedPreferences sharedPreferences;
    boolean isExit;
    private static final String TAG = "LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //设置标题栏高度
        findViewById(R.id.login_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this);
        userNumber = (EditText) findViewById(R.id.user_num);
        password = (EditText) findViewById(R.id.password);
        zhuce = (TextView) findViewById(R.id.login_zhuce);
        denglu = (Button) findViewById(R.id.denglu);
        loginForgetPassword = (TextView) findViewById(R.id.login_forget_password);
        loginForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ChagePasswordOne.class);
                LoginActivity.this.startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        sharedPreferences = getSharedPreferences("mima", MODE_PRIVATE);



            zhuce.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, RegisteredOne.class);
                    LoginActivity.this.startActivity(intent);
                }
            });

            denglu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.denglu:
                            denLu();
                            break;
                    }
                }
            });
        }


    public void denLu() {
        new Thread(new Runnable() {
            @Override
            synchronized public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://q1.wangzhuanz.com/sign.php");

                    //发送post请求
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("phone", userNumber.getText().toString()));
                    list.add(new BasicNameValuePair("password", password.getText().toString()));
                    Log.i("!!!", password.getText().toString());
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
                    httpPost.setEntity(entity);
                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    //读回网络返回
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String response = EntityUtils.toString(httpEntity, "utf-8");

                    Log.i("!!!", response);

                    //回传主线程
                    Message message = new Message();
                    message.what = SHOW_RESPONSE;
                    message.obj = response;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_RESPONSE:
                    try {
                        response = (String) msg.obj;
                        JSONObject jsonObject = new JSONObject(response);
                        String phone = jsonObject.getString("message");
                        int code = jsonObject.getInt("code");
                        if (code == 200) {
                            shibie = 1;
                        }
                        Log.i("json内容", phone);
                        //进行UI操作
                        Toast.makeText(LoginActivity.this, phone, Toast.LENGTH_SHORT).show();
                        if (shibie == 1) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("phone", userNumber.getText().toString());
                            editor.putString("mima", password.getText().toString());
                            editor.putInt("shibie", 1);
                            editor.commit();
                            Log.i(TAG, "handleMessage: 登录信息储存完毕");
                            //界面跳转
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(intent);
                            LoginActivity.this.finish();
                            Log.i(TAG, "handleMessage: 登录界面跳转完毕");
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessageDelayed(0, 2000);
                return false;
            } else {
                LoginActivity.this.finish();
                return true;
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            isExit = false;
        }

    };

}
