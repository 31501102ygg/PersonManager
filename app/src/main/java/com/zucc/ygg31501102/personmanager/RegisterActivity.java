package com.zucc.ygg31501102.personmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zucc.ygg31501102.personmanager.modal.User;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity {
    private Button registerButton;
    private EditText repeatPassword;
    private EditText Password;
    private EditText userId;
    private FloatingActionButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        //第一：默认初始化
        Bmob.initialize(this, "e6e554c7e514fa01eee26c056b20ec19");

        userId = (EditText)findViewById(R.id.et_username);
        Password = (EditText)findViewById(R.id.et_password);
        repeatPassword = (EditText)findViewById(R.id.et_repeatpassword);
        registerButton = (Button)findViewById(R.id.bt_go);
        back = (FloatingActionButton)findViewById(R.id.register_fab);
        repeatPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                registerUser();
                return false;
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        back.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
              finish();
            }
        });
    }

    private void registerUser(){
        final String password = Password.getText().toString();
        final String repeatpassword = repeatPassword.getText().toString();
        final String userid = userId.getText().toString();
        final String username = "新用户";
        if (userid.isEmpty()||password.isEmpty()||repeatpassword.isEmpty()){
            Toast.makeText(getApplicationContext(),"edittext不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(repeatpassword)){
            Toast.makeText(getApplicationContext(),"两次输入的密码不一致",Toast.LENGTH_SHORT).show();
            return;
        }

        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("userid", userid);
        query.setLimit(1);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if(e==null){
                    if (object.size()==0){
                        User newUser = new User();
                        newUser.setUseremail("");
                        newUser.setPhonenumber("");
                        newUser.setUsername(username);
                        newUser.setUserid(userid);
                        newUser.setPassword(password);
                        newUser.setBalance(0);
                        newUser.save(new SaveListener<String>() {
                            @Override
                            public void done(String objectId,BmobException e) {
                                if(e==null){
                                    Log.i("bmob","成功");
                                    finish();
                                }else{
                                    Log.i("bmob","失败");
                                }
                            }
                        });
                        return;
                    }
                    Toast.makeText(getApplicationContext(),"用户名已经存在",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{

                }
            }
        });
    }
}
