package com.zucc.ygg31501102.personmanager.fragments.incomeexpend;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import com.zucc.ygg31501102.personmanager.R;

import java.util.ArrayList;

public class IncomeExpenditureFragment  extends Fragment{
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    public static TextView mBalance;
    private String[] mTitle = {"收入","支出"};
    ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_expenditure, null);
        initDate();
        mTabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        mBalance = (TextView) view.findViewById(R.id.balance) ;
        mViewPager = (ViewPager) view.findViewById(R.id.income_viewpager);
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
        setBalance();
        return view;
    }

    private void initDate(){
        if(fragments.size()==0) {
            fragments.add(new PayRecyclerViewFragment());
            fragments.add(new IncomeRecyclerViewFragment());
        }
        else{
            fragments.clear();
            fragments.add(new PayRecyclerViewFragment());
            fragments.add(new IncomeRecyclerViewFragment());
        }
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
    public void setBalance(){
        mBalance.setText(""+getDateBaseBalance());
    }
}
