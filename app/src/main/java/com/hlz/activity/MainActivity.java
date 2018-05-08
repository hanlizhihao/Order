package com.hlz.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hlz.adapter.UnderwayAdapter;
import com.hlz.animationlibrary.CubeOutTransformer;
import com.hlz.entity.Indent;
import com.hlz.fragment.HistoryFragment;
import com.hlz.fragment.DiscoverFragment;
import com.hlz.fragment.MeFragment;
import com.hlz.fragment.UnderwayFragment;
import com.hlz.order.MyApplication;
import com.hlz.order.R;
import com.hlz.order.RabbitMQService;
import com.hlz.util.AppManager;
import com.hlz.util.DialogHelp;
import com.hlz.util.DoubleClickExitHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener
{
    private final String TAG="MainActivity";
    private boolean _isVisible=true;
    private ProgressDialog _waitDialog;
    private int mSelectedItem;
    //ViewPager控件
    private ViewPager main_viewPager ;
    //RadioGroup控件
    private RadioGroup main_tab_RadioGroup ;
    //RadioButton控件
    private RadioButton radio_chats , radio_contacts , radio_discover , radio_me ;
    //类型为Fragment的动态数组
    private ArrayList<Fragment> fragmentList ;
    private DoubleClickExitHelper mDoubleClickExit;
    private AppManager appManager;
    private Toolbar toolbar;
    Fragment chatsFragment;
    Fragment contactsFragment;
    Fragment discoverFragment;
    Fragment meFragment;
    //设备运行时间计时
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
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //界面初始函数，用来获取定义的各控件对应的ID
        InitView();
        //ViewPager初始化函数
        InitViewPager();
        mDoubleClickExit = new DoubleClickExitHelper(this);//双击退出
        appManager=AppManager.getAppManager();
        appManager.addActivity(this);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=new Intent(this, RabbitMQService.class);
        startService(intent);
        RabbitMQService.setHandler(rabbitServiceHandler);
    }
    public void InitView()
    {
        main_tab_RadioGroup = (RadioGroup) findViewById(R.id.main_tab_RadioGroup) ;

        radio_chats = (RadioButton) findViewById(R.id.radio_chats) ;
        radio_contacts = (RadioButton) findViewById(R.id.radio_contacts) ;
        radio_discover = (RadioButton) findViewById(R.id.radio_discover) ;
        radio_me = (RadioButton) findViewById(R.id.radio_me) ;
        main_tab_RadioGroup.setOnCheckedChangeListener(this);
    }

    public void InitViewPager() {
        main_viewPager = (ViewPager) findViewById(R.id.main_ViewPager);
        fragmentList = new ArrayList<>() ;

        chatsFragment = new UnderwayFragment() ;
        contactsFragment = new HistoryFragment();
        discoverFragment = new DiscoverFragment();
        meFragment = new MeFragment();

        //将各Fragment加入数组中
        fragmentList.add(chatsFragment);
        fragmentList.add(contactsFragment);
        fragmentList.add(discoverFragment);
        fragmentList.add(meFragment);

        //设置ViewPager的设配器
        main_viewPager.setAdapter(new MyAdapter(getSupportFragmentManager(),fragmentList));
        //当前为第一个页面
        main_viewPager.setCurrentItem(0);
        //ViewPager的页面改变监听器
        main_viewPager.setOnPageChangeListener(new MyListner());
        main_viewPager.setPageTransformer(true,new CubeOutTransformer());
    }
    public class MyAdapter extends FragmentStatePagerAdapter
    {
        ArrayList<Fragment> list ;
         MyAdapter(FragmentManager fm , ArrayList<Fragment> list)
        {
            super(fm);
            this.list = list ;
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }
        @Override
        public int getCount() {
            return list.size();
        }
    }

    public class MyListner implements ViewPager.OnPageChangeListener
    {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            //获取当前页面用于改变对应RadioButton的状态
            int current = main_viewPager.getCurrentItem() ;
            switch(current)
            {
                case 0:
                    main_tab_RadioGroup.check(R.id.radio_chats);
                    break;
                case 1:
                    main_tab_RadioGroup.check(R.id.radio_contacts);
                    break;
                case 2:
                    main_tab_RadioGroup.check(R.id.radio_discover);
                    break;
                case 3:
                    main_tab_RadioGroup.check(R.id.radio_me);
                    break;
            }
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int CheckedId)
    {
        //获取当前被选中的RadioButton的ID，用于改变ViewPager的当前页
        int current=0;
        switch(CheckedId)
        {
            case R.id.radio_chats:
                current = 0 ;
                break ;
            case R.id.radio_contacts:
                current = 1 ;
                break;
            case R.id.radio_discover:
                current = 2 ;
                break;
            case R.id.radio_me:
                current = 3 ;
                break ;
        }
        if(main_viewPager.getCurrentItem() != current)
        {
            main_viewPager.setCurrentItem(current);
        }
    }
    /**
     * 双击退出
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return mDoubleClickExit.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add_new_order:
                //UIHelper.showSimpleBack(this, SimpleBackPage.SEARCH);
                Intent intent = new Intent(MainActivity.this,MakeOrderActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 若发生改变则返回改变后的订菜字符串和上菜字符串
     * @param requestCode 请求的编码
     * @param resultCode 结果的编码
     * @param intent 参数，带有结果
     */
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent){
        if (requestCode==0&&resultCode==0){
            if (intent!=null&&intent.getStringExtra("reserveChanged")!=null&&!"".equals(intent.getStringExtra("reserveChanged"))){
                String reserve=intent.getStringExtra("reserveChanged");
                String fulfill=intent.getStringExtra("fulfillChanged");
                String id=intent.getStringExtra("id");
                String[] reserves=reserve.substring(0,reserve.length()-1).split("e");
                String[] fulfillNumbers=fulfill.substring(0,fulfill.length()-1).split("e");
                int fulfillNumber=0;
                int reserveNumber=0;
                for (String s:reserves){
                    String[] singleReserve=s.split("a");
                    reserveNumber=reserveNumber+Integer.valueOf(singleReserve[1]);
                }
                for (String s:fulfillNumbers){
                    String[] singleFulfill=s.split("a");
                    fulfillNumber=fulfillNumber+Integer.valueOf(singleFulfill[1]);
                }
                //获取到Adapter的数据源
                UnderwayFragment fragment=(UnderwayFragment)chatsFragment;
                List<Indent> indentList=fragment.adapter.getIndents();
                int idInt=Integer.valueOf(id);
                for (int i=0;i<indentList.size();i++){
                    if (idInt==indentList.get(i).getId()){
                        indentList.get(i).setFulfill(fulfill);
                        indentList.get(i).setReserve(reserve);
                        indentList.get(i).setReserveNumber(reserveNumber);
                        indentList.get(i).setFulfillNumber(fulfillNumber);
                        break;
                    }
                }
                UnderwayAdapter adapter=new UnderwayAdapter(this,indentList);
                fragment.adapter=adapter;
                fragment.getPlanList().setAdapter(adapter);
            }else if (intent!=null&&!intent.getStringExtra("reserveChanged").equals("")){
                //当结算时，将会返回int的id,找出以后删除指定的indent
                UnderwayFragment fragment=(UnderwayFragment)chatsFragment;
                List<Indent> indentList=fragment.adapter.getIndents();
                String id=intent.getStringExtra("reserveChanged");
                for (int i=0;i<indentList.size();i++){
                    if (id.equals(indentList.get(i).getId().toString())){
                        indentList.remove(i);
                        break;
                    }
                }
                UnderwayAdapter adapter=new UnderwayAdapter(this,indentList);
                fragment.adapter=adapter;
                fragment.getPlanList().setAdapter(adapter);
            }
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
    /**
     * 用于处理后台Service返回的消息处理者
     */
    public Handler rabbitServiceHandler=new Handler(){
        @Override
        public void handleMessage(Message message){
            UnderwayFragment fragment=(UnderwayFragment) chatsFragment;
            fragment.onRefresh();
        }
    };
}
