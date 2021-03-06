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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zucc.ygg31501102.personmanager.R;
import com.zucc.ygg31501102.personmanager.MainActivity;
import com.zucc.ygg31501102.personmanager.modal.Expend;
import com.zucc.ygg31501102.personmanager.modal.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class PayRecyclerViewFragment extends Fragment {
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
            public void onClick(int position) {
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
                        updateBalance(lists.get(position).getNumber());
                        String id= ""+mAdapter.removeData(position);
                        ExpendRemoveFromDatebase(id);
                        IncomeExpenditureFragment.mBalance.setText(IncomeExpenditureFragment.decimalFormat.format(User.currentUser.getBalance()));
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialog.show();
                Toast.makeText(getActivity(),"您长按点击了"+position+"行",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public void initExpends(){
        SQLiteDatabase db = MainActivity.databaseHelper.getReadableDatabase();
        lists.clear();
        String [] columns = {"expendid","number","expendtype","expendname","expendremark","expendcreatedate"};
        Cursor cursor = db.query("expends",columns,"userid=? and number < 0",new String[]{User.currentUser.getUserid()},null,null,null);
        if (cursor.moveToFirst()) {
            do {
                Expend expend = new Expend();
                expend.setExpendid(cursor.getInt(cursor.getColumnIndex("expendid")));
                expend.setExpendname(cursor.getString(cursor.getColumnIndex("expendname")));
                expend.setNumber(cursor.getFloat(cursor.getColumnIndex("number")));
                expend.setExpendremark(cursor.getString(cursor.getColumnIndex("expendremark")));
                expend.setExpendtype(cursor.getString(cursor.getColumnIndex("expendtype")));
                long date = cursor.getLong(cursor.getColumnIndex("expendcreatedate"));
                expend.setExpendcreatedate(date);
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
        SQLiteDatabase db = MainActivity.databaseHelper.getReadableDatabase();
        db.delete("expends","expendid = ?",new String[]{id});
    }

    public void updateBalance(float Money){
        float balance = User.currentUser.getBalance();
        balance -= Money;
        User.currentUser.setBalance(balance);
        User user = new User();
        user.setValue("balance",balance);
        user.update(User.currentUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","更新成功");
                }else{
                    Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
}
