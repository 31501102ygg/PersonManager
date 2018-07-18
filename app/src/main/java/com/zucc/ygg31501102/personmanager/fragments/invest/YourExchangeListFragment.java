package com.zucc.ygg31501102.personmanager.fragments.invest;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.zucc.ygg31501102.personmanager.BuyExchangeActivity;
import com.zucc.ygg31501102.personmanager.MainActivity;
import com.zucc.ygg31501102.personmanager.R;
import com.zucc.ygg31501102.personmanager.SaleExchangeActivity;
import com.zucc.ygg31501102.personmanager.modal.User;
import com.zucc.ygg31501102.personmanager.modal.YourExchangeAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YourExchangeListFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private YourExchangeListAdapter mAdapter;
    private ArrayList<YourExchangeAccount> lists=new ArrayList<YourExchangeAccount>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invest_recyclerview,null);
        //通过findViewById拿到RecyclerView实例
        initList();
        if (lists.size()==0){
            TextView hint = (TextView)view.findViewById(R.id.invest_hint);
            hint.setVisibility(View.VISIBLE);
        }else {
            mRecyclerView = (RecyclerView) view.findViewById(R.id.invest_listview);
            //设置RecyclerView管理器
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            //初始化适配器
            mAdapter = new YourExchangeListAdapter(lists);
            //设置添加或删除item时的动画，这里使用默认动画
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            //设置适配器
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new YourExchangeListAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    Intent intent1=new Intent();
                    intent1.setClass(getActivity(), SaleExchangeActivity.class);//从一个activity跳转到另一个activityIntent intent = new Intent(this, OtherActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("YourExchangeAccount", lists.get(position));
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                }

//                @Override
//                public void onLongClick(final int position) {
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//                    dialog.setTitle("提醒");
//                    dialog.setMessage("删除这个条目");
//                    dialog.setCancelable(false);
//                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                        }
//                    });
//                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                        }
//                    });
//                    dialog.show();
//                    Toast.makeText(getActivity(), "您长按点击了" + position + "行", Toast.LENGTH_SHORT).show();
//                }
            });
            return view;
        }
        return view;
    }

    private void initList(){
        lists.clear();
        Map<String,Float> map = new HashMap<String,Float>();
        SQLiteDatabase datebase = MainActivity.databaseHelper.getReadableDatabase();
        String [] columns = {"DISTINCT  investid","userid","startdate","typename","tradetype","sum(number)"};
        Cursor cursor = datebase.query("invests",columns,"userid=? and tradetype=0",new String[]{User.currentUser.getUserid()},"typename",null,null);
        if (cursor.moveToFirst()){
            do {
                map.put(cursor.getString(cursor.getColumnIndex("typename")),cursor.getFloat(cursor.getColumnIndex("sum(number)")));
            }while (cursor.moveToNext());
        }
        cursor = datebase.query("invests",columns,"userid=? and tradetype=1",new String[]{User.currentUser.getUserid()},"typename",null,null);
        if (cursor.moveToFirst()){
            do {
                float money = map.get(cursor.getString(cursor.getColumnIndex("typename")))-cursor.getFloat(cursor.getColumnIndex("sum(number)"));
                map.put(cursor.getString(cursor.getColumnIndex("typename")),money);
            }while (cursor.moveToNext());
        }
        for (Map.Entry<String,Float> entry: map.entrySet()) {
            YourExchangeAccount y = new YourExchangeAccount();
            y.setUserid(User.currentUser.getUserid());
            y.setMoneyTypeName(entry.getKey());
            y.setNumber(entry.getValue());
            lists.add(y);
        }
    }
}
