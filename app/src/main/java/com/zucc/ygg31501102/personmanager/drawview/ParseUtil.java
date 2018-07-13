package com.zucc.ygg31501102.personmanager.drawview;

import android.content.Context;

public class ParseUtil {
    private Context mContext;
    public ParseUtil(Context context) {
        mContext = context;
    }
    public int sp2px(int value) {
        float v = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (value * v + 0.5f);
    }
    public int dp2px(int value) {
        float v = mContext.getResources().getDisplayMetrics().density;
        return (int) (value * v + 0.5f);
    }
}
