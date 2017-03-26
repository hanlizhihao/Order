package com.hlz.order;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hlz.activity.MainActivity;
import com.hlz.database.DataBaseUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by DELL on 2016/9/6.
 */
public class LoginActivity extends Activity {
    @InjectView(R.id.name_edit)
    EditText nameEdit;
    @InjectView(R.id.pass_edit)
    EditText passEdit;
    @InjectView(R.id.remember)
    AppCompatCheckBox remember;
    @InjectView(R.id.login_btn)
    AppCompatButton loginBtn;
    @InjectView(R.id.setting_url)
    TextView settingUrl;
    @InjectView(R.id.forget)
    TextView forget;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.inject(this);
        if (isFristRun()){//这是一个历史遗留问题，主要是判断是不是第一次运行的函数不可用导致
            Log.d("LoginActivity","是第一次运行");
            DataBaseUtil dataBaseUtil=new DataBaseUtil();
            Boolean sign=dataBaseUtil.createDatabase(MyApplication.getContext());
            dataBaseUtil.DataBaseUtilInit(MyApplication.getContext());
            String[] menu=getResources().getStringArray(R.array.datetest);
            Boolean sign1=dataBaseUtil.initExample(menu);
            if (sign&&sign1){
                Log.d("TAG","数据库创建成功，数据初始化成功");
            }else{
                Log.d("TAG","失败");
            }
        }else{
            Log.d("LoginActivity","不是第一次运行");
        }
    }
    @OnClick({R.id.remember, R.id.login_btn, R.id.setting_url, R.id.forget})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.remember:
                break;
            case R.id.login_btn:
                if (validate())//校验
                {
                    Intent intent=new Intent(this,MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }
                break;
            case R.id.setting_url:
                break;
            case R.id.forget:
                break;
        }
    }
    public boolean validate() {
        boolean valid = true;
        String name = nameEdit.getText().toString();
        String password = passEdit.getText().toString();
        if (name.isEmpty()) {
            nameEdit.setError("不可为空");
            valid = false;
        } else {
            nameEdit.setError(null);
        }
        if (password.isEmpty()) {
            passEdit.setError("不能为空");
            valid = false;
        } else {
            passEdit.setError(null);
        }
        return valid;
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
}
