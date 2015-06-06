package com.shuai.test.touchevent;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.shuai.test.R;

public class TestTouchActivity extends Activity {
    private final static String TAG=TestTouchActivity.class.getSimpleName();
    private TextView tvTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_touch);
        
        tvTest=(TextView) findViewById(R.id.tv_test);
        tvTest.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick");
            }
        });
        
        tvTest.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG,"onTouch:"+event);
                //return false;
                return true;
            }
        });
    }

}
