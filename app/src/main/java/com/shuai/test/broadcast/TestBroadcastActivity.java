package com.shuai.test.broadcast;

import com.shuai.test.R;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestBroadcastActivity extends Activity {
    
    private Button mBtnSend;
    private MyReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_broadcast);
        
        mReceiver=new MyReceiver();
        mBtnSend=(Button) findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyReceiver.ACTION_TEST);
                intent.putExtra(MyReceiver.ACTION_TEST_NAME, 2);
                sendBroadcast(intent);
            }
        });
        
        IntentFilter filter=new IntentFilter(MyReceiver.ACTION_TEST);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }
}
