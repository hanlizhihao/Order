package com.hlz.fragment;

/**
 * Created by Administrator on 2016/9/8.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hlz.adapter.PlanAdapter;
import com.hlz.order.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class chatsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    ExpandableListView list;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private View footView;
    private RelativeLayout loading;
    ProgressBar footerProgressbar;
    TextView footerTextview;
    private int lastItem;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chats_fragment, container, false);
        list=(ExpandableListView)v.findViewById(R.id.expandList);
        mSwipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipe_refresh_layout);
        footView = LayoutInflater.from(getActivity()).inflate(R.layout.item_footer, null);
        loading = (RelativeLayout) footView.findViewById(R.id.xlistview_footer_content);
        footerProgressbar = (ProgressBar) footView.findViewById(R.id.xlistview_footer_progressbar);
        footerTextview = (TextView) footView.findViewById(R.id.xlistview_footer_hint_textview);
        //下拉加载
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
            public void onScrollStateChanged(AbsListView view,
                                             int scrollState) {
                    loadData();
                }
        });
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        list.addFooterView(footView);
        PlanAdapter adapter = new PlanAdapter(getActivity());
        list.setAdapter(adapter);
        return v;
    }
    protected void loadData() {
        footerProgressbar.setVisibility(View.VISIBLE);
        footerTextview.setText("加载中");
        handler.postDelayed(new Runnable() {
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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //停止刷新
            setSwipeRefreshLoadedState();
        }
    };

    @Override
    public void onRefresh() {
        handler.sendEmptyMessageDelayed(1, 1000);
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
}
