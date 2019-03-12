package com.shuai.test.okhttp.util;

import android.telecom.Call;

import com.shuai.test.okhttp.data.CachePolicy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import okhttp3.Request;

/**
 * @author shuaiweican
 * @date 2019/3/11
 */
public class ReflectUtil {
    private static final String TAG = ReflectUtil.class.getSimpleName();

    public static void setRequestTag(Request request, CachePolicy cachePolicy) {
        if (request != null/* && request.tag() == null*/) {
            try {
                Field tagField = Request.class.getDeclaredField("tag");
                tagField.setAccessible(true);

//                Field modifiersField = Field.class.getDeclaredField("modifiers");
//                modifiersField.setAccessible(true);
//                modifiersField.setInt(tagField, tagField.getModifiers() & ~Modifier.FINAL);

                tagField.set(request, cachePolicy);
            } catch (Exception e) {
                Util.logE(TAG, e.getMessage(), e);
            }
        }

    }
}
