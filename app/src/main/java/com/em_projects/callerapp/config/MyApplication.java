package com.em_projects.callerapp.config;

import android.app.Application;

/**
 * Created by eyalmuchtar on 06/08/2017.
 */

public class MyApplication extends Application {
    public static final String TAG = "MyApplication";

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
}
