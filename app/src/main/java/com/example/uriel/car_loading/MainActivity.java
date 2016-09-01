package com.example.uriel.car_loading;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import static com.example.uriel.car_loading.R.color.longin_one;

public class MainActivity extends FragmentActivity implements View.OnClickListener{
    private ViewPager mViewPager;
    private PagerAdapter mAdapter;
    private List<View> mViews = new ArrayList<View>();

    private LinearLayout mTabOne;
    private LinearLayout mTabTwo;
    private LinearLayout mTabThree;

    private ImageButton mImgOne;
    private ImageButton mImgTwo;
    private ImageButton mImgThree;

    private TextView mTxtOne;
    private TextView mTxtTwo;
    private TextView mTxtThree;

    private MainHomePageFragment homePage;
    private MainServerFragment server;
    private MainPersonalFragment personal;

    boolean isExit;



    //判断是否登录
    private SharedPreferences sharedPreferences;
    private static final int SHOW_RESPONSE2 = 1;
    private String response;
    private int shibie;
    private  String phone, mima;

    private  Intent intent;

    private static final String TAG = "MainActivity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.main_center);

        initView();

        initEvents();

        setSelect(0);
//        startService(intent);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //判断登录
        sharedPreferences = getSharedPreferences("mima", MODE_PRIVATE);
        shibie = sharedPreferences.getInt("shibie", 0);
        if(shibie == 0){
            Log.i(TAG, "onCreate: 跳转登陆界面");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }if(shibie == 1)
        {

            phone = sharedPreferences.getString("phone", "");
            mima = sharedPreferences.getString("mima", "");
            Log.i("###", phone);
            Log.i("***", mima);
            zhijiedenLu(phone, mima);
        }
        tuisong();
    }

    private void tuisong()
    {
//        phone = sharedPreferences.getString("phone", "");
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        JPushInterface.setAlias(this,phone , new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

                Log.i(TAG, "gotResult: 推送别名设置参数" + i);
            }
        });
    }

    private void initEvents() {
        mTabOne.setOnClickListener(this);
        mTabTwo.setOnClickListener(this);
        mTabThree.setOnClickListener(this);

    }


    private void initView() {
        //tabs
        mTabOne = (LinearLayout) findViewById(R.id.tab_one);
        mTabTwo = (LinearLayout) findViewById(R.id.tab_two);
        mTabThree = (LinearLayout) findViewById(R.id.tab_three);
        //ImageButton
        mImgOne = (ImageButton) findViewById(R.id.tab_one_img);
        mImgTwo = (ImageButton) findViewById(R.id.tab_two_img);
        mImgThree = (ImageButton) findViewById(R.id.tab_three_img);

        mTxtOne = (TextView) findViewById(R.id.tab_one_txt);
        mTxtTwo = (TextView) findViewById(R.id.tab_two_txt);
        mTxtThree = (TextView) findViewById(R.id.tab_three_txt);


    }

    private void setSelect(int i) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hidFragment(transaction);
        switch (i) {
            case 0:
                if (homePage == null) {
                    homePage = new MainHomePageFragment();
                    transaction.add(R.id.mian_content, homePage);
                } else {
                    transaction.show(homePage);
                }
                mImgOne.setBackgroundResource(R.mipmap.info_setting);
                mTxtOne.setTextColor(this.getResources().getColor(longin_one));
                break;
            case 1:
                if (server == null) {
                    server = new MainServerFragment();
                    transaction.add(R.id.mian_content, server);
                } else {
                    transaction.show(server);
                }
                mImgTwo.setBackgroundResource(R.mipmap.server_setting);
                mTxtTwo.setTextColor(this.getResources().getColor(longin_one));
                break;
            case 2:
                if (personal == null) {
                    personal = new MainPersonalFragment();
                    transaction.add(R.id.mian_content, personal);
                } else {
                    transaction.show(personal);
                }
                mImgThree.setBackgroundResource(R.mipmap.personal_setting);
                mTxtThree.setTextColor(this.getResources().getColor(longin_one));
                break;
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void hidFragment(FragmentTransaction transaction) {
        if (homePage != null) {
            transaction.hide(homePage);
        }
        if (server != null) {
            transaction.hide(server);
        }
        if (personal != null) {
            transaction.hide(personal);
        }
    }


    @Override
    public void onClick(View v) {
        resetImg();
        switch (v.getId()) {
            case R.id.tab_one:
                setSelect(0);
                break;
            case R.id.tab_two:
                setSelect(1);
                break;
            case R.id.tab_three:
                setSelect(2);
                break;
            default:
                break;
        }

    }

    /**
     * 将图片切换为暗色
     */
    private void resetImg() {
        mImgOne.setBackgroundResource(R.mipmap.info);
        mTxtOne.setTextColor(this.getResources().getColor(R.color.huise));
        mImgTwo.setBackgroundResource(R.mipmap.server);
        mTxtTwo.setTextColor(this.getResources().getColor(R.color.huise));
        mImgThree.setBackgroundResource(R.mipmap.personal);
        mTxtThree.setTextColor(this.getResources().getColor(R.color.huise));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessageDelayed(0, 2000);
                return false;
            } else {
                MainActivity.this.finish();
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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.uriel.car_loading/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.uriel.car_loading/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public void zhijiedenLu(final String phone, final String mima) {
        new Thread(new Runnable() {
            @Override
            synchronized public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://q1.wangzhuanz.com/sign.php");

                    //发送post请求
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("phone", phone));
                    list.add(new BasicNameValuePair("password", mima));
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
                    httpPost.setEntity(entity);
                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    //读回网络返回
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String response = EntityUtils.toString(httpEntity, "utf-8");

                    Log.i("!!!", response);

                    //回传主线程
                    Message message = new Message();
                    message.what = SHOW_RESPONSE2;
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
                case SHOW_RESPONSE2:
                    try {
                        response = (String) msg.obj;
                        JSONObject jsonObject = new JSONObject(response);
                        String phone = jsonObject.getString("message");
                        int code = jsonObject.getInt("code");
                        if (code == 200) {
                            shibie = 1;
                        }
                        else{
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            MainActivity.this.startActivity(intent);
                            MainActivity.this.finish();
                        }
                        Log.i("json内容", phone);
                        //进行UI操作
                        Toast.makeText(MainActivity.this, phone, Toast.LENGTH_SHORT).show();


                        //界面跳转
//                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                        MainActivity.this.startActivity(intent);
//                        MainActivity.this.finish();
                        break;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };



}
