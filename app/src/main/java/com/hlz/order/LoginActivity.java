package com.hlz.order;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hlz.activity.MainActivity;
import com.hlz.database.DatabaseUtil;
import com.hlz.entity.Menu;
import com.hlz.entity.Menus;
import com.hlz.entity.User;
import com.hlz.net.NetworkUtil;
import com.hlz.util.DialogHelp;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**登录页主要完成的功能有
 * 1.登录与自动登录
 * 2.初始化数据库
 * 3.验证菜单版本号
 * 4.向数据库写入菜单
 * Created by DELL on 2016/9/6.
 */
public class LoginActivity extends Activity {
    private boolean _isVisible=true;
    private ProgressDialog _waitDialog;
    private String TAG="LoginActivity";
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
    boolean login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置这个activity界面
        setContentView(R.layout.login);
        //使用butteKnife实现了不需要写很多的findViewById
        ButterKnife.inject(this);
        showWaitDialog("正在初始化...");
        SharedPreferences sp=MyApplication.getContext().getSharedPreferences("appConfig",MODE_PRIVATE);
        login=sp.getBoolean("isLogin",false);
        initDatabase();
    }
    @OnClick({R.id.remember, R.id.login_btn, R.id.setting_url, R.id.forget})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.remember:
                break;
            case R.id.login_btn:
                if (validate())//校验
                {
                    showWaitDialog("登录中...");
                    NetworkUtil networkUtil=NetworkUtil.getNetworkUtil();
                    networkUtil.login(nameEdit.getText().toString(),passEdit.getText().toString(),successListener,errorListener,TAG);
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
    //判断是否是第一次运行
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
    //登录验证，成功返回的回调函数
    public Response.Listener successListener=new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            Gson gson=new Gson();
            Log.d(TAG,s);
            User user=gson.fromJson(s,User.class);
            if (!user.getName().equals("")){
                //用于自动登录的配置
                SharedPreferences sp=MyApplication.getContext().getSharedPreferences("appConfig",MODE_PRIVATE);
                SharedPreferences.Editor edit=sp.edit();
                edit.putBoolean("isLogin",true);
                edit.putInt("id",user.getId());
                edit.putString("name",user.getName());
                edit.putString("username",user.getUsername());
                edit.putString("password",user.getPassword());
                edit.apply();
                //跳转界面
                hideWaitDialog();
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }else{
                hideWaitDialog();
                nameEdit.setError("用户名或密码错误");
                passEdit.setError("用户名或密码错误");
            }
        }
    };
    public Response.Listener<String> getMenusListener=new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            try{
                if (s!=null&&!s.equals("")){
                    Gson gson=new Gson();
                    List<Menu> menus=gson.fromJson(s,new TypeToken<List<Menu>>(){}.getType());
                    Map<String,Double> result=new HashMap<>();
                    for (Menu menu:menus){
                        result.put(menu.getGreensName(),menu.getPrice());
                    }
                    DatabaseUtil databaseUtil=new DatabaseUtil();
                    databaseUtil.DataBaseUtilInit(MyApplication.getContext());
                    if (!databaseUtil.insertDatabase(result)){
                        hideWaitDialog();
                        Alerter.create(LoginActivity.this)
                                .setBackgroundColor(R.color.colorLightBlue)
                                .setTitle("菜单数据初始化失败！")
                                .setText("请稍后重启App以重试")
                                .setDuration(3000)
                                .show();
                    }else{
                        Toast.makeText(MyApplication.getContext(),"数据初始化成功",Toast.LENGTH_SHORT);
                        hideWaitDialog();
                        if (login){
                            secondLogin();
                        }
                    }
                }else{
                    Toast.makeText(MyApplication.getContext(),"菜单数据获取失败",Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                Toast.makeText(MyApplication.getContext(),"json解析出错",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    };
    //网络连接失败后，回调的函数
    public Response.ErrorListener errorListener=new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            hideWaitDialog();
            Alerter.create(LoginActivity.this)
                    .setBackgroundColor(R.color.colorLightBlue)
                    .setTitle("网络出错了哟！")
                    .setText("是否接入餐厅服务器所在局域网络？")
                    .setDuration(3000)
                    .show();
        }
    };
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
    //若已经登录则数据库初始化之后，直接跳转到主Activity
    public void initDatabase(){
        NetworkUtil networkUtil=NetworkUtil.getNetworkUtil();
        if (isFristRun()){
            //如果是第一次运行，则先创建数据库
            DatabaseUtil databaseUtil =new DatabaseUtil();
            databaseUtil.createDatabase(MyApplication.getContext());
            databaseUtil.DataBaseUtilInit(MyApplication.getContext());
            networkUtil.getMenuVersion(getMenuVersion,errorListener,TAG);//获取最新版本
        }else{
            networkUtil.getMenuVersion(getMenuVersion,errorListener,TAG);
        }
    }
    public Response.Listener<String> getMenuVersion=new Response.Listener<String>() {
        @Override
        public void onResponse(String s) {
            if (s!=null&&!"".equals(s)){
                SharedPreferences sp=MyApplication.getContext().getSharedPreferences("appConfig",MODE_PRIVATE);
                String version=sp.getString("version",null);
                if (version!=null&&s.equals(version)){
                    Toast.makeText(MyApplication.getContext(),"数据初始化成功",Toast.LENGTH_SHORT).show();
                    hideWaitDialog();
                    if (login){
                        secondLogin();
                    }
                }else{
                    NetworkUtil networkUtil=NetworkUtil.getNetworkUtil();
                    networkUtil.getMenu(getMenusListener,errorListener,TAG);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("version",s);
                    editor.apply();
                }
            }else{
                Toast.makeText(MyApplication.getContext(),"请求验证菜单失败，稍后将重试",Toast.LENGTH_LONG).show();
                hideWaitDialog();
            }
        }
    };
    private void secondLogin(){
        showWaitDialog("登录中...");
        SharedPreferences sp=MyApplication.getContext().getSharedPreferences("appConfig",MODE_PRIVATE);
        String username=sp.getString("username","");
        String password=sp.getString("password","");
        if (!username.equals("")){
            NetworkUtil networkUtil=NetworkUtil.getNetworkUtil();
            networkUtil.login(username,password,successListener,errorListener,TAG);
        }
    }
}
