package com.hlz.entity;

/**用于读取菜单的类
 * Created by Administrator on 2017/3/27 0027.
 */

public class Menu {
    private Integer id;
    private String greensName;
    private Double price;
    private String dbVersion;
    public Menu(){

    }
    public Menu(Integer id, String greensName, Double price, String dbVersion) {
        this.id = id;
        this.greensName = greensName;
        this.price = price;
        this.dbVersion = dbVersion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDbVersion() {
        return dbVersion;
    }

    public void setDbVersion(String dbVersion) {
        this.dbVersion = dbVersion;
    }

    public String getGreensName() {
        return greensName;
    }

    public void setGreensName(String greensName) {
        this.greensName = greensName;
    }
}
