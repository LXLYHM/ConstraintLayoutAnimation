package com.dawnling.app;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * Created by LXL on 2018/1/17.
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
