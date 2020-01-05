package com.bzy.sdk;

import android.app.Application;

import com.reyun.tracking.common.ReYunConst;

public abstract class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ReYunConst.DebugMode = BuildConfig.DEBUG;
    }
}
