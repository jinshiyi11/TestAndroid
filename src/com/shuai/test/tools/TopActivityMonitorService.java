package com.shuai.test.tools;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class TopActivityMonitorService extends Service {
	private Runnable mCallback;
	private Handler mHandler=new Handler();
	private static final int FIND_DURATION=1000;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		mCallback=new Runnable() {
			
			@Override
			public void run() {
				findTopActivity();
			}
		};
		

		findTopActivity();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(mCallback);
	}

	private void findTopActivity(){
		mHandler.postDelayed(mCallback, FIND_DURATION);
		
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
	    Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
	    ComponentName componentInfo = taskInfo.get(0).topActivity;
	    componentInfo.getPackageName();
	}

}
