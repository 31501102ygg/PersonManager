package com.zucc.ygg31501102.personmanager.fragments.schedule;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zucc.ygg31501102.personmanager.MainActivity;
import com.zucc.ygg31501102.personmanager.R;
import com.zucc.ygg31501102.personmanager.modal.Expend;
import com.zucc.ygg31501102.personmanager.modal.Schedule;
import com.zucc.ygg31501102.personmanager.modal.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ScheduleFragment extends Fragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String[] mTitle = {"待办日程","循环日程"};
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    public static ArrayList<Schedule> WaitSchedule = new ArrayList<Schedule>();
    public static ArrayList<Schedule> RecyclerSchedule = new ArrayList<Schedule>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedules, null);
        initList();
        initDate();
        mTabLayout = (TabLayout) view.findViewById(R.id.schedule_tablayout);
        mViewPager = (ViewPager) view.findViewById(R.id.schedule_viewpager);
        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            //此方法用来显示tab上的名字
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle[position % mTitle.length];
            }

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        //禁止ViewPager滑动
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        //将ViewPager关联到TabLayout上
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

    private void initDate(){
        if(fragments.size()==0) {
            fragments.add(new WaitScheduleRecyclerViewFragment());
            fragments.add(new EchoScheduleRecyclerViewFragment());
        }
        else{
            fragments.clear();
            fragments.add(new WaitScheduleRecyclerViewFragment());
            fragments.add(new EchoScheduleRecyclerViewFragment());
        }
    }
    private void initList(){
        WaitSchedule.clear();
        RecyclerSchedule.clear();
        SQLiteDatabase db = MainActivity.databaseHelper.getReadableDatabase();
        String [] columns = {"scheduleid","startdate","enddate","scheduletitle","scheduleremark","days","picaddress","scheduleaddress","state"};
        Cursor cursor = db.query("schedules",columns,"userid=? and state>=0",new String[]{User.currentUser.getUserid()},null,null,"startdate ASC");
        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = new Schedule();
                schedule.setScheduleid(cursor.getInt(cursor.getColumnIndex("scheduleid")));
                long date = cursor.getLong(cursor.getColumnIndex("startdate"));
                schedule.setStartDate(longToDate(date));
                date = cursor.getLong(cursor.getColumnIndex("enddate"));
                schedule.setEndDate(longToDate(date));
                schedule.setTitle(cursor.getString(cursor.getColumnIndex("scheduletitle")));
                schedule.setRemark(cursor.getString(cursor.getColumnIndex("scheduleremark")));
                schedule.setAddress(cursor.getString(cursor.getColumnIndex("scheduleaddress")));
                schedule.setImage(cursor.getString(cursor.getColumnIndex("picaddress")));
                schedule.setState(cursor.getInt(cursor.getColumnIndex("state")));
                int days = cursor.getInt(cursor.getColumnIndex("days"));
                schedule.setDays(days);
                if (days > 0)
                    RecyclerSchedule.add(schedule);
                WaitSchedule.add(schedule);
            } while (cursor.moveToNext());
        }
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


}
