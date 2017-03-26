package com.hlz.order;

import android.app.Application;
import android.content.Context;

/**
 * Created by hlz on 2017/2/7 0007
 * 10:38 //存储全局变量
 */

public class MyApplication extends Application{
    public static Context context;
    public static Context getContext(){
        return context;
    }
    @Override
    public void onCreate(){
        context=this;
    }
}
