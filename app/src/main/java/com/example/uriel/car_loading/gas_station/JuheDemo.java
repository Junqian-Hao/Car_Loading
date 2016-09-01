package com.example.uriel.car_loading.gas_station;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.*;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class JuheDemo {
	public static final String DEF_CHATSET = "UTF-8";
	public static final int DEF_CONN_TIMEOUT = 30000;
	public static final int DEF_READ_TIMEOUT = 30000;
	public static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";

	// 配置您申请的KEY
	public static final String APPKEY = "4e07ef68782261188b687732fe8799a0";

	// 1.按城市检索加油站
	public static void getRequest1() throws Exception {
	        String result = null;
	        String url = "http://apis.juhe.cn/oil/region";//请求接口地址
	        Map params = new HashMap();//请求参数
	        params.put("city", "太原");//城市名urlencode utf8;
	        params.put("keywords", "");//关键字urlencode utf8;
	        params.put("page", "");//页数，默认1
	        params.put("format", "");//格式选择1或2，默认1
	        params.put("key", APPKEY);//应用APPKEY(应用详细页查询)
	        System.out.println(net(url, params, "GET"));
	        Gson gson = new Gson();
	        Json json = gson.fromJson(net(url, params, "GET"),Json.class);
	        System.out.println(json.reason);
	}
	    

public static void main(String[] args) throws Exception {
getRequest1();	
}


	    /**
	     * @param strUrl 请求地址
	     * @param params 请求参数
	     * @param method 请求方法
	     * @return 网络请求字符串
	     * @throws Exception
	     */
	    @TargetApi(Build.VERSION_CODES.KITKAT)
		public static String net(String strUrl, Map params, String method) throws Exception {
	        HttpURLConnection conn = null;
	        BufferedReader reader = null;
	        String rs = null;
	        try {
	            StringBuffer sb = new StringBuffer();
	            if (method == null || method.equals("GET")) {
	                strUrl = strUrl + "?" + urlencode(params);
	            }
	            URL url = new URL(strUrl);
	            conn = (HttpURLConnection) url.openConnection();
	            if (method == null || method.equals("GET")) {
	                conn.setRequestMethod("GET");
	            } else {
	                conn.setRequestMethod("POST");
	                conn.setDoOutput(true);
	            }
	            conn.setRequestProperty("User-agent", userAgent);
	            conn.setUseCaches(false);
	            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
	            conn.setReadTimeout(DEF_READ_TIMEOUT);
	            conn.setInstanceFollowRedirects(false);
	            conn.connect();
	            if (params != null && method.equals("POST")) {
	                try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
	                    out.writeBytes(urlencode(params));
	                }
	            }
	            InputStream is = conn.getInputStream();
	            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
	            String strRead = null;
	            while ((strRead = reader.readLine()) != null) {
	                sb.append(strRead);
	            }
	            rs = sb.toString();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (reader != null) {
	                reader.close();
	            }
	            if (conn != null) {
	                conn.disconnect();
	            }
	        }
	        return rs;
	    }

	    //将map型转为请求参数型
	    public static String urlencode(Map<String, Object> data) {
	        StringBuilder sb = new StringBuilder();
	        for (Map.Entry i : data.entrySet()) {
	            try {
	                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
	            } catch (UnsupportedEncodingException e) {
	                e.printStackTrace();
	            }
	        }
	        return sb.toString();
	    }

}
