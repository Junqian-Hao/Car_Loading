package com.example.uriel.car_loading;

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import com.example.uriel.car_loading.get_car_info.*;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uriel.car_loading.R;

import com.google.gson.Gson;
import com.google.gson.internal.Streams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.*;

//违章查询
public class illgealActivity extends Activity implements Spinner.OnItemSelectedListener {

    private Spinner illgealInfo;
    private SimpleAdapter simpleAdapter;
    private List<Map<String,Object>> dateList;

    private ListView illgealView;
    private SimpleAdapter illgealViewAdapter;
    private List<Map<String,Object>> illgealViewList;
    private TextView illgealSum;
    private TextView pointSum;
    private TextView moneySum;

    private String[] chuli;
    private SharedPreferences sharedPreferences;
    private String phone;

    private ImageView illgealBack;
    private static final String TAG = "illgealActivity";

    String urlTxt = "http://q1.wangzhuanz.com/api/weizhang.php";
    StringBuffer stringBuffer = new StringBuffer();
    List<ItemBean> list2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.illegal);
        //设置标题栏高度
        findViewById(R.id.illegal_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this);
        illgealView = (ListView) findViewById(R.id.illgeal_list);
        dateList = new ArrayList<Map<String,Object>>();
        illgealInfo = (Spinner)findViewById(R.id.illegal_info);
        illgealViewList = new ArrayList<Map<String,Object>>();
        illgealSum = (TextView) findViewById(R.id.illegal_sum);
        pointSum = (TextView) findViewById(R.id.point_sum);
        moneySum = (TextView) findViewById(R.id.money_sum);

        illgealBack = (ImageView) findViewById(R.id.illegal_back);
        illgealBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                illgealActivity.this.finish();
            }
        });

        sharedPreferences = getSharedPreferences("mima", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");

        chaxun();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,Object> map = (HashMap<String,Object>)simpleAdapter.getItem(position);
        illgealViewAdapter = new SimpleAdapter(illgealActivity.this, (List<? extends Map<String, ?>>) ((HashMap<String, Object>) simpleAdapter.getItem(position)).get("weizhangxinxi"),R.layout.illegal_item,new String[] {"itemNumnber","itemTime","itemLocation","itemWay","itemMoney","itemmPoint"},
                new int[] {R.id.item_numnber,R.id.item_time,R.id.item_location,R.id.item_way,R.id.item_money,R.id.itemm_point});

        illgealView.setAdapter(illgealViewAdapter);
        illgealSum.setText(map.get("illgealSum")+"");
        pointSum.setText(map.get("pointSum")+"");
        moneySum.setText(map.get("moneySum")+"");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }








    private void chaxun() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(urlTxt);

                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("phone", phone));
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
                    httpPost.setEntity(entity);

                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity httpEnity = httpResponse.getEntity();
                        String fanhui = EntityUtils.toString(httpEnity, "utf-8");//返回是得到的字符串，里面以@分割了用户某名下所有汽车
                        Log.i("得到的json",fanhui);
                         chuli = fanhui.split("@");//拆成每一辆车的json
                        Log.i("!!!", "json数组长度" + chuli.length);
                        //将chuli里的每一个json进行解析
                        for (int i = 0; i < chuli.length; i++) {
                            int illgealSum = 0;
                            int pointSum = 0;
                            int moneySum = 0;
                            Map<String ,Object> map = new HashMap<String ,Object>();
                            List<Map> list1 = new ArrayList<Map>();
                            Gson gson = new Gson();
                            Yi yi = gson.fromJson(chuli[i], Yi.class);
                            Result a = yi.getResult();
                            map.put("carImage",R.mipmap.car);
                            map.put("carNumber","宝马"+i);
                            map.put("carChejiahao","车牌号：" + a.getLsprefix()+a.getLsnum());

                            Log.i(TAG, "run: 涉牌前缀" + a.getLsprefix() + "车牌剩余" + a.getLsnum());

                            tupian(a.getLsprefix(),a.getLsnum());

                            Log.i("!!!!!!!", String.valueOf(yi.getResult().getList().length));
                            Alist[] b = yi.getResult().getList();
                            stringBuffer.append(  yi.getStatus() + a.getCarorg() + a.getLsnum() + a.getUsercarid());
                            for (int x = 0; x < b.length; x++) {
                                Alist c = b[x];
                                Map<String ,Object> map2 = new HashMap<String ,Object>();
//                                stringBuffegBuffer.append(c.getAddress() + c.getContent() + c.getIllegalidld() + c.getLegalnum() + c.getNumber() + c.getPrice() + c.getScore() + c.getTime());
//                                list2.add(new ItemBean(x+1,c.getTime(),c.getAddress(),c.getContent(),c.getPrice(),c.getScore()));
                                map2.put("itemNumnber",x+1);
                                map2.put("itemTime",c.getTime());
                                map2.put("itemLocation",c.getAddress());
                                map2.put("itemWay",c.getContent());
                                map2.put("itemMoney",c.getPrice());
                                map2.put("itemmPoint",c.getScore());
                                list1.add(map2);
                                illgealSum ++;
                                pointSum +=Integer.parseInt(c.getScore());
                                moneySum +=Integer.parseInt(c.getPrice());

                            }
                            map.put("weizhangxinxi", list1);
                            map.put("illgealSum",illgealSum);
                            map.put("pointSum",pointSum);
                            map.put("moneySum",moneySum);
                            dateList.add(map);
                            Message message = new Message();
                            message.what = 0;
                            message.obj = stringBuffer.toString();
                            handler.sendMessage(message);
                        }
                    } else {
                        Toast.makeText(illgealActivity.this, "网络异常，请稍候再试", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
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
                    String asd = (String) msg.obj;

                    simpleAdapter = new SimpleAdapter(illgealActivity.this,dateList,R.layout.car_item,new String[] {"carImage","carNumber","carChejiahao"},
                            new int[] {R.id.car_image,R.id.car_number,R.id.chejiahao});
                    simpleAdapter.setDropDownViewResource(R.layout.car_item);
                    illgealInfo.setAdapter(simpleAdapter);
                    illgealInfo.setOnItemSelectedListener(illgealActivity.this);
                    break;
                case 1:

                    //这是图片的uri地址
                    String result = (String) msg.obj;


                    Log.i(TAG, "handleMessage: 图片返回" + result);
                    break;
            }
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            illgealActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void tupian(final String lat, final String lon) {
        new Thread(new Runnable() {
            @Override
            synchronized public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://q1.wangzhuanz.com/getImg.php");

                    //发送post请求
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("lsprefix", lat));
                    list.add(new BasicNameValuePair("lsnum", lon));
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
                    httpPost.setEntity(entity);
                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    //读回网络返回
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String response = EntityUtils.toString(httpEntity, "utf-8");

                    Log.i("!!!", response);

                    //回传主线程
                    Message message = new Message();
                    message.what = 1;
                    message.obj = response;
                    handler.sendMessage(message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
