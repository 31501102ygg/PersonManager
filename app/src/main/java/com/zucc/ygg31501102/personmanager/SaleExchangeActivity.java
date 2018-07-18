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

import com.zucc.ygg31501102.personmanager.fragments.invest.ExchangeListFragment;
import com.zucc.ygg31501102.personmanager.modal.Exchange;
import com.zucc.ygg31501102.personmanager.modal.Expend;
import com.zucc.ygg31501102.personmanager.modal.User;
import com.zucc.ygg31501102.personmanager.modal.YourExchangeAccount;

import java.text.DecimalFormat;
import java.util.Date;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class SaleExchangeActivity extends AppCompatActivity {

    private float currentInvestFloat=0;
    private ImageView Back;
    private TextView MoneyType;
    private TextView CurrentInvest;
    private TextView GetRMB;
    private TextView YourHava;
    private EditText SaleNumber;
    private Button confirm;
    private DecimalFormat decimalFormat =new DecimalFormat("0.00");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_exchange);

        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        final YourExchangeAccount yourExchangeAccount = (YourExchangeAccount) bundle.getSerializable("YourExchangeAccount");

        Back = (ImageView)findViewById(R.id.sale_exchange_back);
        MoneyType = (TextView)findViewById(R.id.sale_exchange_money_type);
        YourHava = (TextView)findViewById(R.id.sale_exchange_have_number);
        CurrentInvest = (TextView)findViewById(R.id.sale_exchange_current_invest);
        GetRMB = (TextView)findViewById(R.id.sale_exchange_get_rmb);
        SaleNumber = (EditText)findViewById(R.id.sale_exchange_number);
        confirm = (Button)findViewById(R.id.sale_exchange_confirm);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        MoneyType.setText(yourExchangeAccount.getMoneyTypeName());
        YourHava.setText(decimalFormat.format(yourExchangeAccount.getNumber()));
        currentInvestFloat = findInvest(yourExchangeAccount.getMoneyTypeName());
        CurrentInvest.setText(decimalFormat.format(currentInvestFloat));
        GetRMB.setText(decimalFormat.format(currentInvestFloat*yourExchangeAccount.getNumber()));
        SaleNumber.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        SaleNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.i("after文本监听",SaleNumber.getText().toString());
                if (SaleNumber.length()!=0) {
                    GetRMB.setText(decimalFormat.format(currentInvestFloat*Float.valueOf(SaleNumber.getText().toString())/100));
                }else {
                    GetRMB.setText("0.00");
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sale();
            }
        });

    }

    public void sale(){
        if (SaleNumber.length()==0){
            Toast.makeText(getApplicationContext(),"数量不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if (Float.valueOf(SaleNumber.getText().toString())==0){
            Toast.makeText(getApplicationContext(),"数量不能为0",Toast.LENGTH_SHORT).show();
            return;
        }
        if (Float.valueOf(SaleNumber.getText().toString()) > Float.valueOf(YourHava.getText().toString())){
            Toast.makeText(getApplicationContext(),"你没有这么多外币",Toast.LENGTH_SHORT).show();
            return;
        }
        SQLiteDatabase database = MainActivity.databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("typename",MoneyType.getText().toString());
        values.put("tradetype",1);
        values.put("number",SaleNumber.getText().toString());
        values.put("startdate",new Date().getTime());
        values.put("userid", User.currentUser.getUserid());
        database.insert("invests",null,values);

        Expend expend = new Expend();
        expend.setExpendname("卖出"+MoneyType.getText().toString());
        expend.setExpendremark("卖出外汇");
        expend.setExpendtype("投资收入");
        expend.setNumber(Float.valueOf(SaleNumber.getText().toString())*currentInvestFloat/100);
        expend.setExpendcreatedate(new Date().getTime());
        expend.setUserid(User.currentUser.getUserid());
        expend.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.i("bmob_expend", "成功");
                    float Money = Float.valueOf(Float.valueOf(SaleNumber.getText().toString())*currentInvestFloat/100);
                    SQLiteDatabase db = MainActivity.databaseHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("number", Money);
                    values.put("expendtype", "投资收入");
                    values.put("expendname", "卖出" + MoneyType.getText().toString());
                    values.put("expendremark", "卖出外汇");
                    values.put("userid", User.currentUser.getUserid());
                    values.put("expendcreatedate", new Date().getTime());
                    db.insert("expends", null, values);
                    float balance = User.currentUser.getBalance() + Money;
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

    public float findInvest(String type){
        if (ExchangeListFragment.lists == null ){
            Toast.makeText(getApplicationContext(),"网络异常",Toast.LENGTH_SHORT).show();
            return 0;
        }
        for(Exchange exchange: ExchangeListFragment.lists){
            if (exchange.getName().equals(type)){
                return (float) exchange.getBankConversionPri();
            }
        }
        return 0;
    }
}
