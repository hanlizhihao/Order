package com.hlz.fragment;

/**
 * 已完成和已取消的数据
 * Created by Administrator on 2016/9/8.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hlz.activity.MainActivity;
import com.hlz.adapter.HistoryAdapter;
import com.hlz.adapter.UnderwayAdapter;
import com.hlz.entity.Indent;
import com.hlz.net.NetworkUtil;
import com.hlz.order.R;
import com.lqr.recyclerview.LQRRecyclerView;
import com.tapadoo.alerter.Alerter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ContactsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    final String TAG="historyFragment";
    @InjectView(R.id.finished)
    Button finished;
    @InjectView(R.id.canceled)
    Button canceled;
    @InjectView(R.id.list)
    LQRRecyclerView list;
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    MainActivity activity;
    private int lastItem;
    private List<Indent> indents;//已完成的数据集
    private List<Indent> canceledIndents;
    private Handler handlerAdapter;
    private HistoryAdapter historyAdapter;
    private Handler handlerRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.inject(this, v);
        activity=(MainActivity) getActivity();
        activity.showWaitDialog("正在加载...");
        list.setOnScrollListenerExtension(new LQRRecyclerView.OnScrollListenerExtension() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                loadData();
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                lastItem = dx + dy;
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        handlerAdapter=new Handler(){
            @Override
            public void handleMessage(Message message){
                if (message.what==1){
                    historyAdapter = new HistoryAdapter(indents,activity);
                    list.setAdapter(historyAdapter);
                }
                if (message.what==2){
                    historyAdapter = new HistoryAdapter(canceledIndents,activity);
                    list.setAdapter(historyAdapter);
                }
            }
        };
        handlerRefresh = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //停止刷新
                setSwipeRefreshLoadedState();
            }
        };
        initData();
        return v;
    }
    public void initData(){
        NetworkUtil networkUtil=NetworkUtil.getNetworkUtil();
        networkUtil.getFinished(null,listener,errorListener,TAG);
        networkUtil.getCanceled(null,canceledListener,errorListener,TAG);
    }
    public Response.Listener<String> canceledListener=new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            if (s!=null&&!s.equals("")){
                Gson gson=new Gson();
                canceledIndents=gson.fromJson(s,new TypeToken<List<Indent>>(){}.getType());
                activity.hideWaitDialog();
                handlerRefresh.sendEmptyMessage(1);
                handlerAdapter.sendEmptyMessage(2);
            }
        }
    };
    //获取已经完成的订单数据
    public Response.Listener<String> listener=new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            if (s!=null&&!s.equals("")){
                Gson gson=new Gson();
                indents=gson.fromJson(s,new TypeToken<List<Indent>>(){}.getType());
                activity.hideWaitDialog();
                handlerRefresh.sendEmptyMessage(1);
                handlerAdapter.sendEmptyMessage(1);
            }
        }
    };
    public Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            activity.hideWaitDialog();
            Alerter.create(activity)
                    .setBackgroundColor(R.color.colorLightBlue)
                    .setTitle("网络出错了哟！")
                    .setText("您可能与服务器失去连接！")
                    .setDuration(3000)
                    .show();
        }
    };
    protected void loadData() {
//        footerProgressbar.setVisibility(View.VISIBLE);
//        footerTextview.setText("加载中");
//        handlerRefresh.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //如果数据很多，为了不一次性全部显示出来，所以Adapeter要这样设计一个函数，
//                //用于标识显示几页数据
//                footerTextview.setText("加载更多");
//                footerProgressbar.setVisibility(View.INVISIBLE);
//            }
//        }, 2000);
//        延迟两秒后执行
//        这种方式，只适用于一次性从网络获取了大量数据的情况
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.finished, R.id.canceled})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.finished:
                finished.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handlerAdapter.sendEmptyMessage(1);
                        finished.setBackgroundResource(R.color.colorTabBlue);
                        canceled.setBackgroundResource(R.color.colorLightPink);
                    }
                });
                break;
            case R.id.canceled:
                canceled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handlerAdapter.sendEmptyMessage(2);
                        finished.setBackgroundResource(R.color.colorLightPink);
                        canceled.setBackgroundResource(R.color.colorTabBlue);
                    }
                });
                break;
        }
    }

    @Override
    public void onRefresh() {
        NetworkUtil networkUtil=NetworkUtil.getNetworkUtil();
        networkUtil.getFinished(null,listener,errorListener,TAG);
    }
    /**
     * 设置顶部正在加载的状态
     */
    protected void setSwipeRefreshLoadingState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);
        }
    }
    /**
     * 设置顶部加载完毕的状态
     */
    protected void setSwipeRefreshLoadedState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }
    }
}
