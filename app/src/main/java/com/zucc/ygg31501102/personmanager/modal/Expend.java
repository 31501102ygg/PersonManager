package com.zucc.ygg31501102.personmanager.modal;

import java.util.Date;

import cn.bmob.v3.BmobObject;

public class Expend extends BmobObject {
    private int expendid ;
    private String userid;
    private float number;//金额
    private String expendtype;//种类
    private String expendname;//名称
    private String expendremark;//备注
    private long expendcreatedate;//创建日期

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getExpendid() {
        return expendid;
    }

    public void setExpendid(int expendid) {
        this.expendid = expendid;
    }

    public float getNumber() {
        return number;
    }

    public void setNumber(float number) {
        this.number = number;
    }

    public String getExpendtype() {
        return expendtype;
    }

    public void setExpendtype(String expendtype) {
        this.expendtype = expendtype;
    }

    public String getExpendname() {
        return expendname;
    }

    public void setExpendname(String expendname) {
        this.expendname = expendname;
    }

    public long getExpendcreatedate() {
        return expendcreatedate;
    }

    public void setExpendcreatedate(long expendcreatedate) {
        this.expendcreatedate = expendcreatedate;
    }
    public String getExpendremark() {
        return expendremark;
    }

    public void setExpendremark(String expendremark) {
        this.expendremark = expendremark;
    }

}
