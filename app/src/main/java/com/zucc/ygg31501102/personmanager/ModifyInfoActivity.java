package com.zucc.ygg31501102.personmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.zucc.ygg31501102.personmanager.modal.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ModifyInfoActivity extends AppCompatActivity {
    private EditText name;
    private EditText phone;
    private EditText email;
    private ImageView back;
    private Button modify;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_person_info);

        name = (EditText) findViewById(R.id.person_info_modify_name);
        email = (EditText) findViewById(R.id.person_info_modify_email);
        phone = (EditText) findViewById(R.id.person_info_modify_phone);
        back = (ImageView) findViewById(R.id.person_info_modify_back);
        modify = (Button) findViewById(R.id.person_info_modify_confirm);

        name.setText(User.currentUser.getUsername());
        email.setText(User.currentUser.getUseremail());
        phone.setText(User.currentUser.getPhonenumber());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modify();
            }
        });
    }

    private void modify() {
        String modifyname = name.getText().toString();
        String modifyphone = phone.getText().toString();
        String modifyemail = email.getText().toString();

        User user = new User();
        user.setUsername( modifyname);
        user.setPhonenumber(modifyphone);
        user.setUseremail(modifyemail);
        user.setBalance(User.currentUser.getBalance());
        user.update(User.currentUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.i("bmob", "更新成功");
                    loadUser();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "更新失败", Toast.LENGTH_SHORT).show();
                    Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    private void loadUser(){
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.getObject(User.currentUser.getObjectId(), new QueryListener<User>() {
            @Override
            public void done(User object,BmobException e) {
                if(e==null){
                    Log.i("bmob", "更新成功");
                    User.currentUser = object;
                }else{
                    Toast.makeText(getApplicationContext(), "更新失败", Toast.LENGTH_SHORT).show();
                    Log.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }
}
