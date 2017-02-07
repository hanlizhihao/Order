package com.hlz.entity;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Hanlizhi on 2016/10/5.
 */

public class DinnerTable {
    private int id=0;
    private String table_id;//桌号
    private int number_people=0;//就餐人数
    private Map<String,Integer> order_book;//预订的菜
    private Map<String,Integer> order_serve;//已经上的菜
    /**
     * 这个代表当前应该显示的可扩展的子组件的内容
     * 这个字符串部分应该是预定菜的菜名
     * value值部分是已经上菜的key值数量
     * 当出现book和serve的菜名不完全相等时，serve没有的部分value值应该为0
     */
    private Map<String,Integer> order_display;
    private String time_order;//下单时间
    private int time_wait;//等待时间
    private int number_reminder=0;//催单次数


    DinnerTable(int id,String table_id,int number_people,int number_reminder,String time_order,Map<String,Integer>
                order_book,Map<String,Integer> order_serve,int time_wait){
        this.id=id;
        this.table_id=table_id;
        this.number_people=number_people;
        this.number_reminder=number_reminder;
        this.time_order=time_order;
        this.order_book=order_book;
        this.order_serve=order_serve;
        this.time_wait=time_wait;
        /**
         * 这里利用的Map的覆盖特性
         * 首先，让display读取book，然后再putAll（serve），由于map的key值不允许重复
         * 所以key值还是book的key值，也就是菜名没变，但是要求book和serve不同的部分的value要设置为0
         * 所以应该对order_display进行进一步处理
         */
        Map<String,Integer> order_display=new TreeMap<>();
        order_display.putAll(order_book);
        for (Map.Entry<String,Integer> entry:order_display.entrySet()
             ) {
            entry.setValue(0);
        }
        order_display.putAll(order_serve);
        this.order_display=order_display;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTable_id() {
        return table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public int getNumber_people() {
        return number_people;
    }

    public void setNumber_people(int number_people) {
        this.number_people = number_people;
    }

    public Map<String,Integer> getOrder_book() {
        return order_book;
    }

    public void setOrder_book(Map<String,Integer> order_book) {
        this.order_book = order_book;
    }

    public Map<String,Integer> getOrder_serve() {
        return order_serve;
    }

    public void setOrder_serve(Map<String,Integer> order_serve) {
        this.order_serve = order_serve;
    }

    public String getTime_order() {
        return time_order;
    }

    public void setTime_order(String time_order) {
        this.time_order = time_order;
    }

    public int getTime_wait() {
        return time_wait;
    }

    public void setTime_wait(int time_wait) {
        this.time_wait = time_wait;
    }

    public int getNumber_reminder() {
        return number_reminder;
    }

    public void setNumber_reminder(int number_reminder) {
        this.number_reminder = number_reminder;
    }

    public Map<String, Integer> getOrder_display() {
        return order_display;
    }

    public void setOrder_display(Map<String, Integer> order_display) {
        this.order_display = order_display;
    }
}
