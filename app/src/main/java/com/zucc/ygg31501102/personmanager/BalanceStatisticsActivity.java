package com.zucc.ygg31501102.personmanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.zucc.ygg31501102.personmanager.database.PersonManagerDatabaseHelper;
import com.zucc.ygg31501102.personmanager.drawview.PieChart;
import com.zucc.ygg31501102.personmanager.modal.User;

public class BalanceStatisticsActivity extends AppCompatActivity{
    private PieChart pieChart;
    private Button btnRandom;
    private ImageView back;
    private PersonManagerDatabaseHelper dbHelper;
    private float[] datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart);
        back = (ImageView) findViewById(R.id.piechart_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TotalMoney();
        pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setDatas(datas);
        pieChart.invalidate();
    }

    public void TotalMoney(){
        float pay = 0;
        float income = 0;
        datas = new float[2];
        String [] columns = {"number"};
        SQLiteDatabase db = MainActivity.databaseHelper.getReadableDatabase();
        Cursor cursor = db.query("expends",columns,"userid=?",new String[]{User.currentUser.getUserid()},null,null,null);
        if(cursor.moveToFirst()) {
            do {
                float number = cursor.getFloat(cursor.getColumnIndex("number"));
                if (number<0)
                    pay+=(-number);
                else
                    income+=number;
            } while (cursor.moveToNext());
        }
        datas[0] = pay;
        datas[1] = income;
    }
}
