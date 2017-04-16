package com.hlz.fragment;
/**
 * 显示进度的Fragment
 * Created by hlz on 2016/9/8.
 */
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hlz.activity.MainActivity;
import com.hlz.adapter.UnderwayAdapter;
import com.hlz.entity.Indent;
import com.hlz.net.NetworkUtil;
import com.hlz.order.MyApplication;
import com.hlz.order.R;
import com.hlz.order.RabbitMQService;
import com.lqr.recyclerview.LQRRecyclerView;
import com.tapadoo.alerter.Alerter;

import java.util.List;


public class chatsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    String TAG="planFragment";
    public UnderwayAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private View footView;
    private RelativeLayout loading;
    LQRRecyclerView planList;
    ProgressBar footerProgressbar;
    TextView footerTextview;
    private int lastItem;
    private List<Indent> indents;//网络请求的Indent数据集
    private MainActivity mainActivity;
    private Handler handlerService;
    private Handler handlerAdapter;
    private void initView(View v){
        mainActivity=(MainActivity) getActivity();
        mainActivity.showWaitDialog("正在获取订单信息");
        mSwipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipe_refresh_layout);
        footView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.item_footer, null);
        loading = (RelativeLayout) footView.findViewById(R.id.xlistview_footer_content);
        footerProgressbar = (ProgressBar) footView.findViewById(R.id.foot_progressbar);
        footerTextview = (TextView) footView.findViewById(R.id.foot_text);
        planList=(LQRRecyclerView)v.findViewById(R.id.plan_list);
        initUnderwayData();
        //与后台Service联系
        handlerService=new Handler(){
            @Override
            public void handleMessage(Message message){
                if (message.what==1){
                    onRefresh();
                }
            }
        };
        Intent intent=new Intent(mainActivity, RabbitMQService.class);
        mainActivity.startService(intent);
    }
    public void initUnderwayData(){
        NetworkUtil networkUtil=NetworkUtil.getNetworkUtil();
        networkUtil.getUnderwayOrder(getUnderwayDataListener,errorListener,TAG);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plan, container, false);
        initView(v);
        //下拉加载
        planList.setOnScrollListenerExtension(new LQRRecyclerView.OnScrollListenerExtension() {
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
                    adapter = new UnderwayAdapter(mainActivity,indents);
                    planList.setAdapter(adapter);
                }
            }
        };
        return v;
    }
    protected void loadData() {
        footerProgressbar.setVisibility(View.VISIBLE);
        footerTextview.setText("加载中");
        handlerRefresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                //如果数据很多，为了不一次性全部显示出来，所以Adapeter要这样设计一个函数，
                //用于标识显示几页数据
                footerTextview.setText("加载更多");
                footerProgressbar.setVisibility(View.INVISIBLE);
            }
        }, 2000);
        //延迟两秒后执行
        //这种方式，只适用于一次性从网络获取了大量数据的情况
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
    Handler handlerRefresh = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //停止刷新
            setSwipeRefreshLoadedState();
        }
    };
    @Override
    public void onRefresh() {
        NetworkUtil networkUtil=NetworkUtil.getNetworkUtil();
        networkUtil.getUnderwayOrder(getUnderwayDataListener,errorListener,TAG);
        //具体的数据更新操作，网络请求+数据更新
        //这是个回调函数，将在发生下拉手势时被回调执行
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    public Response.Listener getUnderwayDataListener=new Response.Listener<String>() {
        @Override
        public void onResponse(String o) {
            if (o!=null&&!o.equals("")){
                Gson gson=new Gson();
                indents=gson.fromJson(o,new TypeToken<List<Indent>>(){}.getType());
                mainActivity.hideWaitDialog();
                handlerRefresh.sendEmptyMessage(1);
                handlerAdapter.sendEmptyMessage(1);
            }
        }
    };
    public Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            mainActivity.hideWaitDialog();
            Alerter.create(mainActivity)
                    .setBackgroundColor(R.color.colorLightBlue)
                    .setTitle("网络出错了哟！")
                    .setText("您可能与服务器失去连接！")
                    .setDuration(3000)
                    .show();
        }
    };
}
