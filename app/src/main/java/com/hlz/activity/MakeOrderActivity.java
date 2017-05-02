package com.hlz.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hlz.adapter.ShoppingCartAdapter;
import com.hlz.adapter.SortAdapter;
import com.hlz.database.DatabaseUtil;
import com.hlz.entity.IndentModel;
import com.hlz.entity.ShoppingCart;
import com.hlz.net.NetworkUtil;
import com.hlz.net.UrlManager;
import com.hlz.order.MyApplication;
import com.hlz.order.R;
import com.hlz.util.AppManager;
import com.hlz.util.CharacterParser;
import com.hlz.util.ClearEditText;
import com.hlz.util.DialogHelp;
import com.hlz.util.PinyinComparator;
import com.hlz.util.SideBar;
import com.hlz.util.SortModel;
import com.lqr.recyclerview.LQRRecyclerView;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class MakeOrderActivity extends AppCompatActivity {
    public final String TAG="MakeOrderActivity";
    private boolean _isVisible = true;
    private ProgressDialog _waitDialog;
    private RelativeLayout realative;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private Button addIndent;
    private ClearEditText mClearEditText;
    private TextView sumSize;//点菜总数
    private TextView sumPrice;//总钱数
    ImageButton order_cart;
    private LQRRecyclerView shoppingCartListView;
    private Button shoppingCartClear;
    private ShoppingCartAdapter shoppingCartAdapter;
    private ShoppingCart shoppingCart;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private AppManager manager;
    public Response.Listener<String> listener=new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            if (s!=null&&!"".equals(s)){
                if (s.equals("success")){
                    manager.finishActivity();
                }else{
                    Toast.makeText(MakeOrderActivity.this,"创建订单失败，服务器端异常！",Toast.LENGTH_LONG).show();
                }
            }else{
                Alerter.create(MakeOrderActivity.this)
                        .setBackgroundColor(R.color.colorLightBlue)
                        .setTitle("创建订单失败！")
                        .setText("网络请求失败！")
                        .setDuration(2000)
                        .show();
            }
        }
    };
    public Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            Alerter.create(MakeOrderActivity.this)
                    .setBackgroundColor(R.color.colorLightBlue)
                    .setTitle("网络出错了哟！")
                    .setText("您可能与服务器失去连接！")
                    .setDuration(2000)
                    .show();
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);
        manager= AppManager.getAppManager();
        manager.addActivity(this);
        showWaitDialog("初始化...");
        realative=(RelativeLayout)findViewById(R.id.cart);
        addIndent=(Button)findViewById(R.id.take_order_button);
        addIndent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!shoppingCart.isEmpty()){
                    NetworkUtil networkUtil=NetworkUtil.getNetworkUtil();
                    IndentModel model=new IndentModel();
                    //窗口弹出
                    networkUtil.createIndent(model,listener,errorListener,TAG);
                }else{
                    Toast.makeText(MakeOrderActivity.this,"还没有选择菜品",Toast.LENGTH_LONG).show();
                }
            }
        });
        initViews();
        AppManager.getAppManager().addActivity(this);
        hideWaitDialog();
    }
    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if(position != -1){
                    sortListView.setSelection(position);
                }
            }
        });
        sortListView = (ListView)findViewById(R.id.country_lvcountry);
        /**
         * 这个函数接受一个数组作为数据源
         * 菜单应该是Map类型，在这里获取Map类型的key集合，并转化为数组形式
         * 因为是点菜宝，因此不设定显示价格，只在购物车附近显示总价格与菜品数量
         */
        DatabaseUtil databaseUtil=new DatabaseUtil();
        databaseUtil.DataBaseUtilInit(MyApplication.getContext());
        Map<String,Double> menu=databaseUtil.queryDatabase();
        Set<String> menuSet= menu.keySet();
        String[] menusArray = new String[menuSet.size()];
        int i=0;
        for (Object o:menuSet.toArray()){
            menusArray[i]=o.toString();
            i++;
        }
        SourceDateList = filledData(menusArray);
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        /**
         * 在这里获取购物车、总菜数、总钱数的图标
         */
        sumSize=(TextView)findViewById(R.id.sumSize);
        sumPrice=(TextView)findViewById(R.id.sumPrice);
        order_cart = (ImageButton) findViewById(R.id.order_cart);
        shoppingCart=new ShoppingCart(MyApplication.getContext());
        adapter = new SortAdapter(MyApplication.getContext(), SourceDateList, order_cart,realative,sumSize,sumPrice,shoppingCart);
        sortListView.setAdapter(adapter);
        mClearEditText = (ClearEditText)findViewById(R.id.filter_edit);
        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        //加载弹窗视图布局
        View view= LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.shopping_cart,null);
        shoppingCartClear=(Button) view.findViewById(R.id.clear);
        shoppingCartListView=(LQRRecyclerView)view.findViewById(R.id.shopping_cart_list);
        //创建PopupWindow
        final PopupWindow finalPopup=new PopupWindow(this);
        finalPopup.setContentView(view);
        finalPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        finalPopup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        finalPopup.setTouchable(true);
        finalPopup.setOutsideTouchable(false);
        finalPopup.setBackgroundDrawable(new ColorDrawable(0x000000));
        finalPopup.getContentView().setFocusableInTouchMode(true);
        finalPopup.getContentView().setFocusable(true);
        finalPopup.setAnimationStyle(R.style.anim_menu_bottombar);
        finalPopup.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (finalPopup != null && finalPopup.isShowing()) {
                        finalPopup.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });
        order_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalPopup.isShowing()){
                    adapter = new SortAdapter(MyApplication.getContext(), SourceDateList, order_cart,realative,sumSize,sumPrice,shoppingCart);
                    sortListView.setAdapter(adapter);
                    finalPopup.dismiss();
                }else{
                    shoppingCartAdapter=new ShoppingCartAdapter(shoppingCart,MyApplication.getContext(),sumSize,sumPrice);
                    shoppingCartListView.setAdapter(shoppingCartAdapter);
                    shoppingCartClear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            shoppingCart.clearShoppingCart();
                            changeShoppingCartAdapter();
                        }
                    });
                    finalPopup.showAtLocation(findViewById(R.id.cart_view), Gravity.CENTER, 0, 0);
                }
            }
        });
    }

    //用于更新shoppingCart
    private void changeShoppingCartAdapter(){
        shoppingCartAdapter.getData().clear();
        shoppingCartAdapter.notifyDataSetChanged();
    }
    /**
     * 为ListView填充数据
     * @param date 填充数组
     * @return List
     */
    private List<SortModel> filledData(String [] date){
        List<SortModel> mSortList = new ArrayList<>();

        for (String aDate : date) {
            SortModel sortModel = new SortModel();
            sortModel.setName(aDate);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(aDate);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * 添加数据
     */
    private void filterData(String filterStr){
        List<SortModel> filterDateList = new ArrayList<>();
        if(TextUtils.isEmpty(filterStr)){
            filterDateList = SourceDateList;
        }else{
            filterDateList.clear();
            for(SortModel sortModel : SourceDateList){
                String name = sortModel.getName();
                if(name.contains(filterStr) || characterParser.getSelling(name).startsWith(filterStr)){
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
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
}
