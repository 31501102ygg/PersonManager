package com.zucc.ygg31501102.personmanager.modal;

import java.io.Serializable;

public class Exchange implements Serializable{
    private String name;
    private double fBuyPri;
    private double mBuyPri;
    private double fSellPri;
    private double mSellPri;
    private double bankConversionPri;
    private String date;
    private String time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getfBuyPri() {
        return fBuyPri;
    }

    public void setfBuyPri(double fBuyPri) {
        this.fBuyPri = fBuyPri;
    }

    public double getmBuyPri() {
        return mBuyPri;
    }

    public void setmBuyPri(double mBuyPri) {
        this.mBuyPri = mBuyPri;
    }

    public double getfSellPri() {
        return fSellPri;
    }

    public void setfSellPri(double fSellPri) {
        this.fSellPri = fSellPri;
    }

    public double getmSellPri() {
        return mSellPri;
    }

    public void setmSellPri(double mSellPri) {
        this.mSellPri = mSellPri;
    }

    public double getBankConversionPri() {
        return bankConversionPri;
    }

    public void setBankConversionPri(double bankConversionPri) {
        this.bankConversionPri = bankConversionPri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}