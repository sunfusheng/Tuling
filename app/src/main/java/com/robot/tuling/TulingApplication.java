package com.robot.tuling;

import com.orm.SugarApp;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by sunfusheng on 2014/12/10.
 */
public class TulingApplication extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
