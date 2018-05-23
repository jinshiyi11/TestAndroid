package com.shuai.test.reflect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.shuai.test.R;

import java.lang.reflect.Field;

public class TestReflectActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = TestReflectActivity.class.getSimpleName();
    private Button mBtnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_reflect);

        mBtnTest = (Button) findViewById(R.id.btn_test);
        mBtnTest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_test:
                User user = new User();
                Field[] fields = User.class.getDeclaredFields();
                try {
                    fields[0].setAccessible(true);
                    fields[0].set(user, 1);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "" + user);
                break;
        }
    }
}
