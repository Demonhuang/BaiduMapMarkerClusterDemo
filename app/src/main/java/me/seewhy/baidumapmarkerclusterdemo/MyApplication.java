package me.seewhy.baidumapmarkerclusterdemo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * User: seewhy
 * Date: 15/9/1
 * Time: 下午11:05
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
    }
}
