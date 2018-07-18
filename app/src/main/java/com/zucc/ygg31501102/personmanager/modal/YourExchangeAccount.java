package com.zucc.ygg31501102.personmanager.modal;

import cn.bmob.v3.BmobObject;

public class YourExchangeAccount extends BmobObject{
    private String userid;
    private String moneyTypeName;
    private float number;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMoneyTypeName() {
        return moneyTypeName;
    }

    public void setMoneyTypeName(String moneyTypeName) {
        this.moneyTypeName = moneyTypeName;
    }

    public float getNumber() {
        return number;
    }

    public void setNumber(float number) {
        this.number = number;
    }
}
