package com.shuai.test.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.shuai.test.ITest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class TestService extends Service {
    private final static String TAG = TestService.class.getSimpleName();
    private ITest mTest;

    private static class MyTestAidl extends ITest.Stub {

        @Override
        public String getName(int id, String type) throws RemoteException {
            Log.d(TAG, "getName");
//            throw new NullPointerException("null");
//            throw new Error("eeee");
            return "xxx";
        }

        @Override
        public String testStrictPolicy() throws RemoteException {
            try {
                URL url = new URL("http://www.baidu.com");
                URLConnection conn = url.openConnection();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                try {
                    in.read();
                } finally {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public TestService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        mTest = new MyTestAidl();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mTest.asBinder();
    }
}
