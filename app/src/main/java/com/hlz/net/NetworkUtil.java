package com.hlz.net;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hlz.entity.Indent;
import com.hlz.entity.IndentModel;
import com.hlz.order.MyApplication;
import com.hlz.order.R;

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
                Map<String, String> map = new HashMap<>();
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
    public void getUnderwayOrder(Response.Listener<String> listener, Response.ErrorListener errorListener, String TAG){
        StringRequest request=new StringRequest(Request.Method.GET,urlManager.findURL(context,"underway").getUrl(),listener,errorListener);
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
                if (indent.getFirstTime()!=null){
                    map.put("time", indent.getFirstTime().toString());
                }else{
                    map.put("time","");
                }
                return map;
            }
        };
        request.setTag(TAG);
        queue.add(request);
    }
    public void finishedIndent(String id,Response.Listener<String> listener,Response.ErrorListener errorListener,String TAG){
        StringRequest request=new StringRequest(Request.Method.GET,urlManager.findURL(context,"finishedIndentNoTelephone").getUrl()+id,listener,
                errorListener);
        request.setTag(TAG);
        queue.add(request);
    }
    public void finishedIndent(String id, final String telephone, final String price, Response.Listener<String> listener, Response.ErrorListener
            errorListener, String TAG){
        StringRequest request=new StringRequest(Request.Method.POST,urlManager.findURL(context,"finishedIndent").getUrl()+id,listener,errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("telephone", telephone);
                map.put("price",price);
                return map;
            }
        };
        request.setTag(TAG);
        queue.add(request);
    }
    public void validateTelephone(final String telephone, Response.Listener<String> listener, Response.ErrorListener errorListener, String TAG){
        StringRequest request=new StringRequest(Request.Method.POST,urlManager.findURL(context,"validateTelephone").getUrl(),listener,errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("telephone", telephone);
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
    //id为页码,获取已完成的数据集
    public void getFinished(String id,Response.Listener<String> listener,Response.ErrorListener errorListener,String TAG){
        StringRequest request;
        if (id==null){
            request=new StringRequest(Request.Method.GET,urlManager.findURL(context,"finished").getUrl()+"1",listener,errorListener);
        }else{
            request=new StringRequest(Request.Method.GET,urlManager.findURL(context,"finished").getUrl()+id,listener,errorListener);
        }
        request.setTag(TAG);
        queue.add(request);
    }
    public void getCanceled(String id,Response.Listener<String> listener,Response.ErrorListener errorListener,String TAG){
        StringRequest request;
        if (id==null){
            request=new StringRequest(Request.Method.GET,urlManager.findURL(context,"canceled").getUrl()+"1",listener,errorListener);
        }else{
            request=new StringRequest(Request.Method.GET,urlManager.findURL(context,"canceled").getUrl()+id,listener,errorListener);
        }
        request.setTag(TAG);
        queue.add(request);
    }
    public void createIndent(final IndentModel model, Response.Listener<String> listener, Response.ErrorListener errorListener, String TAG){
        StringRequest request=new StringRequest(Request.Method.POST,urlManager.findURL(context,"addIndent").getUrl(),listener,errorListener){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("table",model.getTable());
                Gson gson=new Gson();
                String reserve=gson.toJson(model.getReserve());
                System.out.println(reserve);
                map.put("reserve",reserve);
                map.put("price",Double.toString(model.getPrice()));
                return map;
            }
        };
        request.setTag(TAG);
        queue.add(request);
    }
    public void getSigns(String id,Response.Listener<String> listener,Response.ErrorListener errorListener,String TAG){
        StringRequest request=new StringRequest(Request.Method.GET,urlManager.findURL(context,"getSigns").getUrl()+id,listener,errorListener);
        request.setTag(TAG);
        queue.add(request);
    }
    public void signIn(String id,Response.Listener<String> listener,Response.ErrorListener errorListener,String TAG){
        StringRequest request=new StringRequest(Request.Method.GET,urlManager.findURL(context,"sign").getUrl()+id,listener,errorListener);
        request.setTag(TAG);
        queue.add(request);
    }
    public void signOut(String id, Response.Listener<String> listener, Response.ErrorListener errorListener, String TAG) {
        StringRequest request = new StringRequest(Request.Method.GET, urlManager.findURL(context, "signOut").getUrl()+id, listener, errorListener);
        request.setTag(TAG);
        queue.add(request);
    }
    public void canceledRequest(String TAG){
        queue.cancelAll(TAG);
    }
}
