package com.shuai.test.util;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 *
 */
public class LeakDetectHelper {
    private static final String TAG = LeakDetectHelper.class.getSimpleName();

    public static void release(Activity activity) {
        if (activity == null) {
            return;
        }

        ((ViewGroup) activity.getWindow().getDecorView()).removeAllViews();
        LeakDetectHelper.setFieldNull(activity);
    }

    public static void setFieldNull(Object obj) {
        if (obj == null) {
            return;
        }

        Class<?> clazz = obj.getClass();
        while (clazz != null && clazz != Object.class && !clazz.getName().contains("android")) {
            setNull(clazz, obj);
            clazz = clazz.getSuperclass();
        }
    }

    private static void setNull(Class clazz, Object obj) {
        if (clazz == null) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.getType().isPrimitive() && (field.getModifiers() & Modifier.STATIC) == 0) {
                try {
                    field.setAccessible(true);
                    field.set(obj, null);
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "", e);
                }
            }
        }
    }
}
