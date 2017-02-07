package com.hlz.entity;

/**
 * Created by hlz on 2016/10/5.
 */

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestDinnerTable {
    /**
     * 此类为进度页面的Adapter的数据源
     * 网络连接的数据可以存储到该实体
     * */
    private List<DinnerTable> dinnerTables=new ArrayList<>();

    public  TestDinnerTable(){
        String[] table1_book = new String[]{"鱼香肉丝", "尖椒炒肉", "招牌牛肉饭"};
        /**
         * 将订单类型改为了Map存储
         */
        Map<String,Integer> OrderBook=new LinkedHashMap<>();
        OrderBook.put(table1_book[0],1);
        OrderBook.put(table1_book[1],2);
        OrderBook.put(table1_book[2],5);
        String[] table1_serve = new String[]{"鱼香肉丝", "尖椒炒肉"};
        Map<String,Integer> OderServe=new LinkedHashMap<>();
        OderServe.put(table1_serve[0],1);
        OderServe.put(table1_serve[1],2);
        DinnerTable dt1=new DinnerTable(1,"a1",3,1,"18:30:55", OrderBook, OderServe,20);
        String[] table2_book = new String[]{"韩式泡菜", "土豆泥", "紫菜汤"};
        Map<String,Integer> Table2_OderBook=new LinkedHashMap<>();
        Table2_OderBook.put(table2_book[0],1);
        Table2_OderBook.put(table2_book[1],2);
        Table2_OderBook.put(table2_book[2],1);
        Map<String,Integer> Table2_OderServe=new LinkedHashMap<>();
        String[] table2_serve = new String[]{"土豆泥"};
        Table2_OderServe.put(table2_serve[0],2);
        DinnerTable dt2=new DinnerTable(2,"a2",2,0,"18:40:10", Table2_OderBook,Table2_OderServe,10);
        dinnerTables.add(dt1);dinnerTables.add(dt2);
        //编号，桌号，用餐人数，催单次数，下单时间，订单，已经上的菜，等待时间
    }

    public List<DinnerTable> getDinnerTables() {
        return dinnerTables;
    }

    public void setDinnerTables(List<DinnerTable> dinnerTables) {
        this.dinnerTables = dinnerTables;
    }
}
