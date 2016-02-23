package com.shuai.test.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    public static final String ACTION_TEST="test";
    public static final String ACTION_TEST_NAME="info";

    @Override
    public void onReceive(Context context, Intent intent) {
    }

}
