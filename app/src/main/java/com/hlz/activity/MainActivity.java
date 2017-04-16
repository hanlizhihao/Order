package com.hlz.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import com.hlz.animationlibrary.CubeOutTransformer;
import com.hlz.entity.Indent;
import com.hlz.fragment.ContactsFragment;
import com.hlz.fragment.DiscoverFragment;
import com.hlz.fragment.MeFragment;
import com.hlz.fragment.chatsFragment;
import com.hlz.order.MyApplication;
import com.hlz.order.R;
import com.hlz.util.AppManager;
import com.hlz.util.DialogHelp;
import com.hlz.util.DoubleClickExitHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener
{
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

    public void InitViewPager()
    {
        main_viewPager = (ViewPager) findViewById(R.id.main_ViewPager);

        fragmentList = new ArrayList<>() ;

        Fragment chatsFragment = new chatsFragment() ;
        Fragment contactsFragment = new ContactsFragment();
        Fragment discoverFragment = new DiscoverFragment();
        Fragment meFragment = new MeFragment();

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
    //用于获取UnderwayDetailsActivity返回的结果
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent intent){
        if (requestCode==0&&resultCode==0){
            if (intent!=null&&!"".equals(intent.getStringExtra("reservedChanged"))){
                String reserve=intent.getStringExtra("reserveChanged");
                String fulfill=intent.getStringExtra("fulfillChanged");
                String id=intent.getStringExtra("id");
                Integer reserveNumber=reserve.substring(0,reserve.length()-1).split("e").length;
                Integer fulfillNumber=fulfill.substring(0,fulfill.length()-1).split("e").length;
                //获取到Adapter的数据源
                FragmentManager manager=getSupportFragmentManager();
                chatsFragment fragment=(chatsFragment) manager.getFragments().get(0);
                List<Indent> indentList=fragment.adapter.getIndents();
                int i=0;
                for (Indent indent:indentList){
                    if (id.equals(indent.getId().toString())){
                        indent.setFulfill(fulfill);
                        indent.setReserve(reserve);
                        indent.setReserveNumber(reserveNumber);
                        indent.setFulfillNumber(fulfillNumber);
                        indentList.add(i,indent);
                        i=i+1;
                        break;
                    }
                }
                fragment.adapter.setIndents(indentList);
                fragment.adapter.notifyDataSetChanged();
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
}
