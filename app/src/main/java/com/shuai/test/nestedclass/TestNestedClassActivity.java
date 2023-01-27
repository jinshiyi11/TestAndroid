package com.shuai.test.nestedclass;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.shuai.test.R;

public class TestNestedClassActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = TestNestedClassActivity.class.getSimpleName();
    private Button mBtnTest;
    private String mText = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_nested_class);



        mBtnTest = (Button) findViewById(R.id.btn_test);
        mBtnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,mText);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_test:
                break;
        }

    }
}
