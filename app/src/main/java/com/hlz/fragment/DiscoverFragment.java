package com.hlz.fragment;

/**
 * Created by Administrator on 2016/9/8.
 */
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hlz.net.NetworkUtil;
import com.hlz.order.MyApplication;
import com.hlz.order.R;
import com.jiang.android.pbutton.CProgressButton;
import com.tapadoo.alerter.Alerter;

import static android.content.Context.MODE_PRIVATE;

public class DiscoverFragment extends Fragment {
    private final String TAG="DiscoverFragment";
    private CProgressButton progressButton;
    private int id;
    private int state=0;
    private int progress=0;
    public Response.Listener<String> listener=new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            progress++;
            if (s!=null){
                if (s.equals("success")){
                    progressButton.normal(2);
                    state=4;
                    Toast.makeText(getActivity(),"签到完成",Toast.LENGTH_LONG);
                }
            }else{
                progressButton.normal(1);
            }
            progress=0;
        }
    };
    public Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            volleyError.printStackTrace();
            Alerter.create(getActivity())
                    .setBackgroundColor(R.color.colorLightBlue)
                    .setTitle("网络出错了哟！")
                    .setText("是否接入餐厅服务器所在局域网络？")
                    .setDuration(3000)
                    .show();
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discover,container,false) ;
        CProgressButton.initStatusString(new String[]{"签到","暂停","签到完成","错误","正在签到"});
        progressButton = (CProgressButton)v.findViewById(R.id.sign_in);
        progressButton.normal(state);//初始化为签到
        SharedPreferences sp= MyApplication.getContext().getSharedPreferences("appConfig",MODE_PRIVATE);
        id=sp.getInt("id",0);
        progressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(progressButton.getState() != CProgressButton.STATE.NORMAL){
                    progressButton.normal(state);//初始化为签到
                }else {
                    progressButton.startDownLoad();
                    progressButton.download(progress);
                    NetworkUtil networkUtil=NetworkUtil.getNetworkUtil();
                    networkUtil.signIn(Integer.toString(id),listener,errorListener,TAG);
                    Log.d(TAG,"执行了网络请求");
                    progress++;
                    progressButton.download(progress);
                }
            }
        });
        return v ;
    }
}
