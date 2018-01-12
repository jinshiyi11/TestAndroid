package com.shuai.test.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.shuai.test.ITest;
import com.shuai.test.R;

public class TestServiceActivity extends Activity implements View.OnClickListener {
    private final static String TAG = TestServiceActivity.class.getSimpleName();
    private Button mBtnTestBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_service);

        mBtnTestBind = (Button) findViewById(R.id.btn_test_bind);
        mBtnTestBind.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_test_bind:
                Intent intent = new Intent(this, TestService.class);
                bindService(intent, new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                        ITest iTest = ITest.Stub.asInterface(iBinder);
                        try {
                            iTest.getName(0, "hello");
                            iTest.testStrictPolicy();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        Log.d(TAG,"onServiceConnected finish");
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {
                        Log.d(TAG,"onServiceDisconnected finish");
                    }
                }, Context.BIND_AUTO_CREATE);
                break;
        }
    }
}
