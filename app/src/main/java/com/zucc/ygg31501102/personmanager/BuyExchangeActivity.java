package com.zucc.ygg31501102.personmanager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zucc.ygg31501102.personmanager.modal.Exchange;
import com.zucc.ygg31501102.personmanager.modal.Expend;
import com.zucc.ygg31501102.personmanager.modal.Schedule;
import com.zucc.ygg31501102.personmanager.modal.User;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class BuyExchangeActivity extends AppCompatActivity {

    private ImageView Back;
    private TextView MoneyType;
    private TextView CurrentInvest;
    private TextView NeedRMB;
    private EditText BuyNumber;
    private Button confirm;
    private DecimalFormat decimalFormat =new DecimalFormat("0.00");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_exchange);

        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        final Exchange exchange = (Exchange) bundle.getSerializable("exchange");

        Back = (ImageView)findViewById(R.id.buy_exchange_back);
        MoneyType = (TextView)findViewById(R.id.buy_exchange_money_type);
        CurrentInvest = (TextView)findViewById(R.id.buy_exchange_current_invest);
        NeedRMB = (TextView)findViewById(R.id.buy_exchange_need_rmb);
        BuyNumber = (EditText)findViewById(R.id.buy_exchange_number);
        confirm = (Button)findViewById(R.id.buy_exchange_confirm);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        MoneyType.setText(exchange.getName());
        CurrentInvest.setText(decimalFormat.format(exchange.getBankConversionPri()));
        BuyNumber.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        BuyNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i("before文本监听",BuyNumber.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.i("on文本监听",BuyNumber.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i("after文本监听",BuyNumber.getText().toString());
                if (BuyNumber.length()!=0) {
                    NeedRMB.setText(decimalFormat.format(exchange.getBankConversionPri()*Float.valueOf(BuyNumber.getText().toString())/100));
                }else {
                    NeedRMB.setText("0.00");
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buy();
            }
        });

    }

    public void buy(){
        if (BuyNumber.length()==0){
            Toast.makeText(getApplicationContext(),"数量不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if (Float.valueOf(BuyNumber.getText().toString())==0){
            Toast.makeText(getApplicationContext(),"数量不能为0",Toast.LENGTH_SHORT).show();
            return;
        }
        Float currentBalance = User.currentUser.getBalance();
        if (currentBalance < Float.valueOf(NeedRMB.getText().toString())){
            Toast.makeText(getApplicationContext(),"账户余额不足",Toast.LENGTH_SHORT).show();
            return;
        }
        SQLiteDatabase database = MainActivity.databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("typename",MoneyType.getText().toString());
        values.put("tradetype",0);
        values.put("number",BuyNumber.getText().toString());
        values.put("startdate",new Date().getTime());
        values.put("userid", User.currentUser.getUserid());
        database.insert("invests",null,values);

        Expend expend = new Expend();
        expend.setExpendname("购买"+MoneyType.getText().toString());
        expend.setExpendremark("购买外汇");
        expend.setExpendtype("投资支出");
        expend.setNumber(-Float.valueOf(NeedRMB.getText().toString()));
        expend.setExpendcreatedate(new Date().getTime());
        expend.setUserid(User.currentUser.getUserid());
        expend.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.i("bmob_expend", "成功");
                    float Money = Float.valueOf(NeedRMB.getText().toString());
                    SQLiteDatabase db = MainActivity.databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("number", -Money);
                    values.put("expendtype", "投资支出");
                    values.put("expendname", "购买" + MoneyType.getText().toString());
                    values.put("expendremark", "购买外汇");
                    values.put("userid", User.currentUser.getUserid());
                    values.put("expendcreatedate", new Date().getTime());
                    db.insert("expends", null, values);
                    float balance = User.currentUser.getBalance() - Money;
                    User.currentUser.setBalance(balance);
                    User user = new User();
                    user.setValue("balance", balance);
                    user.update(User.currentUser.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.i("bmob", "更新成功");
                            } else {
                                Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                    finish();
                } else {
                    Log.i("bmob_expend", "失败");
                }
            }
        });
    }
}
