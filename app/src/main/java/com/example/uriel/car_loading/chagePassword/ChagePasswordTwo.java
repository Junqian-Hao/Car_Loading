package com.example.uriel.car_loading.chagePassword;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.uriel.car_loading.AreaActivity;
import com.example.uriel.car_loading.BindingActivity;
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
import java.util.concurrent.ExecutionException;

import static com.example.uriel.car_loading.R.id.chage_password_back_two;

/**
 * Created by uriel on 2016/5/15.
 */
public class ChagePasswordTwo extends Activity {
    private Button nextTwo;
    private ImageView backTwo;
    private String phone;
    private EditText registeredPasswordOne, registeredPasswordTwo;
    public static final int SHOW_RESPONSE = 0;
    private SharedPreferences sharedPreferences;
    private ImageView changePasswordBackTwo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_two);
        //设置标题栏高度
        findViewById(R.id.password_two_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this);
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.bar_registered_two);
        nextTwo = (Button) findViewById(R.id.chage_password_next_two);

        //获取上一个界面的用户手机号
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        Log.i("上个界面的手机号", phone);

        //初始化控件
        registeredPasswordOne = (EditText) findViewById(R.id.chage_password_password_one);
        registeredPasswordTwo = (EditText) findViewById(R.id.chage_password_password_two);

        nextTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    zhuce();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        changePasswordBackTwo = (ImageView) findViewById(chage_password_back_two);
        changePasswordBackTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChagePasswordTwo.this.finish();
            }
        });
    }

    //执行注册事件
    public void zhuce() throws ExecutionException, InterruptedException {
        String sj = phone;//获取手机号
        String mi = registeredPasswordOne.getText().toString();//获取密码
        String qe = registeredPasswordTwo.getText().toString();//获取确认密码

        Log.i("@@@", sj + mi + qe);

        if (!mi.equals(qe)) {
            Toast.makeText(this, "俩次密码输入不同", Toast.LENGTH_SHORT).show();
            return;
        } else if (mi.equals("") || mi == null) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }  else {
            zhuce(sj, mi);//上传数据
        }
    }

    public void zhuce(final String shoujihao, final String mima) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://q1.wangzhuanz.com/Upassword.php");


                    //发送post请求
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("phone", shoujihao));
                    list.add(new BasicNameValuePair("password", mima));
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
                    httpPost.setEntity(entity);

                    //读回网络返回

                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String response = EntityUtils.toString(httpEntity, "utf-8");
                    Log.i("@@@@", response);
                    JSONObject jsonObject = new JSONObject(response);
                    String asd = jsonObject.getString("message");
                    int code = jsonObject.getInt("code");

                    //回传主线程
                    Message message = new Message();
                    message.what = SHOW_RESPONSE;
                    message.arg1 = code;
                    message.obj = asd;
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
                    String response = (String) msg.obj;
                    Log.i("返回信息", response);
                    int code = msg.arg1;
                    //进行UI操作
                    Toast.makeText(ChagePasswordTwo.this, response, Toast.LENGTH_SHORT).show();
                    if (code == 200) {
                        sharedPreferences = getSharedPreferences("mima", AreaActivity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phone","");
                        editor.putString("mima","");
                        editor.putInt("shibie", 0);
                        editor.commit();
                        Intent intent = new Intent(ChagePasswordTwo.this, LoginActivity.class);
                        ChagePasswordTwo.this.startActivity(intent);
                        ChagePasswordTwo.this.finish();
                    }
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ChagePasswordTwo.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}