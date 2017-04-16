package com.hlz.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.hlz.adapter.UnderwayItemAdapter;
import com.hlz.database.DatabaseUtil;
import com.hlz.entity.Indent;
import com.hlz.net.NetworkUtil;
import com.hlz.order.MyApplication;
import com.hlz.order.R;
import com.hlz.util.AppManager;
import com.hlz.util.DialogHelp;
import com.hlz.util.StringUtil;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 正在进行订单的详情页，主要功能有：
 * 1.对单个订单进行结算或者修改。
 * 要求是：在结算前提示是否验证手机以及对总价的更改，修改后如果要退出Activity则提示是否保存更改
 * 2.具有toolBar，对不同的订单，显示不同toolBar
 */
public class UnderwayDetailsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private boolean _isVisible = true;
    private ProgressDialog _waitDialog;
    public final String TAG = "UnderwayDetailsActivity";
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.underway_details_list)
    ListView list;
    @InjectView(R.id.finished_indent)
    Button finishedIndent;
    @InjectView(R.id.update_indent)
    Button updateIndent;
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout refreshLayout;
    public static class IndentMenu {
        private String name;
        private String reserveNumber;
        private String fulfillNumber;
        private String price;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getReserveNumber() {
            return reserveNumber;
        }

        public void setReserveNumber(String reserveNumber) {
            this.reserveNumber = reserveNumber;
        }

        public String getFulfillNumber() {
            return fulfillNumber;
        }

        public void setFulfillNumber(String fulfillNumber) {
            this.fulfillNumber = fulfillNumber;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
    private List<IndentMenu> indentMenus=new ArrayList<>();
    private AppManager manager;
    private Indent indent;
    private Map<String, Double> menus;//数据库中的菜单
    private int lastItem;
    private NetworkUtil networkUtil;
    public boolean listIsChanged = false;
    private UnderwayItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_underway_details);
        showWaitDialog("正在努力加载数据...");
        ButterKnife.inject(this);
        //取出传递过来的数据，tableID,reserve,fulfill,reminderNumber,id
        Intent intent = getIntent();
        indent = new Indent();
        indent.setReserve(intent.getStringExtra("reserve"));
        indent.setFulfill(intent.getStringExtra("fulfill"));
        indent.setTableId(intent.getStringExtra("tableID"));
        indent.setId(intent.getIntExtra("id",0));
        indent.setReminderNumber(intent.getIntExtra("reminderNumber",0));
        indent.setFirstTime(intent.getLongExtra("firstTime",0));
        indent.setPrice(intent.getDoubleExtra("price",0));
        //堆栈式管理
        manager = AppManager.getAppManager();
        manager.addActivity(this);
        //获取数据库中存储的菜单数据
        DatabaseUtil databaseUtil = new DatabaseUtil();
        databaseUtil.DataBaseUtilInit(MyApplication.getContext());
        menus = databaseUtil.queryDatabase();
        setIndentMenus(indent);//设置数据源
        //初始化toolBar
        initToolBar();
        //初始化下拉刷新
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }

            public void onScrollStateChanged(AbsListView view,
                                             int scrollState) {
            }
        });
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        networkUtil = NetworkUtil.getNetworkUtil();
        //设置Adapter
        adapter = new UnderwayItemAdapter(this, indentMenus);
        list.setAdapter(adapter);
        hideWaitDialog();
    }
    @Override
    public void onRefresh() {
        networkUtil.getSingleIndent(indent.getId().toString(), getSingleIndentById, errorListener, TAG);
    }

    private Response.Listener<String> getSingleIndentById = new Response.Listener<String>() {
        @Override
        public void onResponse(String o) {
            Gson gson = new Gson();
            indent = gson.fromJson(o, Indent.class);
            setIndentMenus(indent);
            adapter.setIndentMenus(indentMenus);
            handler.sendEmptyMessage(1);
        }
    };
    public Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Alerter.create(UnderwayDetailsActivity.this)
                    .setBackgroundColor(R.color.colorLightBlue)
                    .setTitle("网络出错了哟！")
                    .setText("您可能与服务器失去连接！")
                    .setDuration(2000)
                    .show();
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //停止刷新
            setSwipeRefreshLoadedState();
        }
    };
    /**
     * 设置顶部加载完毕的状态
     */
    protected void setSwipeRefreshLoadedState() {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
            refreshLayout.setEnabled(true);
        }
    }
    @OnClick({R.id.finished_indent, R.id.update_indent})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.finished_indent:
                //提示，验证手机号
                break;
            case R.id.update_indent:
                //点击之后，重置判断改变的变量
                break;
        }
    }
    String[] reserveAndFulfill;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        super.onKeyDown(keyCode,event);
        if (listIsChanged){
               AlertDialog.Builder builder = new AlertDialog.Builder(UnderwayDetailsActivity.this);
                builder.setTitle("注意：");
                builder.setMessage("检测到数据已经被更改，是否保存更改？");
                builder.setIcon(R.mipmap.logo);
                DialogInterface.OnClickListener dialog = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent=getIntent();
                        if (arg1 == DialogInterface.BUTTON_POSITIVE) {
                            showWaitDialog("正在保存更改");
                            NetworkUtil networkUtil=NetworkUtil.getNetworkUtil();
                            /**
                             * 更新Indent需要向后台发送所有关于它的信息
                             */
                            indentMenus=adapter.getIndentMenus();
                            reserveAndFulfill=StringUtil.fromListToString(indentMenus);
                            indent.setReserve(reserveAndFulfill[0]);
                            indent.setFulfill(reserveAndFulfill[1]);
                            networkUtil.updateIndent(indent,updateIndentListener,errorListener,TAG);
                            //网络请求，修改数据，结束Activity，并传递一些数据已经更改，
                        } else if (arg1 == DialogInterface.BUTTON_NEGATIVE) {
                            intent.putExtra("reserveChanged","");
                            UnderwayDetailsActivity.this.setResult(0,intent);
                            manager.finishActivity();
                            //将结束Activity
                        }
                    }
                };
                builder.setPositiveButton("取消", dialog);
                builder.setNegativeButton("确定", dialog);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }else{
            Intent intent=getIntent();
            intent.putExtra("reserveChanged","");
            UnderwayDetailsActivity.this.setResult(0,intent);
            manager.finishActivity();
        }
        return true;
    }

    /**
     * 用于改变指定id的Indent
     */
    public Response.Listener<String> updateIndentListener=new Response.Listener<String> () {
        @Override
        public void onResponse(String o) {
            hideWaitDialog();
            if ("success".equals(o)){
                //成功返回则将新的数据返回给MainActivity
                Intent intent=getIntent();
                intent.putExtra("reserveChanged",reserveAndFulfill[0]);
                intent.putExtra("fulfillChanged",reserveAndFulfill[1]);
                intent.putExtra("id",indent.getId().toString());
                Toast.makeText(MyApplication.getContext(),"保存更改数据成功",Toast.LENGTH_SHORT).show();
                UnderwayDetailsActivity.this.setResult(0,intent);
                manager.finishActivity();
            }else {
                Intent intent=getIntent();
                intent.putExtra("reserveChanged","");
                Toast.makeText(MyApplication.getContext(),"保存更改数据失败",Toast.LENGTH_SHORT).show();
                UnderwayDetailsActivity.this.setResult(0,intent);
                manager.finishActivity();
            }
        }
    };
    private void initToolBar() {
        toolbar.setTitle("桌号：" + indent.getTableId());
        toolbar.setSubtitle("催单次数：" + indent.getReminderNumber());
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);//决定是否可点击
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }
    /**
     * 设置数据源
     * @param indent 由Intent传递过来的参数
     */
    public void setIndentMenus(Indent indent) {
        indentMenus.clear();
        String reserve = indent.getReserve();
        String fulfill = indent.getFulfill();
        /*对字符串进行处理，基于默认的格式：
          地三鲜a1e，这样的格式*/
        reserve = reserve.substring(0, reserve.length() - 1);
        if (fulfill != null) {
            fulfill = fulfill.substring(0, fulfill.length() - 1);
            String[] reserves = reserve.split("e");
            String[] fulfills = fulfill.split("e");
            Map<String, String> fulfillMap = new HashMap<>();
            for (String f : fulfills) {
                String[] fArray = f.split("a");
                fulfillMap.put(fArray[0], fArray[1]);
            }
            for (int i = 0; i < reserves.length; i++) {
                String[] singleMenuReserve = reserves[i].split("a");
                UnderwayDetailsActivity.IndentMenu indentMenu = new UnderwayDetailsActivity.IndentMenu();
                indentMenu.setName(singleMenuReserve[0]);
                indentMenu.setReserveNumber(singleMenuReserve[1]);
                String fulfillNumber = validateHasFulfill(fulfillMap, singleMenuReserve[0]);
                indentMenu.setFulfillNumber(fulfillNumber);
                indentMenu.setPrice(getSingleGreensPrice(menus, singleMenuReserve));
                indentMenus.add(indentMenu);
            }
        } else {
            String[] reserves = reserve.split("e");
            for (int i = 0; i < reserves.length; i++) {
                String[] singleMenuReserve = reserves[i].split("a");
                UnderwayDetailsActivity.IndentMenu indentMenu = new UnderwayDetailsActivity.IndentMenu();
                indentMenu.setName(singleMenuReserve[0]);
                indentMenu.setReserveNumber(singleMenuReserve[1]);
                indentMenu.setFulfillNumber("0");
                indentMenu.setPrice(getSingleGreensPrice(menus,singleMenuReserve));
                indentMenus.add(indentMenu);
            }
        }
    }

    //验证菜名是否在上菜的列表中
    private String validateHasFulfill(Map<String, String> fulfill, String reserveName) {
        Set<String> set = fulfill.keySet();
        if (set.contains(reserveName)) {
            return fulfill.get(reserveName);
        } else {
            return "0";
        }
    }

    private String getSingleGreensPrice(Map<String, Double> menuMap,String[] singleMenuInformation) {
        Double price = menuMap.get(singleMenuInformation[0]);
        if (price == null) {
            Log.d(TAG, "订单中的菜，菜单中竟然没有！");
            return "0";
        } else {
            int number =Integer.valueOf(singleMenuInformation[1]);
            double result=price*number;
            Log.d(TAG,Double.toString(result));
            return Double.toString(result);
        }
    }

    //进度对话框
    public ProgressDialog showWaitDialog(String message) {
        if (_isVisible) {
            if (_waitDialog == null) {
                _waitDialog = DialogHelp.getWaitDialog(this, message);
            }
            if (_waitDialog != null) {
                _waitDialog.setMessage(message);
                _waitDialog.show();
            }
            return _waitDialog;
        }
        return null;
    }

    public void hideWaitDialog() {
        if (_isVisible && _waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    @Override
    public void onResume(){
        MyApplication.getMonitoringTime().start();
        super.onResume();
    }
    @Override
    public void onPause(){
        MyApplication.getMonitoringTime().end();
        super.onPause();
    }
    //点击左上角则退出
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //  finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
