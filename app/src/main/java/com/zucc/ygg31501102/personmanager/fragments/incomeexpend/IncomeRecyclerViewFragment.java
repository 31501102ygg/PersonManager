package com.zucc.ygg31501102.personmanager.fragments.incomeexpend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zucc.ygg31501102.personmanager.R;
import com.zucc.ygg31501102.personmanager.modal.Expend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class IncomeRecyclerViewFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private ArrayList<Expend> lists = new ArrayList<Expend>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, null);
        initExpends();
        //通过findViewById拿到RecyclerView实例
        mRecyclerView = (RecyclerView)view.findViewById(R.id.listview);
        //设置RecyclerView管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //初始化适配器
        mAdapter = new RecyclerViewAdapter(lists);
        //设置添加或删除item时的动画，这里使用默认动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置适配器
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(final int position) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("备注");
                dialog.setMessage(mAdapter.getRemark(position));
                dialog.setCancelable(false);
                dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialog.show();
            }
            @Override
            public void onLongClick(final int position) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("提醒");
                dialog.setMessage("删除这个条目");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String id= ""+mAdapter.removeData(position);
                        ExpendRemoveFromDatebase(id);
                        setBalance();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialog.show();
            }
        });
        return view;
    }

    public void initExpends(){
        SQLiteDatabase db = com.zucc.ygg31501102.personmanager.MainActivity.databaseHelper.getReadableDatabase();
        lists.clear();
        String [] columns = {"expendid","number","expendtype","expendname","expendremark","expendcreatedate"};
        Cursor cursor = db.query("expends",columns,"number < 0",null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                Expend expend = new Expend();
                expend.setExpendid(cursor.getInt(cursor.getColumnIndex("expendid")));
                expend.setExpendname(cursor.getString(cursor.getColumnIndex("expendname")));
                expend.setNumber(cursor.getFloat(cursor.getColumnIndex("number")));
                expend.setExpendremark(cursor.getString(cursor.getColumnIndex("expendremark")));
                expend.setExpendtype(cursor.getString(cursor.getColumnIndex("expendtype")));
                long date = cursor.getLong(cursor.getColumnIndex("expendcreatedate"));
                expend.setExpendcreatedate(longToDate(date));
                lists.add(expend);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public Date longToDate(long intDate){
        Date date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowTimeStr = sdf.format(intDate);
            date = sdf.parse(nowTimeStr);
        }catch (Exception e){
            Toast.makeText(getActivity(),"时间转化出错",Toast.LENGTH_SHORT).show();
            return null;
        }
        return date;
    }

    public void ExpendRemoveFromDatebase(String id){
        SQLiteDatabase db = com.zucc.ygg31501102.personmanager.MainActivity.databaseHelper.getReadableDatabase();
        db.delete("expends","expendid = ?",new String[]{id});
    }

    public void setBalance(){
        IncomeExpenditureFragment.mBalance.setText(""+getDateBaseBalance());
    }

    public float getDateBaseBalance(){
        float balance = 0;
        SQLiteDatabase db = com.zucc.ygg31501102.personmanager.MainActivity.databaseHelper.getReadableDatabase();
        String [] columns = {"number"};
        Cursor cursor = db.query("expends",columns,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                float number = cursor.getFloat(cursor.getColumnIndex("number"));
                balance += number;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return balance;

    }
}
