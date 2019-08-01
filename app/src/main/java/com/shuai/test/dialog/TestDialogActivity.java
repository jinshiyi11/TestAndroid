package com.shuai.test.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import com.shuai.test.R;

public class TestDialogActivity extends Activity {
	private Context mContext;
	private Button mBtnShowDialog;
	private Button mBtnShowMyDialog;
	private MyDialog mMyDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_dialog);
		
		mContext=this;
		mBtnShowDialog=(Button) findViewById(R.id.btn_show_dialog);
		mBtnShowDialog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
				builder.setTitle("1111");
				builder.setMessage("2222");
				builder.show();
				
				//Dialog dlg=new Dialog(mContext);
				//dlg.setContentView(R.layout.dialog_test);
				//dlg.setTitle("1111");
				//dlg.show();
				
//				DisplayMetrics metrics = getResources().getDisplayMetrics();
//				int width = metrics.widthPixels;
//				dlg.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
			}
		});

		mBtnShowMyDialog=findViewById(R.id.btn_show_my_dialog);
		mBtnShowMyDialog.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mMyDialog = new MyDialog(mContext);
				mMyDialog.show();
			}
		});
		
	}

}
