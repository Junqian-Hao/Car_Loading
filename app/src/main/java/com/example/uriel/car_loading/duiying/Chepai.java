package com.example.uriel.car_loading.duiying;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 郝俊谦 on 2016/5/5.
 */
public class Chepai {
    private Map<String, String> map = new HashMap<String, String>();

    private void jianli() {
        map.put("京", "beijing");
        map.put("皖", "anhui");
        map.put("闽", "fujian");
        map.put("甘", "");
        map.put("粤", "guangdong");
        map.put("桂", "");
        map.put("贵", "guizhou");
        map.put("琼", "hainan");
        map.put("冀", "hebei");
        map.put("豫", "henan");
        map.put("黑", "heilongjiang");
        map.put("鄂", "hubei");
        map.put("湘", "");
        map.put("吉", "jilin");
        map.put("苏", "jiangsu");
        map.put("赣", "jiangxi");
        map.put("辽", "");
        map.put("蒙", "");
        map.put("宁", "ningxia");
        map.put("青", "qinghai");
        map.put("鲁", "shandong");
        map.put("晋", "shanxi");
        map.put("陕", "");
        map.put("沪", "shanghai");
        map.put("川", "sichuan");
        map.put("津", "tianjin");
        map.put("藏", "");
        map.put("新", "xinjiang");
        map.put("云", "yunnan");
        map.put("浙", "zhejiang");
        map.put("渝", "chongqing");
    }

    public String chaxun(String name)
    {
        jianli();
        return map.get(name);
    }

}
