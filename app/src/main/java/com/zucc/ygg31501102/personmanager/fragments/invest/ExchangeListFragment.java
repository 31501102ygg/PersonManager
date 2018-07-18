package com.zucc.ygg31501102.personmanager.fragments.invest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.zucc.ygg31501102.personmanager.BuyExchangeActivity;
import com.zucc.ygg31501102.personmanager.R;
import com.zucc.ygg31501102.personmanager.fragments.incomeexpend.RecyclerViewAdapter;
import com.zucc.ygg31501102.personmanager.modal.Exchange;
import com.zucc.ygg31501102.personmanager.plug_in.JSONParser;

import java.util.ArrayList;

public class ExchangeListFragment extends Fragment{
    private Handler handler;
    private RecyclerView mRecyclerView;
    private ExchangeListAdapter mAdapter;
    public static ArrayList<Exchange> lists;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invest_recyclerview,null);
        getExchangeList();
        //通过findViewById拿到RecyclerView实例
        mRecyclerView = (RecyclerView)view.findViewById(R.id.invest_listview);
        //设置RecyclerView管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        //初始化适配器
                        mAdapter = new ExchangeListAdapter(lists);
                        //设置添加或删除item时的动画，这里使用默认动画
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        //设置适配器
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(new ExchangeListAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int position) {
                                Intent intent1=new Intent();
                                intent1.setClass(getActivity(), BuyExchangeActivity.class);//从一个activity跳转到另一个activityIntent intent = new Intent(this, OtherActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("exchange", lists.get(position));
                                intent1.putExtras(bundle);
                                startActivity(intent1);
                            }
                        });
                }
            }
        };
        return view;
    }

    private void getExchangeList(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String path = "http://web.juhe.cn:8080/finance/exchange/rmbquot?key=ab433606bc553b885fba8b40858d4537";
                lists = (ArrayList<Exchange>) new JSONParser().getsReturnJSONObject(path);
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }).start();
    }
}
