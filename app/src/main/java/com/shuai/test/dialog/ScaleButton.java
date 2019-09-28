package com.shuai.test.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 *
 */
public class ScaleButton extends Button {

    public ScaleButton(Context context) {
        this(context,null);
    }

    public ScaleButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ScaleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                break;
        }
        return super.onTouchEvent(event);
    }
}
