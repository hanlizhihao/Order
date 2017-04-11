package com.hlz.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/9 0009
 * 实体类，用于接受订单.
 */

public class Indent implements Serializable{
    private Integer id;
    private String tableId;
    private String reserve;
    private Integer reserveNumber;
    private String fulfill;
    private Integer fulfillNumber;
    private Integer reminderNumber;
    private Long beginTime;
    private Long endTime;
    private Long firstTime;
    private Integer style;
    private Double price;
    public Indent(){

    }
    public Indent(Integer id, String tableId, String reserve, Integer reserveNumber, String fulfill, Integer fulfillNumber, Integer reminderNumber,
                  Long  beginTime, Long  endTime, Long firstTime, Integer style, Double price) {
        this.id = id;
        this.tableId = tableId;
        this.reserve = reserve;
        this.reserveNumber = reserveNumber;
        this.fulfill = fulfill;
        this.fulfillNumber = fulfillNumber;
        this.reminderNumber = reminderNumber;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.firstTime = firstTime;
        this.style = style;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public Integer getReserveNumber() {
        return reserveNumber;
    }

    public void setReserveNumber(Integer reserveNumber) {
        this.reserveNumber = reserveNumber;
    }

    public String getFulfill() {
        return fulfill;
    }

    public void setFulfill(String fulfill) {
        this.fulfill = fulfill;
    }

    public Integer getFulfillNumber() {
        return fulfillNumber;
    }

    public void setFulfillNumber(Integer fulfillNumber) {
        this.fulfillNumber = fulfillNumber;
    }

    public Integer getReminderNumber() {
        return reminderNumber;
    }

    public void setReminderNumber(Integer reminderNumber) {
        this.reminderNumber = reminderNumber;
    }

    public Long  getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long  beginTime) {
        this.beginTime = beginTime;
    }

    public Long  getEndTime() {
        return endTime;
    }

    public void setEndTime(Long  endTime) {
        this.endTime = endTime;
    }

    public Long  getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(Long  firstTime) {
        this.firstTime = firstTime;
    }

    public Integer getStyle() {
        return style;
    }

    public void setStyle(Integer style) {
        this.style = style;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
