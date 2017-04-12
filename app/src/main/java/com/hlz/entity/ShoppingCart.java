package com.hlz.entity;

import android.content.Context;

import com.hlz.database.DatabaseUtil;

import java.util.Map;
import java.util.TreeMap;

/**
 * QQ：1430261583
 * Created by Hanlizhi on 2016/10/24.
 * 这里的总价是折扣后的价格，菜单中保存的也要求是折后价
 */

public class ShoppingCart {
    private Map<String,Double> order;//当前使用的菜单
    private Double price=0.0;//订单总价
    private Integer order_size=0;//订单菜品总数
    private Map<String,Integer> order_book;//订菜及其数量

    public ShoppingCart(Context context){
        DatabaseUtil databaseUtil = new DatabaseUtil();
        databaseUtil.DataBaseUtilInit(context);
        this.order= databaseUtil.queryDatabase();
        this.order_book=new TreeMap<>();

    }
    public Map<String, Integer> getOrder_book() {
        return order_book;
    }
    public void setOrder_book(Map<String, Integer> order_book) {
        this.order_book = order_book;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getOrder_size() {
        return order_size;
    }

    public void setOrder_size(Integer order_size) {
        this.order_size = order_size;
    }
    private void renewPrice(double arg){//用于更新菜单的总价
        Double sum=getPrice()+arg;
        setPrice(sum);
    }
    public Integer findSingleSize(String name){//用于返回菜名对应的数量，可能返回null。
        if (order_book.isEmpty()){
            return null;
        }else{
            if (order_book.get(name)==null){
                return null;
            }else{
                return order_book.get(name);
            }
        }
    }
    public boolean addSingleSize(String name){//用于添加菜名对应的数量
        try{
            if (order_book.get(name)==null){
                order_book.put(name,1);
                order_size=order_size+1;
                renewPrice(order.get(name));
                return true;
            }else {
                int value = order_book.get(name);
                value = value + 1;
                order_book.put(name, value);
                order_size = order_size + 1;
                renewPrice(order.get(name));
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteSingleSize(String name){
        try {
            if (order_book.get(name)==null){
                return false;
            }else{
                int value=order_book.get(name);
                value=value-1;
                order_book.put(name,value);
                renewPrice(-order.get(name));
                order_size=order_size-1;
                return true;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
    public Map<String, Double> getOrder() {
        return order;
    }

    public void setOrder(Map<String, Double> order) {
        this.order = order;
    }
    public boolean isEmpty(){
        return order.isEmpty();
    }
}
