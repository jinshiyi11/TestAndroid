package com.shuai.test.recyclerview;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;

import com.shuai.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TestRecyclerViewActivity extends Activity implements View.OnClickListener {
    private ImageView mIvAdd;
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private List<String> mData = new ArrayList<>();
    private SimpleItemTouchCallback mTouchCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);
        mIvAdd = findViewById(R.id.iv_add);
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
        mIvAdd.setOnClickListener(this);

        for (int i = 0; i < 5; i++) {
            mData.add("Test" + i);
        }// define an adapter
        mAdapter = new MyAdapter(mData);
        recyclerView.setAdapter(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(new SimpleItemTouchCallback(mAdapter, mData));
        helper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_add) {
            int start = mData.size();
            mData.add("Test" + start);
            mAdapter.notifyItemRangeInserted(start,1);
        }

    }
}
