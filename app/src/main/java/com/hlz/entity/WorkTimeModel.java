package com.hlz.entity;

import java.util.Date;

/**
 * Created by hlz on 2018/5/25.
 */

public class WorkTimeModel {

    //用户id
    private int id;

    /**
     * 设备运行时间
     */
    private String time;
    // 离开开始时间
    private Date leaveBeginTime;

    private Date leaveEndTime;

    /**
     * 离开持续时间
     */
    private String duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getLeaveBeginTime() {
        return leaveBeginTime;
    }

    public void setLeaveBeginTime(Date leaveBeginTime) {
        this.leaveBeginTime = leaveBeginTime;
    }

    public Date getLeaveEndTime() {
        return leaveEndTime;
    }

    public void setLeaveEndTime(Date leaveEndTime) {
        this.leaveEndTime = leaveEndTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
