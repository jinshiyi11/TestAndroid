package com.shuai.test.service;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import com.shuai.test.IMyService;
import com.shuai.test.R;

public class TestServiceActivity extends Activity {
    private Button mBtnBindService;
    private IMyService mMyService;
    private ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMyService=IMyService.Stub.asInterface(service);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_service);

        mBtnBindService= (Button) findViewById(R.id.btn_bind_service);
        mBtnBindService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TestServiceActivity.this,TestService.class);
                bindService(intent,mServiceConnection, Service.BIND_AUTO_CREATE);
            }
        });
    }
}
