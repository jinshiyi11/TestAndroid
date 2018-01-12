package com.shuai.test.recyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.shuai.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TestRecyclerViewActivity extends Activity {
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private SimpleItemTouchCallback mTouchCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        List<String> input = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            input.add("Test" + i);
        }// define an adapter
        mAdapter = new MyAdapter(input);
        recyclerView.setAdapter(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(new SimpleItemTouchCallback(mAdapter,input));
        helper.attachToRecyclerView(recyclerView);
    }
}
