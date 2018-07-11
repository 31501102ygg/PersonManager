package com.zucc.ygg31501102.personmanager.modal;

import cn.bmob.v3.BmobObject;

public class User extends BmobObject {
    public static User currentUser = null;
    private String username;
    private String userid;
    private String password;
    private String useremail;

    public String getName() {
        return username;
    }

    public void setName(String name) {
        this.username = name;
    }

    public String getId() {
        return userid;
    }

    public void setId(String id) {
        this.userid = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return useremail;
    }

    public void setEmail(String email) {
        this.useremail = email;
    }

}
