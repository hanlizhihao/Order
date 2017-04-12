package com.hlz.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hlz.entity.Indent;
import com.hlz.entity.Menu;
import com.hlz.order.MyApplication;
import com.tapadoo.alerter.Alert;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 网络访问工具类
 * Created by Administrator on 2017/3/26 0026.
 */

public class NetworkUtil {
    private Context context;
    private RequestQueue queue;
    private UrlManager urlManager;
    private String url="可能会变的ip地址";
    private NetworkUtil(){
        this.context=MyApplication.getContext();
        queue=Volley.newRequestQueue(MyApplication.getContext());
        urlManager=UrlManager.getUrlManager();
    }
    //单例模式
    public static NetworkUtil getNetworkUtil(){
        return NetworkUtilHolder.nInstance;
    }
    private static class NetworkUtilHolder{
        private static final NetworkUtil nInstance=new NetworkUtil();
    }
    /**
     * 网络请求的方法集
     */
    //loginActivity需要的方法
    public void login(final String username, final String password, Response.Listener<String> listener, Response.ErrorListener errorListener, String TAG){
        StringRequest request=new StringRequest(Request.Method.POST,urlManager.findURL(context,"login").getUrl(),listener,errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", username);
                map.put("password", password);
                return map;
            }
        };
        request.setTag(TAG);
        queue.add(request);
    }
    //空值为错，有值为正确返回的结果，函数将返回版本信息
    public void getMenuVersion(Response.Listener<String> listener,Response.ErrorListener errorListener, String TAG){
        StringRequest request=new StringRequest(Request.Method.GET, urlManager.findURL(context, "version").getUrl(),listener,errorListener);
        request.setTag(TAG);
        queue.add(request);
    }
    //向服务器端请求菜单
    public void getMenu(Response.Listener<String> listener,Response.ErrorListener errorListener,String TAG){
        StringRequest request=new StringRequest(Request.Method.GET, urlManager.findURL(context, "menus").getUrl(),listener,errorListener);
        request.setTag(TAG);
        queue.add(request);
    }
    public void getUnderwayOrder(Response.Listener<JSONArray> listener, Response.ErrorListener errorListener, String TAG){
        JsonArrayRequest request=new JsonArrayRequest(urlManager.findURL(context,"underway").getUrl(),listener,errorListener);
        request.setTag(TAG);
        queue.add(request);
    }
    public void updateIndent(final Indent indent, Response.Listener<String> listener, Response.ErrorListener errorListener, String TAG){
        StringRequest request=new StringRequest(Request.Method.POST,urlManager.findURL(context,"indent/update").getUrl(),listener,errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("id", indent.getId().toString());
                map.put("reserve",indent.getReserve());
                map.put("fulfill", indent.getFulfill());
                map.put("table", indent.getTableId());
                map.put("reminderNumber", indent.getReminderNumber().toString());
                map.put("price", indent.getPrice().toString());
                map.put("time", indent.getFirstTime().toString());
                return map;
            }
        };
        request.setTag(TAG);
        queue.add(request);
    }
    public void getSingleIndent(String id,Response.Listener<String> listener,Response.ErrorListener errorListener,String TAG){
        StringRequest request=new StringRequest(Request.Method.GET,urlManager.findURL(context,"indent/id").getUrl()+id,listener,errorListener);
        request.setTag(TAG);
        queue.add(request);
    }
    public void reminder(String id,Response.Listener<String> listener,Response.ErrorListener errorListener,String TAG){
        StringRequest request=new StringRequest(Request.Method.GET,urlManager.findURL(context,"reminder").getUrl()+id,listener,errorListener);
        request.setTag(TAG);
        queue.add(request);
    }
    public void canceledRequest(String TAG){
        queue.cancelAll(TAG);
    }
}
