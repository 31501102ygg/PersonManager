package com.zucc.ygg31501102.personmanager.fragments.invest;

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
import android.widget.Toast;

import com.zucc.ygg31501102.personmanager.R;
import com.zucc.ygg31501102.personmanager.modal.User;
import com.zucc.ygg31501102.personmanager.modal.YourExchangeAccount;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class InvestFragment extends Fragment {
    private TextView balance;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String[] mTitle = {"你的外汇","汇率"};
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private DecimalFormat decimalFormat =new DecimalFormat("0.00");
    public static Map<String,Integer> FlagsMap = new HashMap<String ,Integer>();
    private String[] countrys={"美元","欧元","港币","日元","英镑","澳大利亚元","加拿大元","泰国铢","新加坡元","挪威克朗","新西兰元","丹麦克朗","瑞典克朗","菲律宾比索","卢布","瑞士法郎","新台币","澳门元","林吉特","南非兰特","韩国元"};
    private int[] flags={R.drawable.flag_usa_96,R.drawable.flag_europe_96,R.drawable.flag_hongkong_96,R.drawable.flag_japan_96,R.drawable.flag_britain_96,
            R.drawable.flag_australia_96,R.drawable.flag_canada_96,R.drawable.flag_thailand_96,R.drawable.flag_singapore_96,R.drawable.flag_norway_96,
            R.drawable.flag_new_zealand_96,R.drawable.flag_denmark_96,R.drawable.flag_sweden_96,R.drawable.flag_philippines_96,R.drawable.flag_russian_federation_96,
            R.drawable.flag_switzerland_96,R.drawable.flag_taiwan_96,R.drawable.flag_macao,R.drawable.flag_malaysia_96,R.drawable.flag_south_africa_96,R.drawable.flag_south_korea_96};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invest, null);
        initMap();
        initDate();
        balance = (TextView)view.findViewById(R.id.invest_balance);
        balance.setText(decimalFormat.format(User.currentUser.getBalance()));
        mTabLayout = (TabLayout) view.findViewById(R.id.invest_tablayout);
        mViewPager = (ViewPager) view.findViewById(R.id.invest_viewpager);
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
            fragments.add(new YourExchangeListFragment());
//            fragments.add(new YourExchangeListFragment());
            fragments.add(new ExchangeListFragment());
        }
        else{
            fragments.clear();
            fragments.add(new YourExchangeListFragment());
//            fragments.add(new YourExchangeListFragment());
            fragments.add(new ExchangeListFragment());
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

    public void initMap(){
        for (int i=0;i<countrys.length;i++){
            FlagsMap.put(countrys[i],flags[i]);
        }
    }
}
