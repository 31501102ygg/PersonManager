package com.zucc.ygg31501102.personmanager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zucc.ygg31501102.personmanager.database.PersonManagerDatabaseHelper;
import com.zucc.ygg31501102.personmanager.modal.Expend;
import com.zucc.ygg31501102.personmanager.modal.User;
import com.zucc.ygg31501102.personmanager.plug_in.CustomDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;


public class AddExpendActivity extends AppCompatActivity{
    private ImageView backImage;
    private EditText textMoney;
    private EditText textName;
    private EditText textRemark;
    private TextView textTime;
    private TextView selectItem;
    private Button addButton;
    private Button payOutButton;
    private Button incomeButton;
    private GridView gridView;
    private CustomDatePicker customDatePicker1;
    private RelativeLayout select_time;
    private final String[] payOutTypes = {"餐饮食品", "衣服饰品", "居家生活", "行车交通","休闲娱乐", "文化教育", "健康医疗", "投资支出", "其他支出"};
    private final String[] incomeTypes = {"工作收入", "投资收入", "其他收入"};
    private final int[] payOutTypeIcons = {
            R.drawable.rice, R.drawable.icons8_clothes_24, R.drawable.bed_empty,
            R.drawable.bus, R.drawable.microphone_variant, R.drawable.lead_pencil,
            R.drawable.hospital, R.drawable.chart_line, R.drawable.lock_question
    };
    private final int[] incomeTypeIcons = {
            R.drawable.worker, R.drawable.chart_areaspline, R.drawable.lock_question,
    };
    private String selectType="餐饮食品";
    private boolean ifIncome = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addexpense);

        backImage = (ImageView)findViewById(R.id.expend_add_back);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        select_time = (RelativeLayout) findViewById(R.id.select_time);
        select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 日期格式为yyyy-MM-dd HH:mm
                customDatePicker1.show(textTime.getText().toString());
            }
        });
        textTime = (TextView)findViewById(R.id.expend_add_date);
        initDatePicker();
        textTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDatePicker1.show(textTime.getText().toString());
            }
        });
        selectItem = (TextView)findViewById(R.id.select_type);
        gridView = (GridView)findViewById(R.id.gridView);
        payOutButton = (Button)findViewById(R.id.button_payout);
        payOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payOutButton.setTextColor(getApplicationContext().getResources().getColor(R.color.light_red));
                incomeButton.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                initPayoutDate();
            }
        });
        incomeButton = (Button)findViewById(R.id.button_income);
        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incomeButton.setTextColor(getApplicationContext().getResources().getColor(R.color.light_red));
                payOutButton.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                initIncomDate();
            }
        });
        payOutButton.callOnClick();
        textMoney = (EditText)findViewById(R.id.expend_add_money);
        textName = (EditText)findViewById(R.id.expend_add_name);
        textRemark = (EditText)findViewById(R.id.expend_add_remark);
        addButton = (Button)findViewById(R.id.expanded_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addExpend();
            }
        });
    }

    /*设置时间日期*/
    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
//        currentDate.setText(now.split(" ")[0]);
        textTime.setText(now);

//        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
//            @Override
//            public void handle(String time) { // 回调接口，获得选中的时间
//                currentDate.setText(time.split(" ")[0]);
//            }
//        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
//        customDatePicker1.showSpecificTime(false); // 不显示时和分
//        customDatePicker1.setIsLoop(false); // 不允许循环滚动

        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                textTime.setText(time);
            }
        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(true); // 显示时和分
        customDatePicker1.setIsLoop(true); // 允许循环滚动
    }

    /*加载支出种类*/
    class PayOutAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // 条目的总数 ，为九宫格格数
            return payOutTypes.length;
        }

        @Override
        public Object getItem(int position) {
            return payOutTypes[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.expend_type_item, null);
            TextView tv_title = (TextView) view.findViewById(R.id.iv_title);
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);

            tv_title.setText(payOutTypes[position]);
            iv_icon.setBackgroundResource(payOutTypeIcons[position]);

            return view;
        }
    }
    private void initPayoutDate() {
        gridView.setAdapter(new PayOutAdapter());
        // 注册九宫格的单个点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            // position点中条目的索引
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectType = "餐饮食品";selectItem.setText("餐饮食品");ifIncome = false;break;
                    case 1:
                        selectType = "衣服饰品";selectItem.setText("衣服饰品");ifIncome = false;break;
                    case 2:
                        selectType = "居家生活";selectItem.setText("居家生活");ifIncome = false;break;
                    case 3:
                        selectType = "行车交通";selectItem.setText("行车交通");ifIncome = false;break;
                    case 4:
                        selectType = "休闲娱乐";selectItem.setText("休闲娱乐");ifIncome = false;break;
                    case 5:
                        selectType = "文化教育";selectItem.setText("文化教育");ifIncome = false;break;
                    case 6:
                        selectType = "健康医疗";selectItem.setText("健康医疗");ifIncome = false;break;
                    case 7:
                        selectType = "投资支出";selectItem.setText("投资支出");ifIncome = false;break;
                    case 8:
                        selectType = "其他支出";selectItem.setText("其他支出");ifIncome = false;break;
                }
            }

        });
    }

    /*加载收入种类*/
    class IncomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // 条目的总数 ，为九宫格格数
            return incomeTypes.length;
        }

        @Override
        public Object getItem(int position) {
            return incomeTypes[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.expend_type_item, null);
            TextView tv_title = (TextView) view.findViewById(R.id.iv_title);
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);

            tv_title.setText(incomeTypes[position]);
            iv_icon.setBackgroundResource(incomeTypeIcons[position]);

            return view;
        }
    }
    private void initIncomDate() {
        gridView.setAdapter(new IncomeAdapter());
        // 注册九宫格的单个点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            // position点中条目的索引
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectType = "工作收入";selectItem.setText("工作收入");ifIncome = true;break;
                    case 1:
                        selectType = "投资收入";selectItem.setText("投资收入");ifIncome = true;break;
                    case 2:
                        selectType = "其他收入";selectItem.setText("其他收入");ifIncome = true;break;

                }
            }

        });

    }

    private boolean addExpend(){
        String StringMoney = textMoney.getText().toString();
        String name = textName.getText().toString();
        String remark = textRemark.getText().toString();
        String type = selectType;
        String createDate = textTime.getText().toString();
        Date date ;
        float Money;
        long dateToLong;
        if(StringMoney==null||name==null||remark==null||type==null||createDate==null)
        {
            Toast.makeText(this,"空格不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Money = Float.parseFloat(StringMoney);
            if (!ifIncome){
                Money = -Money;
            }
        }catch(Exception e){
            Toast.makeText(this,"输入金额不是一个数字",Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            date = sdf.parse(createDate);
            dateToLong = dateToLong(date);
            Toast.makeText(this,"时间:"+date.toString(),Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Toast.makeText(this,"时间格式不正确",Toast.LENGTH_SHORT).show();
            return false;
        }

        Expend expend = new Expend();
        expend.setExpendname(name);
        expend.setExpendremark(remark);
        expend.setExpendtype(type);
        expend.setNumber(Money);
        expend.setExpendcreatedate(dateToLong);
        expend.setUserid(User.currentUser.getUserid());
        expend.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Log.i("bmob_expend","成功");
                    String StringMoney = textMoney.getText().toString();
                    String name = textName.getText().toString();
                    String remark = textRemark.getText().toString();
                    String type = selectType;
                    String createDate = textTime.getText().toString();
                    float Money = Float.valueOf(StringMoney);
                    long dateToLong;
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date date = sdf.parse(createDate);
                        dateToLong = dateToLong(date);
                    }catch(Exception e1){
                        return ;
                    }
                    if (!ifIncome){
                        Money = -Money;
                    }
                    SQLiteDatabase db = MainActivity.databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("number",Money);
                    values.put("expendtype",type);
                    values.put("expendname",name);
                    values.put("expendremark",remark);
                    values.put("userid",User.currentUser.getUserid());
                    values.put("expendcreatedate",dateToLong);
                    db.insert("expends",null,values);
                    float balance = User.currentUser.getBalance();
                    balance += Money;
                    User.currentUser.setBalance(balance);
                    User user = new User();
                    user.setValue("balance",balance);
                    user.update(User.currentUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null){
                                Log.i("bmob","更新成功");
                                finish();
                            }else{
                                Log.i("bmob","更新失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    });
                }else{
                    Log.i("bmob_expend","失败");
                }
            }
        });

        return true;
    }

    /*date和int互换*/
    public long dateToLong(Date date){
        long intDate = date.getTime();
        return intDate;
    }

    public Date longToDate(long intDate){
        Date date = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String nowTimeStr = sdf.format(intDate);
            date = sdf.parse(nowTimeStr);
            Toast.makeText(this,"时间（转化）:"+date.toString(),Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this,"时间转化出错",Toast.LENGTH_SHORT).show();
            return null;
        }
        return date;
    }


}
