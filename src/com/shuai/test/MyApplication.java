package com.shuai.test;

import android.app.Application;
import android.content.Intent;

import com.shuai.test.tools.WindowChangeDetectingService;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
//		Intent intent=new Intent(this, TopActivityMonitorService.class);
//		startService(intent);
		
		Intent intent0 = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
		intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent0);
		
		
		Intent intent=new Intent(this, WindowChangeDetectingService.class);
		startService(intent);
	}

}