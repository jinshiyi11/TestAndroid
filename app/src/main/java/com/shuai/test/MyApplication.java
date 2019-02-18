package com.shuai.test;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;

import com.shuai.test.tools.TopActivityMonitorService;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    private static RefWatcher mRefWatcher;

    public MyApplication() {
        super();
        //Debug.waitForDebugger();
    }

    public static RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        mRefWatcher = LeakCanary.install(this);


//		Intent intent=new Intent(this, TopActivityMonitorService.class);
//		startService(intent);

//		Intent intent0 = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
//		intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent0);


    }

}
