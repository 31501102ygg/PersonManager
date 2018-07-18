package com.zucc.ygg31501102.personmanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zucc.ygg31501102.personmanager.modal.User;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity_1 extends AppCompatActivity{

    private AutoCompleteTextView mUserIdView;
    private TextView mRegisterView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //第一：默认初始化
        Bmob.initialize(this, "e6e554c7e514fa01eee26c056b20ec19");

        mUserIdView = (AutoCompleteTextView) findViewById(R.id.userid);
        mRegisterView = (TextView)findViewById(R.id.register_user);
        mRegisterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity_1.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return false;
                }
                return false;
            }
        });
        mLoginFormView=(Button)findViewById(R.id.sign_in_button);
        mLoginFormView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    private void attemptLogin(){
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity_1.this);
        progressDialog.setMessage("正在登陆中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String userid = mUserIdView.getText().toString();
        final String  password = mPasswordView.getText().toString();
        final String[] password1 = new String[1];
        if (userid.isEmpty()||password.isEmpty()){
            Log.i("login","用户名或密码不能为空");
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        BmobQuery<User> eq1 = new BmobQuery<User>();
        eq1.addWhereEqualTo("userid", userid);
        BmobQuery<User> eq2 = new BmobQuery<User>();
        eq2.addWhereEqualTo("useremail", userid);
        BmobQuery<User> eq3 = new BmobQuery<User>();
        eq3.addWhereEqualTo("userphone", userid);
        List<BmobQuery<User>> queries = new ArrayList<BmobQuery<User>>();
        queries.add(eq1);
        queries.add(eq2);
        queries.add(eq3);
        BmobQuery<User> mainQuery = new BmobQuery<User>();
        mainQuery.or(queries);
        mainQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if(e==null){
                    if (object.size()==0){
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity_1.this, "没有这个账号", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    progressDialog.dismiss();
                    User.currentUser = object.get(0);
//                    SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
//                    editor.putString("userid",user.getUserid());
//                    editor.putString("useremail",user.getUseremail());
//                    editor.putString("password",user.getPassword());
//                    editor.putString("phonenumber",user.getPhonenumber());
//                    editor.putFloat("balance",user.getBalance());
//                    editor.putString("username",user.getUsername());
//                    editor.apply();
//                    SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
//                    String name2 = pref.getString("username","");
//                    String id2 = pref.getString("userid","");
//                    String email2 = pref.getString("useremail","");
//                    String password2 = pref.getString("password","");
//                    String phone2 = pref.getString("phonenumber","");
//                    float balance2 = pref.getFloat("balance",0);
//                    if (!password.equals(password2)){
//                        Toast.makeText(LoginActivity_1.this, "密码错误", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    if(User.currentUser == null){
//                        User.currentUser = new User();
//                        User.currentUser.setUsername(name2);
//                        User.currentUser.setUserid(id2);
//                        User.currentUser.setUseremail(email2);
//                        User.currentUser.setPassword(password2);
//                        User.currentUser.setBalance(balance2);
//                        User.currentUser.setPhonenumber(phone2);
//                    }
                    Intent intent = new Intent(LoginActivity_1.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity_1.this, "没有网络", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }
}
