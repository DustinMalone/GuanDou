package com.gd.guandou.guandou.Application;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.xutils.x;

/**
 * Created by Administrator on 2017/9/28.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化XUtils
        x.Ext.init(this);
        //设置debug模式
        x.Ext.setDebug(true);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }
}
