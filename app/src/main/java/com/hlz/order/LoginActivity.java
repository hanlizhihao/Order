package com.hlz.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
}
