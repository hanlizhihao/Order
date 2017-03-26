package com.hlz.net;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.hlz.order.MyApplication;

/**
 * 网络访问工具类
 * Created by Administrator on 2017/3/26 0026.
 */

public class NetworkUtil {
    private Context context;
    private NetworkUtil(){
        this.context=MyApplication.getContext();
    }
    private RequestQueue queue= Volley.newRequestQueue(context);

    //单例模式
    public static NetworkUtil getNetworkUtil(){
        return NetworkUtilHolder.nInstance;
    }
    private static class NetworkUtilHolder{
        private static final NetworkUtil nInstance=new NetworkUtil();
    }
}
