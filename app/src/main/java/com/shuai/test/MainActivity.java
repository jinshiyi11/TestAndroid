package com.shuai.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.shuai.test.location.TestLocationActivity;
import com.shuai.test.recyclerview.TestRecyclerViewActivity;

/**
 *
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private Button mBtnTestLocation;
    private Button mBtnTestRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnTestLocation = findViewById(R.id.btn_test_location);
        mBtnTestLocation.setOnClickListener(this);
        mBtnTestRecyclerView = findViewById(R.id.btn_test_recycler_view);
        mBtnTestRecyclerView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_test_location) {
            Intent intent = new Intent(this, TestLocationActivity.class);
            startActivity(intent);
        } else if (id == R.id.btn_test_recycler_view) {
            Intent intent = new Intent(this, TestRecyclerViewActivity.class);
            startActivity(intent);
        }
    }
}
