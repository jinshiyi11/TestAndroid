package com.shuai.test.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;

import java.util.Collections;
import java.util.List;

/**
 *
 */

public class SimpleItemTouchCallback extends Callback {
    private MyAdapter mAdapter;
    private List<String> mData;

    public SimpleItemTouchCallback(MyAdapter adapter, List<String> data) {
        mAdapter = adapter;
        mData = data;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN; //s上下拖拽
        int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END; //左->右和右->左滑动
        return makeMovementFlags(dragFlag,swipeFlag);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int from = viewHolder.getAdapterPosition();
        int to = target.getAdapterPosition();
        Collections.swap(mData, from, to);
        mAdapter.notifyItemMoved(from, to);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int pos = viewHolder.getAdapterPosition();
        mData.remove(pos);
        mAdapter.notifyItemRemoved(pos);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE){
            MyAdapter.ViewHolder holder = (MyAdapter.ViewHolder)viewHolder;
            holder.itemView.setBackgroundColor(0xffbcbcbc); //设置拖拽和侧滑时的背景色
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        MyAdapter.ViewHolder holder = (MyAdapter.ViewHolder)viewHolder;
        holder.itemView.setBackgroundColor(0xffeeeeee); //背景色还原
    }
}
