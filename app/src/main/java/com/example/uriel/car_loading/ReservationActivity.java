package com.example.uriel.car_loading;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.List;

/**
 * Created by uriel on 2016/5/21.
 * 预约加油界面
 */
public class ReservationActivity extends Activity implements OnClickListener{
    private EditText nameInput;
    private TextView timeInput;
    private EditText stationInput;
    private Spinner typeInput;
    private EditText oilInput;
    private Button reservationDetermine;

    private Calendar cal;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private String time,time1,time2;

    private ImageView reservationBack;

    //预约加油所属
    private static final String TAG = "ReservationActivity";
    private SharedPreferences sharedPreferences;
    private CheckBox invoice;
    private int fapiao;
    private final String urlTxt = "http://q1.wangzhuanz.com/userOrder.php";
    private  String youlei ;
    private String phone;

    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView( R.layout.reservation);

        sharedPreferences = getSharedPreferences("mima", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");

        initView();

        initEvents();
    }

    private void initView() {
        nameInput = (EditText)findViewById(R.id.name_input);
        timeInput = (TextView) findViewById(R.id.time_input);
        stationInput = (EditText)findViewById(R.id.station_input);
        typeInput = (Spinner)findViewById(R.id.type_input);
        oilInput = (EditText)findViewById(R.id.oil_input);
        reservationDetermine = (Button)findViewById(R.id.reservation_determine);
        invoice = (CheckBox) findViewById(R.id.invoice);

        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        time = year+"年"+(month+1)+"月"+day+"日"+hour+"时"+minute+"分";
        timeInput.setText(time);

        reservationBack = (ImageView) findViewById(R.id.reservation_back);
    }

    private void initEvents() {
        //设置标题栏高度
        findViewById(R.id.reservation_top).getLayoutParams().height = StatusBarHeight.getStatusHeight(this);
        nameInput.setOnClickListener(this);
        timeInput.setOnClickListener(this);
        stationInput.setOnClickListener(this);
        oilInput.setOnClickListener(this);
        reservationDetermine.setOnClickListener(this);

        reservationBack.setOnClickListener(this);

        invoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    fapiao = 1;
                    Log.i("@@", String.valueOf(fapiao));

                }else {
                    fapiao = 0;
                    Log.i("@@", String.valueOf(fapiao));
                }
            }
        });
        typeInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                youlei = ReservationActivity.this.getResources().getStringArray(R.array.youpinzhonglei)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.time_input:
                time = "";
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        time1 = hourOfDay+"时"+minute+"分";
                        timeInput.setText(time2+time1);
                    }
                },hour,minute,true ).show();
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        time2 = year+"年"+(monthOfYear+1)+"月"+dayOfMonth+"日";
                        timeInput.setText(time2+time1);
                    }
                },year,month,day).show();
                break;
            case R.id.reservation_determine:
                //确定按钮点击事件
                tijiaodingdan();
                break;
            case R.id.reservation_back:
                ReservationActivity.this.finish();
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ReservationActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    //上传数据
    private void tijiaodingdan()
    {
        new Thread(new Runnable() {
            @Override
            public void run
                    () {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(urlTxt);

                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("phone",phone));
                    list.add(new BasicNameValuePair("name",nameInput.getText().toString() ));
                    list.add(new BasicNameValuePair("date", year + "-" + month + "-" + day));
                    list.add(new BasicNameValuePair("time", hour + "-" + minute));
                    list.add(new BasicNameValuePair("youzhan", stationInput.getText().toString()));
                    list.add(new BasicNameValuePair("youlei", youlei));
                    list.add(new BasicNameValuePair("price", oilInput.getText().toString()));
                    list.add(new BasicNameValuePair("fapiao", String.valueOf(fapiao)));
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "utf-8");
                    httpPost.setEntity(entity);

                    HttpResponse httpResponse = httpClient.execute(httpPost);
//                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity httpEntity = httpResponse.getEntity();
                    String reposnse = EntityUtils.toString(httpEntity, "utf-8");

                    Log.i("!!!", reposnse);

                    Message message = new Message();
                    message.what = 0;
                    message.obj = reposnse;
                    handler.sendMessage(message);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 0:
                        String a = (String) msg.obj;
                        Log.i("!!!", a);
                        JSONObject jsonObject = new JSONObject(a);

                        Toast.makeText(ReservationActivity.this,jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
//                    Bitmap bitmaap = com.xys.libzxing.zxing.encoding.EncodingUtils.createQRCode(a, 300, 300, null);
//                    imageView.setImageBitmap(bitmaap);
                        break;
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };
}
