package com.shuai.test;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;

import com.shuai.test.matrix.MyDynamicConfig;
import com.shuai.test.matrix.MyPluginListener;
import com.shuai.test.tools.TopActivityMonitorService;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.matrix.Matrix;
import com.tencent.matrix.iocanary.IOCanaryPlugin;
import com.tencent.matrix.iocanary.config.IOConfig;

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    private static Context mContext;
    private static RefWatcher mRefWatcher;


    public MyApplication() {
        super();
        mContext = this;
        //Debug.waitForDebugger();
    }

    public static Context getContext(){
        return mContext;
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
        initMatrix();


//		Intent intent=new Intent(this, TopActivityMonitorService.class);
//		startService(intent);

//		Intent intent0 = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
//		intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(intent0);


    }

    private void initMatrix(){
        Matrix.Builder builder = new Matrix.Builder(this); // build matrix
        builder.patchListener(new MyPluginListener(this)); // add general pluginListener
        MyDynamicConfig dynamicConfig = new MyDynamicConfig(); // dynamic config

        // init plugin
        IOCanaryPlugin ioCanaryPlugin = new IOCanaryPlugin(new IOConfig.Builder()
                .dynamicConfig(dynamicConfig)
                .build());
        //add to matrix
        builder.plugin(ioCanaryPlugin);

        //init matrix
        Matrix.init(builder.build());

        // start plugin
        ioCanaryPlugin.start();
    }

}
