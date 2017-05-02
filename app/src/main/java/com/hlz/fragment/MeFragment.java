package com.hlz.fragment;

/**
 * QQ：1430261583
 * Created by Hanlizhi on 2016/9/8.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;
import com.hlz.activity.MainActivity;
import com.hlz.net.NetworkUtil;
import com.hlz.order.LoginActivity;
import com.hlz.order.MyApplication;
import com.hlz.order.R;
import com.hlz.util.MonitoringTime;
import com.tapadoo.alerter.Alerter;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.content.Context.MODE_PRIVATE;

public class MeFragment extends Fragment {
    @InjectView(R.id.user_name)
    TextView userNameText;
    @InjectView(R.id.realName)
    TextView realNameText;
    @InjectView(R.id.check_number)
    TextView checkNumber;
    private String name;
    private String username;
    private String signNumber;
    private final String TAG="meFragment";
    public Response.Listener<String> listener=new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            signNumber=s;
            userNameText.setText(username);
            realNameText.setText(name);
            checkNumber.setText(signNumber);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_me, container, false);
        final HTextView timeUsed = (HTextView) v.findViewById(R.id.timeused);
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123) {
                    String showTime = msg.getData().getString("showTime");
                    timeUsed.setAnimateType(HTextViewType.RAINBOW);//TYPER
                    timeUsed.animateText(showTime);
                }
            }
        };
        MonitoringTime monitoringTime = new MonitoringTime(getActivity());
        monitoringTime.getShowTime(handler);
        SharedPreferences sp= MyApplication.getContext().getSharedPreferences("appConfig",MODE_PRIVATE);
        name=sp.getString("name","");
        username=sp.getString("username","");
        int id=sp.getInt("id",0);
        if (id==0){
            Toast.makeText(getActivity(),"获取用户信息失败",Toast.LENGTH_LONG).show();
        }else{
            NetworkUtil networkUtil=NetworkUtil.getNetworkUtil();
            networkUtil.getSigns(Integer.toString(id),listener,errorListener,TAG);
        }
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
