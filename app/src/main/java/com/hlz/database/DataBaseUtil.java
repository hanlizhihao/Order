package com.hlz.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * QQ：1430261583
 * Created by Hanlizhi on 2016/10/25.
 * 这是一个用于数据库访问的类，它将包括初始化数据库的函数，根据Map<String,Double>参数向数据库写入的函数
 * 查询数据库并返回Map<String,Double>类型的函数
 * 关于这个类的使用：使用这个类首先要实例化，接着一定要调用DataBaseUtilInit()这个函数，并传入一个Context
 * 类型的参数，然后才可以调用查询之类的参数
 */

public class DatabaseUtil {
    private SQLiteDatabase db;
    private MySQLiteHelper sqLiteHelper;
    private Context context;
    public void DataBaseUtilInit(Context context){
        this.sqLiteHelper=new MySQLiteHelper(context,context.getFilesDir().toString()+"/Dishes.db3",1);
        this.context=context;
    }
    public  boolean createDatabase(Context context){
        try {
            db=SQLiteDatabase.openOrCreateDatabase(context.getFilesDir().toString()+"/Dishes.db3",null);
            String sqlMenu="create table 'menu'('id' integer primary key " +
                    "autoincrement,"+"'name' varchar(255) not null," +"price double not null);";
            db.execSQL(sqlMenu);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    //接受一个字符串数组作为数据源存入数据库
    //假设从网络中返回了数据
    public boolean initExample(String[] menu){
        try {
            double priceTest=20.2;
            int id=1;
            for (int i=0;i<menu.length;i++){
                insertData(db,id,menu[i],priceTest);
                id++;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    //这个是查询数据库返回一个Map菜单的函数
    public Map<String,Double> queryDatabase(){
        try{
            SQLiteDatabase db=sqLiteHelper.getReadableDatabase();
            Cursor cursor=db.rawQuery("select name,price from menu;",null);
            Map<String,Double> menu=new HashMap<>();
            while (cursor.moveToNext()){
                String name=cursor.getString(0);
                Double price=cursor.getDouble(1);
                menu.put(name,price);
            }
            cursor.close();
            return menu;
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
    private void insertData(SQLiteDatabase db
            ,int id, String title, double content)  // 封装insert into
    {
        // 执行插入语句
        db.execSQL("insert into menu values(?, ? , ?);"
                ,new String[]{Integer.toString(id),title,Double.toString(content)});
    }
    /**
     * 将Map类型的菜单参数写入数据库的函数
     */
    public boolean insertDatabase(Map<String,Double> menu){
        try{
            SQLiteDatabase db=sqLiteHelper.getWritableDatabase();
            int line=db.delete("menu",null,null);
            Log.d("删除表中的数据行数为",Integer.toString(line));
            int id=1;
            for (Map.Entry<String,Double> entry:menu.entrySet()) {
                insertData(db,id,entry.getKey(),entry.getValue());
                id=id+1;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
