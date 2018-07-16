package com.zucc.ygg31501102.personmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import static com.zucc.ygg31501102.personmanager.fragments.incomeexpend.RecyclerViewAdapter.getStringDate;

import com.zucc.ygg31501102.personmanager.modal.Schedule;

import java.io.File;

public class ScheduleInfoActivity extends AppCompatActivity {
    private TextView title;
    private TextView startTime;
    private TextView endTime;
    private TextView days;
    private TextView address;
    private ImageView img;
    private TextView remark;
    private ImageView back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_info);
        Intent intent=getIntent();//getIntent将该项目中包含的原始intent检索出来，将检索出来的intent赋值给一个Intent类型的变量intent
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        Schedule schedule = (Schedule) bundle.getSerializable("schedule");

        title=(TextView)findViewById(R.id.s_info_title);
        startTime=(TextView)findViewById(R.id.s_info_start_time);
        endTime=(TextView)findViewById(R.id.s_info_end_time);
        address=(TextView)findViewById(R.id.s_info_address);
        days=(TextView)findViewById(R.id.s_info_loopdays);
        img=(ImageView)findViewById((R.id.s_info_pic)) ;
        remark=(TextView)findViewById(R.id.s_info_remark);
        back=(ImageView)findViewById(R.id.s_info_img);

        title.setText(schedule.getTitle());
        startTime.setText(getStringDate(schedule.getStartDate()));
        endTime.setText(getStringDate(schedule.getEndDate()));
        address.setText(schedule.getAddress());
        days.setText("间隔"+schedule.getDays()+"天");
        remark.setText(schedule.getRemark());
        String path = schedule.getImage();
        if(path!=null) {
            Uri photoUri = Uri.fromFile(new File(path));
            img.setImageURI(photoUri);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
