package com.shuai.test.launchmode;

import com.shuai.test.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LauchModeSingleTaskActivity extends Activity {
	private Context mContext;
	private Button mBtnTest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext=this;
		setContentView(R.layout.activity_test_lauchmode_singletask);
		
		mBtnTest=(Button) findViewById(R.id.btn_test);
		mBtnTest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(mContext, LauchModeSingleTaskActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
}
