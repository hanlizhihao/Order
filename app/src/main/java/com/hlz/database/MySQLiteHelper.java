package com.hlz.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * QQ：1430261583
 * Created by Hanlizhi on 2016/10/26.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {
    public MySQLiteHelper(Context context,String name,int version){
        super(context,name,null,version);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
