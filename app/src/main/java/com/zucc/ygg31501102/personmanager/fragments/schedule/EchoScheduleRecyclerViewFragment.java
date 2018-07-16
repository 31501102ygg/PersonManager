package com.zucc.ygg31501102.personmanager.fragments.schedule;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zucc.ygg31501102.personmanager.MainActivity;
import com.zucc.ygg31501102.personmanager.ScheduleInfoActivity;
import com.zucc.ygg31501102.personmanager.modal.Schedule;

import com.zucc.ygg31501102.personmanager.R;
import com.zucc.ygg31501102.personmanager.database.PersonManagerDatabaseHelper;
import com.zucc.ygg31501102.personmanager.fragments.incomeexpend.RecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EchoScheduleRecyclerViewFragment extends Fragment{
    private RecyclerView mRecyclerView;
    private ScheduleAdapter mAdapter;
    private ArrayList<Schedule> list = new ArrayList<Schedule>();
    private LocalBroadcastManager mLBM;
    private Intent mIntent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_recyclerview, null);
//        initExpends();
        mLBM = LocalBroadcastManager.getInstance(getActivity());
        initSchedule();
        //通过findViewById拿到RecyclerView实例
        mRecyclerView = (RecyclerView)view.findViewById(R.id.schedule_listview);
        //设置RecyclerView管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //初始化适配器
        mAdapter = new ScheduleAdapter(list);
        //设置添加或删除item时的动画，这里使用默认动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置适配器
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(final int position) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("备注");
                dialog.setMessage("123456");
                dialog.setCancelable(false);
                dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent1=new Intent();
                        intent1.setClass(getActivity(), ScheduleInfoActivity.class);//从一个activity跳转到另一个activityIntent intent = new Intent(this, OtherActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("schedule", list.get(position));
                        intent1.putExtras(bundle);
                        startActivity(intent1);
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
                        int id[] = mAdapter.removeData(position);
                        ScheduleRemoveFromDatebase(""+id[0]);if (id[1] > 0) {
                            if (mIntent == null)
                                mIntent = new Intent("com.zucc.ygg31501102.personmanager.removeschedule");
                            mIntent.putExtra("scheduleid", id);
                            mLBM.sendBroadcast(mIntent);
                        }
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

    public void initSchedule(){
        this.list = ScheduleFragment.RecyclerSchedule;
    }

    public void ScheduleRemoveFromDatebase(String id){
        SQLiteDatabase db = MainActivity.databaseHelper.getReadableDatabase();
        db.delete("schedules","scheduleid = ?",new String[]{id});
    }


}
