package com.zucc.ygg31501102.personmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class PersonManagerDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_EXPEND = "create table expends ("
            +"expendid integer primary key autoincrement,"//收入支出记录id
            +"userid text,"//用户id
            +"number real,"//金额
            +"expendtype text,"//种类
            +"expendname text,"//名称
            +"expendremark text,"//备注
            +"expendcreatedate integer,"//创建日期
            +"expendremovedate integer)";//更新日期

    public static final String CREATE_SCHEDULE = "create table schedules ("
            +"scheduleid integer primary key autoincrement,"//日程id
            +"userid text,"//用户id
            +"startdate integer,"//开始时间
            +"enddate integer,"//结束时间
            +"scheduletitle text,"//名称
            +"scheduleremark text,"//备注
            +"days integer,"//下一次出发日程间隔时间
            +"picaddress text,"//图片保存本地地址
            +"scheduleaddress text,"//地址
            +"state int)";//状态

    public static final String CREATE_INVEST= "create table invests ("
            +"investid integer primary key autoincrement,"//买卖外汇id
            +"userid text,"//用户id
            +"startdate integer,"//交易时间
            +"typename text,"//外汇种类
            +"tradetype integer,"//交易种类（0：买，卖：1）
            +"number real)";//交易数目
    private Context mContext;

    public PersonManagerDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EXPEND);
        db.execSQL(CREATE_SCHEDULE);
        db.execSQL(CREATE_INVEST);
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }
}
