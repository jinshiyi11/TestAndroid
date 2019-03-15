package com.shuai.test.okhttp.util;

import android.util.Log;

import com.shuai.test.okhttp.cache.CacheConst;

/**
 * @author shuaiweican
 * @date 2019/3/6
 */
public class Util {
    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable throwable) {
        Log.e(tag, msg, throwable);
    }

    public static boolean isDebug() {
        return true;
    }
}
