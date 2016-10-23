package com.shuai.test.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.shuai.test.IMyService;

public class TestService extends Service {

    private IMyService.Stub mMyService;

    public TestService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMyService=new IMyService.Stub(){

            @Override
            public int add(int a, int b) throws RemoteException {
                return a+b;
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMyService.asBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
