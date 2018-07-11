package com.zucc.ygg31501102.personmanager.fragments;

import android.annotation.SuppressLint;
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

import com.zucc.ygg31501102.personmanager.R;

import java.util.ArrayList;

public class IncomeExpenditureFragment  extends Fragment{
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String[] mTitle = {"收入","支出"};
    ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_expenditure, null);
        initDate();
        mTabLayout = (TabLayout) view.findViewById(R.id.tablayout);
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
                return true;
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
            fragments.add(new IncomeExpenditureTabFragment());
            fragments.add(new IncomeExpenditureTabFragment());
        }
        else{
            fragments.clear();
            fragments.add(new IncomeExpenditureTabFragment());
            fragments.add(new IncomeExpenditureTabFragment());
        }
    }
}
