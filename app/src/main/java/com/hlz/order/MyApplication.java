package com.hlz.order;

import android.app.Application;
import android.content.Context;

import com.hlz.util.AppManager;
import com.hlz.util.MonitoringTime;

/**
 * Created by hlz on 2017/2/7 0007
 * 10:38 //存储全局变量
 */

public class MyApplication extends Application{
    private static MonitoringTime monitoringTime;//监视使用时间
    private static Context context;
    public static Context getContext(){
        return context;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        context=this;
        //初始化监视APP使用时间的类
        monitoringTime=new MonitoringTime(context);
    }
    public static MonitoringTime getMonitoringTime(){
        return monitoringTime;
    }
}
