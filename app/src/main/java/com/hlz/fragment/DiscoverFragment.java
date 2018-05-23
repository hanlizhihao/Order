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
    private CProgressButton signInProgressButton;
    private CProgressButton signOutProgressButton;
    // 用户id
    private int id;
    private TextView signInState;
    private TextView signOutState;
    public static boolean signInIsFinished = false;
    public static boolean signOutIsFinished = false;

    private MainActivity mainActivity;
    public Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            signInIsFinished = s != null && "success".equals(s);
        }
    };
    public Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            DiscoverFragment.signInIsFinished = false;
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
            signInProgressButton.onRestoreInstanceState(parcelable);
        }
        super.onResume();
    }
    @Override
    public void onPause() {
        Parcelable parcelable = signInProgressButton.onSaveInstanceState();
        mainActivity.setParcelable(parcelable);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        View v = inflater.inflate(R.layout.fragment_discover, container, false);
        signInState = (TextView) v.findViewById(R.id.sign_in_state);
        signOutState = (TextView) v.findViewById(R.id.sign_out_state);
        CProgressButton.initStatusString(new String[]{"签到", "签退", "签到完成", "错误", "正在签到", "正在签退", "签退完成"});
        signInProgressButton = (CProgressButton) v.findViewById(R.id.sign_in);
        signOutProgressButton = (CProgressButton) v.findViewById(R.id.sign_out);
        signInProgressButton.normal(0);
        signOutProgressButton.normal(1);
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("appConfig", MODE_PRIVATE);
        id = sp.getInt("id", 0);
        signInProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                if (signInProgressButton.getState() == CProgressButton.STATE.NORMAL) {
                    valueAnimator.setDuration(5000);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int value = (int) animation.getAnimatedValue();
                            signInState.setText("签到进度:" + value);
                            signInProgressButton.download(value);
                            if (value == 100 && DiscoverFragment.signInIsFinished) {
                                signInState.setText("可以正常签到");
                                Alerter.create(getActivity())
                                        .setBackgroundColor(R.color.alerter_default_success_background)
                                        .setTitle("签到成功！")
                                        .setText("健康快乐的一天开始了")
                                        .setDuration(5000)
                                        .show();
                                signInProgressButton.normal(2);
                            } else if (value == 100 && !DiscoverFragment.signInIsFinished){
                                signInState.setText("签到失败");
                                signInProgressButton.normal(3);
                                Alerter.create(getActivity())
                                        .setBackgroundColor(R.color.alert_default_error_background)
                                        .setTitle("签到失败！")
                                        .setText("是否接入餐厅服务器所在局域网络？")
                                        .setDuration(3000)
                                        .show();
                            }
                        }
                    });
                    signInProgressButton.normal(4);
                    NetworkUtil networkUtil = NetworkUtil.getNetworkUtil();
                    networkUtil.signIn(Integer.toString(id), listener, errorListener, TAG);
                    Log.d(TAG, "执行了网络请求");
                    valueAnimator.start();
                } else {
                    valueAnimator.cancel();
                    signInProgressButton.normal(0);
                    signInState.setText("点击签到");
                }
            }
        });
        signOutProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                if (signOutProgressButton.getState() == CProgressButton.STATE.NORMAL) {
                    valueAnimator.setDuration(5000);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int value = (int) animation.getAnimatedValue();
                            signOutState.setText("签退进度:" + value);
                            signOutProgressButton.download(value);
                            if (value == 100 && DiscoverFragment.signOutIsFinished) {
                                signOutState.setText("可以正常签退");
                                Alerter.create(getActivity())
                                        .setBackgroundColor(R.color.alerter_default_success_background)
                                        .setTitle("签退成功！")
                                        .setText("辛苦了，回家好好休息吧！")
                                        .setDuration(5000)
                                        .show();
                                signOutProgressButton.normal(6);
                            } else if (value == 100 && !DiscoverFragment.signInIsFinished){
                                signOutState.setText("失败");
                                signOutProgressButton.normal(3);
                                Alerter.create(getActivity())
                                        .setBackgroundColor(R.color.alert_default_error_background)
                                        .setTitle("签退失败！")
                                        .setText("是否接入餐厅服务器所在局域网络？")
                                        .setDuration(3000)
                                        .show();
                            }
                        }
                    });
                    signOutProgressButton.normal(4);
                    NetworkUtil networkUtil = NetworkUtil.getNetworkUtil();
                    networkUtil.signOut(Integer.toString(id), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            signOutIsFinished = s != null && "success".equals(s);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            signOutIsFinished = false;
                            volleyError.printStackTrace();
                            Alerter.create(getActivity())
                                    .setBackgroundColor(R.color.colorLightBlue)
                                    .setTitle("网络出错了哟！")
                                    .setText("是否接入餐厅服务器所在局域网络？")
                                    .setDuration(3000)
                                    .show();
                        }
                    }, TAG);
                    Log.d(TAG, "执行了网络请求");
                    valueAnimator.start();
                } else {
                    valueAnimator.cancel();
                    signOutProgressButton.normal(1);
                    signOutState.setText("点击签退");
                }
            }
        });
        return v;
    }
}
