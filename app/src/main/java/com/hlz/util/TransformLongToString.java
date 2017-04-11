package com.hlz.util;

/**
 * Created by hlz on 2017/4/9
 */

public class TransformLongToString {
    public static String change(Long usedTime){
        String time;
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
        return time;
    }
}
