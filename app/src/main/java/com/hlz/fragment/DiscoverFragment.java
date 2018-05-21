package com.hlz.fragment;

/**
 * Created by Administrator on 2016/9/8.
 */

import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hlz.activity.MainActivity;
import com.hlz.net.NetworkUtil;
import com.hlz.order.MyApplication;
import com.hlz.order.R;
import com.jiang.android.pbutton.CProgressButton;
import com.tapadoo.alerter.Alerter;

import static android.content.Context.MODE_PRIVATE;

public class DiscoverFragment extends Fragment {
    private final String TAG = "DiscoverFragment";
    private CProgressButton progressButton;
    // 用户id
    private int id;
    private TextView tv;
    private boolean isFinished = false;

    private MainActivity mainActivity;
    public Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            isFinished = s != null && "success".equals(s);
        }
    };
    public Response.ErrorListener errorListener = new Response.ErrorListener() {
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
    public void onResume() {
        Parcelable parcelable = mainActivity.getParcelable();
        if (parcelable != null) {
            progressButton.onRestoreInstanceState(parcelable);
        }
        super.onResume();
    }
    @Override
    public void onPause() {
        Parcelable parcelable = progressButton.onSaveInstanceState();
        mainActivity.setParcelable(parcelable);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        View v = inflater.inflate(R.layout.fragment_discover, container, false);
        tv = (TextView) v.findViewById(R.id.state);
        CProgressButton.initStatusString(new String[]{"签到", "暂停", "签到完成", "错误", "正在签到"});
        progressButton = (CProgressButton) v.findViewById(R.id.sign_in);
        progressButton.normal(0);
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("appConfig", MODE_PRIVATE);
        id = sp.getInt("id", 0);
        progressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                if (progressButton.getState() == CProgressButton.STATE.NORMAL) {
                    valueAnimator.setDuration(5000);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int value = (int) animation.getAnimatedValue();
                            tv.setText("签到进度:" + value);
                            progressButton.download(value);
                            if (value == 100 && isFinished) {
                                tv.setText("可以正常签到");
                                Alerter.create(getActivity())
                                        .setBackgroundColor(R.color.alerter_default_success_background)
                                        .setTitle("签到成功！")
                                        .setText("健康快乐的一天开始了")
                                        .setDuration(5000)
                                        .show();
                                progressButton.normal(2);
                            } else if (value == 100 && !isFinished){
                                tv.setText("签到失败");
                                progressButton.normal(3);
                                Alerter.create(getActivity())
                                        .setBackgroundColor(R.color.alert_default_error_background)
                                        .setTitle("签到失败！")
                                        .setText("是否接入餐厅服务器所在局域网络？")
                                        .setDuration(3000)
                                        .show();
                            }
                        }
                    });
                    progressButton.normal(4);
                    NetworkUtil networkUtil = NetworkUtil.getNetworkUtil();
                    networkUtil.signIn(Integer.toString(id), listener, errorListener, TAG);
                    Log.d(TAG, "执行了网络请求");
                    valueAnimator.start();
                } else {
                    valueAnimator.cancel();
                    progressButton.normal(0);
                    tv.setText("点击签到");
                }
            }
        });
        return v;
    }
}
