package com.shuai.test.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.shuai.test.R;

public class TestScrollViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean b=(null instanceof  TestScrollViewActivity);
        setContentView(R.layout.activity_test_scroll_view);

    }
}
