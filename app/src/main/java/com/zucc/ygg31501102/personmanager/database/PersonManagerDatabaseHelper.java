package com.zucc.ygg31501102.personmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class PersonManagerDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_EXPEND = "create table expends ("
            +"expendid integer primary key autoincrement,"//收入支出记录id
            +"number real,"//金额
            +"expendtype text,"//种类
            +"expendname text,"//名称
            +"expendremark text,"//备注
            +"expendcreatedate integer,"//创建日期
            +"expendremovedate integer)";//更新日期

    private Context mContext;

    public PersonManagerDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EXPEND);
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
