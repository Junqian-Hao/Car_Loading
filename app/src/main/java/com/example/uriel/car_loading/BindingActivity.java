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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.uriel.car_loading.duiying.Cheliangleixin;
import com.example.uriel.car_loading.duiying.Chepai;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by uriel on 2016/5/15.
 */

//绑定车辆
public class BindingActivity extends Activity {
    private Button binddDetermine;
    private EditText chepaishengyubufen, chejiahao, fadongjihao;
    private String chepaiqianzhu, cheliangleixin;
    private Spinner che_pai_qian_zhui, che_liang_lei_xin;
    private String phone;
    private SharedPreferences sharedPreferences;
    private ImageView code;
    private final String urltxt = "http://q1.wangzhuanz.com/userCar.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.binding);
        //设置标题栏高度
        findViewById(R.id.binding_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this);
        binddDetermine = (Button) findViewById(R.id.binding_determine);
        binddDetermine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bangding();
//                Intent intent = new Intent(BindingActivity.this, MainActivity.class);
//                BindingActivity.this.startActivity(intent);
//                BindingActivity.this.finish();
            }
        });

        //获取手机号
        sharedPreferences = getSharedPreferences("mima", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");

        //实例化控件
        chepaishengyubufen = (EditText) findViewById(R.id.lsnum_input);
        chejiahao = (EditText) findViewById(R.id.frameno_input);
        fadongjihao = (EditText) findViewById(R.id.engineno_input);
        che_pai_qian_zhui = (Spinner) findViewById(R.id.lsprefix_input);
        che_liang_lei_xin = (Spinner) findViewById(R.id.lstype_input);
        code = (ImageView) findViewById(R.id.code);

        code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(BindingActivity.this, CaptureActivity.class), 0);
            }
        });

        //获得用户选择内容
        che_pai_qian_zhui.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chepaiqianzhu = BindingActivity.this.getResources().getStringArray(R.array.chepaiqianzhui)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        che_liang_lei_xin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cheliangleixin = BindingActivity.this.getResources().getStringArray(R.array.cheliangleixin)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BindingActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void bangding() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(urltxt);

                    Log.i("管局名称", new Chepai().chaxun(chepaiqianzhu));
                    Log.i("车辆信息代码", new Cheliangleixin().chaxun(cheliangleixin));
                    Log.i("车辆类型", cheliangleixin);
                    Log.i("车牌前缀", chepaiqianzhu);
                    Log.i("手机号", phone);

                    List<NameValuePair> list = new ArrayList<>();
                    list.add(new BasicNameValuePair("phone", phone));
                    list.add(new BasicNameValuePair("carorg", new Chepai().chaxun(chepaiqianzhu)));
                    list.add(new BasicNameValuePair("lsprefix", chepaiqianzhu));
                    list.add(new BasicNameValuePair("lsnum", chepaishengyubufen.getText().toString()));
                    list.add(new BasicNameValuePair("lstype", new Cheliangleixin().chaxun(cheliangleixin)));
                    list.add(new BasicNameValuePair("engineno", fadongjihao.getText().toString()));
                    list.add(new BasicNameValuePair("frameno", chejiahao.getText().toString()));
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
                    httpPost.setEntity(entity);

                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity httpEntity = httpResponse.getEntity();
                        String result = EntityUtils.toString(httpEntity, "utf-8");

                        Log.i("返回字符串", result);
                        JSONObject jsonobject = new JSONObject(result);
                        String jieguo = jsonobject.getString("message");


                        //识别绑定是否成功
                        int shibie = jsonobject.getInt("code");

                        Message message = new Message();
                        message.what = 0;
                        message.obj = jieguo;
                        message.arg1 = shibie;

                        Log.i("结果", jieguo);
                        Log.i("识别码", new Integer(shibie).toString());

                        handler.sendMessage(message);
                    } else {
                        Toast.makeText(BindingActivity.this, "网络异常，请稍候再试", Toast.LENGTH_SHORT).show();
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String jieguo = (String) msg.obj;
                    int shibie = msg.arg1;
                    if(shibie == 200)
                    {
                        BindingActivity.this.finish();
                    }
                    Toast.makeText(BindingActivity.this, jieguo, Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    try {
                    String re = (String) msg.obj;
                    Log.i("json", re);
                    JSONObject jsonObject = null;
                        jsonObject = new JSONObject(re);
                    Toast.makeText(BindingActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        if(jsonObject.getString("code").equals("200"))
                        {
                            BindingActivity.this.finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.i("abc", "返回成功");
            Bundle bundle = data.getExtras();
            final String result = bundle.getString("result");
            Log.i("abc", result);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("http://q1.wangzhuanz.com/userCarma.php");
                        Log.i("!!!", result);
                        List<NameValuePair> list = new ArrayList<NameValuePair>();
                        list.add(new BasicNameValuePair("phone", phone));
                        list.add(new BasicNameValuePair("json", result));
                        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
                        httpPost.setEntity(entity);

                        HttpResponse httpResponse = httpClient.execute(httpPost);
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            HttpEntity httpEntity = httpResponse.getEntity();
                            String reposnse = EntityUtils.toString(httpEntity, "utf-8");

                            Message message = new Message();
                            message.what = 1;
                            message.obj = reposnse;
                            handler.sendMessage(message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
