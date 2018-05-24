package com.hlz.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hlz.activity.MainActivity;
import com.hlz.entity.WorkTimeModel;
import com.hlz.fragment.DiscoverFragment;
import com.hlz.net.NetworkUtil;
import com.hlz.order.MyApplication;
import com.hlz.order.R;
import com.tapadoo.alerter.Alerter;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * QQ：1430261583
 * Created by Hanlizhi on 2016/10/28.
 * 这是一个监视App运行时长的工具类
 * 调用start方法，判断是否是第一次运行，若第一次，向本地数据写入一个showTime，反之则从本地数据取值将endTime
 * 赋值给startTime，用于重新启动时计算时长showTime。
 * run方法是一个无限循环，它每隔1秒中更新一次showTime数据，signStartOrEnd决定是否跳出循环，在start方法
 * 中设置为true，在end方法中设定为false。
 * end方法用于保存关闭时的时间，并使得run方法中线程结束
 * getShowTime方法用于获取本地数据中的showTime并予以显示，它也会重开线程，并每隔2秒更新一次UI
 */
public class MonitoringTime{
    private long startTime;
    private Boolean signStartOrEnd;
    private SharedPreferences.Editor editor;
    private SharedPreferences monitoring;
    private long showTime;

    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String o) {

        }
    };
    public Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            volleyError.printStackTrace();
            Toast.makeText(MyApplication.getContext(), "网络连接异常",Toast.LENGTH_LONG).show();
        }
    };
    private void Time() {
        try {
            while (signStartOrEnd){
                 showTime = System.currentTimeMillis() - startTime+showTime;
                startTime=System.currentTimeMillis();
                editor.putLong("showTime", showTime);
                editor.commit();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("TAG","计时成功结束");
    }
    public MonitoringTime(Context context){
        monitoring = context.getSharedPreferences("monitoring", Context.MODE_PRIVATE);
        editor= monitoring.edit();
    }
    public void start(){
        this.signStartOrEnd=true;
        Date date=new Date();
        String endTime=monitoring.getString("endTime",null);
        if (endTime!=null){
            //看是否是同一天，如果不是则删除showTime，重新计算
            Boolean nextday=date.toString().substring(0,9).equals(endTime.substring(0,9));
            if (!nextday) {
                editor.remove("showTime");
                editor.commit();
            }
        }
        if (monitoring.contains("showTime")){//检查是否包含showTime
            startTime=System.currentTimeMillis();
            showTime=monitoring.getLong("showTime",0);
        }
        else{
            startTime=System.currentTimeMillis();
            editor.putLong("showTime",0);
            editor.commit();
        }
        if (endTime != null) {
            WorkTimeModel model = new WorkTimeModel();
            SharedPreferences sp= MyApplication.getContext().getSharedPreferences("appConfig",MODE_PRIVATE);
            model.setId(sp.getInt("id",0));
            String time = "";
            long usedTime=monitoring.getLong("showTime",0);
            usedTime=usedTime/1000;
            if (usedTime<60){
                time=Long.toString(usedTime);
                time=time+"秒";
            }
            else
            {
                if (usedTime>=3600)
                {
                    long hour=usedTime/3600;
                    long minute=usedTime%3600/60;
                    long second=usedTime%3600%60;
                    time=Long.toString(hour)+"小时"+Long.toString(minute)+"分钟"+
                            Long.toString(second)+"秒";
                }else
                {
                    long minute=usedTime/60;
                    long second=usedTime%60;
                    time=Long.toString(minute)+"分钟"+Long.toString(second)+"秒";
                }
            }
            model.setTime(time);
            model.setLeaveBeginTime(new Date(endTime));
            model.setLeaveEndTime(new Date(System.currentTimeMillis()));
            long durationTime = model.getLeaveEndTime().getTime() - model.getLeaveBeginTime().getTime();
            String duration = "";
            if (usedTime<60){
                time=Long.toString(usedTime);
                duration=time+"秒";
            }
            else
            {
                if (usedTime>=3600)
                {
                    long hour=usedTime/3600;
                    long minute=usedTime%3600/60;
                    long second=usedTime%3600%60;
                    duration=Long.toString(hour)+"小时"+Long.toString(minute)+"分钟"+
                            Long.toString(second)+"秒";
                }else
                {
                    long minute=usedTime/60;
                    long second=usedTime%60;
                    duration=Long.toString(minute)+"分钟"+Long.toString(second)+"秒";
                }
            }
            model.setDuration(duration);
            NetworkUtil.getNetworkUtil().addWork(model, listener, errorListener, "MonitoringTime");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Time();
            }
        }).start();
        Log.d("TAG","计时开始成功");
    }


    public void end(){
        this.signStartOrEnd=false;
        Date date=new Date();
        editor.putString("endTime",date.toString());
        editor.commit();
    }

    /**
     * 这是一个多线程无限循环发送更新UI线程的消息的方法，
     * @param handler 用于接收消息，处理消息
     */
    public void getShowTime(final Handler handler){
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (true){
                    try {
                        long usedTime=monitoring.getLong("showTime",0);
                        Message msg=new Message();
                        msg.what=0x123;
                        Bundle bundle=new Bundle();
                        usedTime=usedTime/1000;
                        if (usedTime<60){
                            String time=Long.toString(usedTime);
                            time=time+"秒";
                            bundle.putString("showTime",time);
                        }
                        else
                        {
                            if (usedTime>=3600)
                            {
                                long hour=usedTime/3600;
                                long minute=usedTime%3600/60;
                                long second=usedTime%3600%60;
                                String time=Long.toString(hour)+"小时"+Long.toString(minute)+"分钟"+
                                        Long.toString(second)+"秒";
                                bundle.putString("showTime",time);
                            }else
                            {
                                long minute=usedTime/60;
                                long second=usedTime%60;
                                String time=Long.toString(minute)+"分钟"+Long.toString(second)+"秒";
                                bundle.putString("showTime",time);
                            }
                        }
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
