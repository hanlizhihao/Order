package com.hlz.entity;

/**
 *保存购物车每一条数据
 * Created by hlz on 2017/4/13 0013.
 */

public class ItemShoppingCart {
    private String name;
    private double price;
    private String number;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
