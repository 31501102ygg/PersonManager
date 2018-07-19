package com.zucc.ygg31501102.personmanager.fragments.personpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zucc.ygg31501102.personmanager.LoginActivity_1;
import com.zucc.ygg31501102.personmanager.ModifyInfoActivity;
import com.zucc.ygg31501102.personmanager.R;
import com.zucc.ygg31501102.personmanager.modal.User;

public class PersonHomepage extends Fragment{
    private TextView name;
    private TextView phone;
    private TextView email;
    private Button out;
    private Button share;
    private RelativeLayout rvPhone;
    private RelativeLayout rvEmail;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_homepage,null);
        email = (TextView)view.findViewById(R.id.person_info_email);
        phone = (TextView)view.findViewById(R.id.person_info_phone);
        name = (TextView)view.findViewById(R.id.person_info_name);
        out = (Button)view.findViewById(R.id.person_homepage_out);
        share = (Button)view.findViewById(R.id.person_homepage_share);
        rvPhone = (RelativeLayout)view.findViewById(R.id.person_info_rv_phone);
        rvEmail = (RelativeLayout)view.findViewById(R.id.person_info_rv_email);

        email.setText(User.currentUser.getUseremail());
        phone.setText(User.currentUser.getPhonenumber());
        name.setText(User.currentUser.getUsername());

        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), LoginActivity_1.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        rvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ModifyInfoActivity.class);
                startActivity(intent);
            }
        });

        rvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ModifyInfoActivity.class);
                startActivity(intent);
            }
        });

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ModifyInfoActivity.class);
                startActivity(intent);
            }
        });

        share.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "标题");
                intent.putExtra(Intent.EXTRA_TEXT, "描述信息" + "这里你可以追加一个url连接");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, "分享到"));
            }
        });
        return view;
    }


}
