package com.shuai.test.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.shuai.test.MyApplication;
import com.squareup.leakcanary.RefWatcher;

/**
 *
 *
 */
public class LeakDetectLayout extends FrameLayout {
    private OnHierarchyChangeListener mListener = HierarchyTreeChangeListener.wrap(new OnHierarchyChangeListener() {
        @Override
        public void onChildViewAdded(View parent, View child) {
            if ((parent instanceof ViewGroup) && !(parent instanceof ListView) && !(parent instanceof RecyclerView)) {
                ViewGroup viewGroup = (ViewGroup) parent;
                if (viewGroup.getChildCount() > 30) {
                    watch(child, "why so many children");
                }
            }
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {
            watch(child, "");
        }

        private void watch(Object watchedReference, String referenceName) {
            RefWatcher refWatcher = MyApplication.getRefWatcher();
            //打开了leak canary开关才会初始化leak canary
            if (refWatcher != null) {
                refWatcher.watch(watchedReference, referenceName);
            }
        }
    });

    public LeakDetectLayout(@NonNull Context context) {
        this(context, null);
    }

    public LeakDetectLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeakDetectLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public LifeCycleMonitorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    private void init() {
        setOnHierarchyChangeListener(mListener);
    }
}
