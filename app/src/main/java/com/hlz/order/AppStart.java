package com.hlz.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DELL on 2016/9/6.
 */
public class AppStart extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appstart);
        final Intent localIntent = new Intent(this,NewActivity.class);
        Timer timer = new Timer();
        TimerTask tast = new TimerTask() {
            @Override
            public void run() {
                startActivity(localIntent);
                AppStart.this.finish();
                }
            };
        timer.schedule(tast, 2000);
    }
}
