package com.hlz.order;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hlz.fragment.ContactsFragment;
import com.hlz.fragment.DiscoverFragment;
import com.hlz.fragment.MeFragment;
import com.hlz.fragment.chatsFragment;
import com.hlz.util.AppManager;
import com.hlz.util.DoubleClickExitHelper;
import com.hlz.util.MonitoringTime;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener
{
    private int mSelectedItem;
    //ViewPager控件
    private ViewPager main_viewPager ;
    //RadioGroup控件
    private RadioGroup main_tab_RadioGroup ;
    //RadioButton控件
    private RadioButton radio_chats , radio_contacts , radio_discover , radio_me ;
    //类型为Fragment的动态数组
    private ArrayList<Fragment> fragmentList ;
    private MonitoringTime monitoringTime;//监视使用时间
    private DoubleClickExitHelper mDoubleClickExit;
    private AppManager appManager;
    private Toolbar toolbar;
    @Override
    public void onResume(){
        super.onResume();
        monitoringTime.start();
    }
    @Override
    public void onStop(){
        super.onStop();
        monitoringTime.end();
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //界面初始函数，用来获取定义的各控件对应的ID
        InitView();
        //ViewPager初始化函数
        InitViewPager();
        //初始化监视APP使用时间的类
        monitoringTime=new MonitoringTime(MainActivity.this);
        mDoubleClickExit = new DoubleClickExitHelper(this);//双击退出
        appManager=AppManager.getAppManager();
        appManager.addActivity(this);
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
     * 返回按钮的监听，用来询问用户是否退出程序
     * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否退出应用
            return mDoubleClickExit.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

}
