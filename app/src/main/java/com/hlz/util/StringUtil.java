package com.hlz.util;

import com.hlz.activity.UnderwayDetailsActivity;

import java.util.List;

/**
 * Created by hlz on 2017/4/9
 */

public class StringUtil {
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
    public static String[] fromListToString(List<UnderwayDetailsActivity.IndentMenu> indentMenus){
        String[] result=new String[2];
        for (UnderwayDetailsActivity.IndentMenu indentMenu:indentMenus){
            result[0]=result[0]+indentMenu.getName()+"a"+indentMenu.getReserveNumber()+"e";
            result[1]=result[1]+indentMenu.getName()+"a"+indentMenu.getFulfillNumber()+"e";
        }
        return result;
    }
}
