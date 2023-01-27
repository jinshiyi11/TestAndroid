package com.shuai.test.recyclerview.grid;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shuai.test.R;
import com.shuai.test.recyclerview.MyAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TestRecyclerGridActivity extends Activity {
    private Context mContext;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);
        mContext = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this,3);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(mContext));
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<String> input = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            input.add("Test" + i);
        }// define an adapter
        mAdapter = new MyAdapter(input);
        mRecyclerView.setAdapter(mAdapter);
    }
}
