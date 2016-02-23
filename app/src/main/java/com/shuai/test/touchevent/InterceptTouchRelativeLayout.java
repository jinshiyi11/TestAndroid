package com.shuai.test.touchevent;

import android.content.Context;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class InterceptTouchRelativeLayout extends RelativeLayout {
    private static final String TAG=InterceptTouchRelativeLayout.class.getSimpleName();
    
    public InterceptTouchRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public InterceptTouchRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent --"+ev);
        //return super.onInterceptTouchEvent(ev);
        //return true;
        
        
        if(ev.getActionMasked()==/*MotionEvent.ACTION_UP*/MotionEvent.ACTION_MOVE){
            return true;
        }else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent --"+event);
        //return super.onTouchEvent(event);
        return true;
    }
    
    
    
    

}
