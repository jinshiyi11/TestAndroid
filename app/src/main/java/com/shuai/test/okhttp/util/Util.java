package com.shuai.test.okhttp.util;

import android.util.Log;

import com.shuai.test.okhttp.data.Const;

/**
 * @author shuaiweican
 * @date 2019/3/6
 */
public class Util {
    public static void logD(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void logE(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void logE(String tag, String msg, Throwable throwable) {
        Log.e(tag, msg, throwable);
    }

    public static boolean isTrue(String value) {
        if (value == null) {
            return false;
        }

        try {
            return Integer.parseInt(value) == Const.INT_TRUE;
        } catch (Exception ignored) {
        }
        return false;
    }

}
