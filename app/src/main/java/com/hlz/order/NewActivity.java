package com.hlz.order;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.hlz.Animation.AnimatedCircleLoadingView;
import com.hlz.database.DataBaseUtil;

/**
 * 这个Activity是动画loading加载
 * 核心类在Animation包中
 */
public class NewActivity extends Activity {

    private AnimatedCircleLoadingView animatedCircleLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view);
        startLoading();
        startPercentMockThread();
    }

    private void startLoading() {
        animatedCircleLoadingView.startDeterminate();
    }
    private boolean isFristRun() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                "share", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!isFirstRun) {
            return false;
        } else {
            editor.putBoolean("isFirstRun", false);
            editor.apply();
            return true;
        }
    }

    private void startPercentMockThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    int j;
                    if (isFristRun()){//这是一个历史遗留问题，主要是判断是不是第一次运行的函数不可用导致
                        Log.d("NewActivity","是第一次运行");
                        DataBaseUtil dataBaseUtil=new DataBaseUtil();
                        Boolean sign=dataBaseUtil.createDatabase(NewActivity.this);
                        dataBaseUtil.DataBaseUtilInit(NewActivity.this);
                        changePercent(1);
                        changePercent(3);
                        String[] menu=getResources().getStringArray(R.array.datetest);
                        changePercent(5);
                        Boolean sign1=dataBaseUtil.initExample(menu);
                        changePercent(10);
                        j=11;
                        if (sign&&sign1){
                            Log.d("TAG","数据库创建成功，数据初始化成功");
                        }else{
                            Log.d("TAG","失败");
                        }
                    }else{
                        Log.d("NewActivity","不是第一次运行");
                        changePercent(40);
                        j=41;
                    }
                    for (int i = j; i <= 100; i++) {
                        Thread.sleep(30);
                        changePercent(i);
                    }
                    Intent localIntent = new Intent(NewActivity.this,LoginActivity.class);
                    startActivity(localIntent);
                    NewActivity.this.finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    private void changePercent(final int percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedCircleLoadingView.setPercent(percent);
            }
        });
    }

    public void resetLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedCircleLoadingView.resetLoading();
            }
        });
    }
}
