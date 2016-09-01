package com.example.uriel.car_loading.duiying;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 郝俊谦 on 2016/5/5.
 */
public class Cheliangleixin {
    Map<String, String> map = new HashMap<String, String>();

    private void jianli() {
        map.put("小型汽车号牌", "02");
        map.put("大型汽车号牌", "01");
        map.put("使馆汽车号牌", "03");
        map.put("领馆汽车号牌", "04");
        map.put("境外汽车号牌", "05");
        map.put("外籍汽车号牌", "06");
        map.put("两、三轮摩托车号牌", "07");
        map.put("轻便摩托车号牌", "08");
        map.put("使馆摩托车号牌", "09");
        map.put("领馆摩托车号牌", "10");
        map.put("境外摩托车号牌", "11");
        map.put("外籍摩托车号牌", "12");
        map.put("农用运输车号牌", "13");
    }
    public String chaxun(String name)
    {
        jianli();
        return map.get(name);
    }
}
