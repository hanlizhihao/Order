package com.hlz.entity;

import java.util.List;

/**
 * QQ：1430261583
 * Created by Hanlizhi on 2016/10/25.
 * 这是一个菜单的类，它要存储，每次从数据库查询到的菜单，菜单版本号，从MobileAPI返回的菜单版本号
 * 还可能会存储返回的新菜单，并更新数据库。
 * 除此以外要包括的功能函数有：1、向购物车添加菜品；2、价格查询函数，根据菜名name返回一个它的价格；
 * 3、比对版本号；4、向数据库写入新菜单；
 */
public class Menus {
    private List<Menu>  menus;
    public Menus(){

    }
    public Menus(List<Menu> menus) {
        this.menus = menus;
    }

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }
}
