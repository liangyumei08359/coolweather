package com.example.coolweather;

import android.app.Application;

import org.litepal.LitePal;

/**
 * @类名: ${type_name}
 * @功能描述:
 * @作者: ${user}
 * @时间: ${date}
 * @最后修改者:
 * @最后修改内容:
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        LitePal.initialize(this);
    }
}