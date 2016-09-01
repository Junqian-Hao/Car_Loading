package com.example.uriel.car_loading.get_car_info;

/**
 * Created by 郝俊谦 on 2016/5/13.
 */

//封装违章信息的类
public class ItemBean {
    public int i;
    public String item_time;
    public String item_location;
    public String item_way;
    public String item_money;
    public String itemm_point;

    public ItemBean(int i, String item_time, String item_location, String item_way, String item_money, String itemm_point) {
        this.i = i;
        this.item_time = item_time;
        this.item_location = item_location;
        this.item_way = item_way;
        this.item_money = item_money;
        this.itemm_point = itemm_point;
    }
}
