package com.shuai.test.spinner;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.shuai.test.R;

public class DialogSpinner extends androidx.appcompat.widget.AppCompatButton /*implements android.content.DialogInterface.OnClickListener*/implements
        OnItemClickListener {
    private Dialog mPopup;
    private ListAdapter mAdapter;
    private ListView mListView;
    
    public interface OnItemSelectedListener{
        /**
         * 
         * @param view 表示控件自己
         * @param position
         */
        public void onItemSelected(View view, int position);
    }

    private OnItemSelectedListener mOnItemSelectedListener;

    public static final int INVALID_POSITION = -1;
    private int mSelectePosition = INVALID_POSITION;

    public DialogSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DialogSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogSpinner(Context context) {
        super(context);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mPopup != null && mPopup.isShowing()) {
            mPopup.dismiss();
            mPopup = null;
        }
    }

    public void setAdapter(ListAdapter adapter) {
        mAdapter = adapter;
        
        setSelection(0);            
    }

    @Override
    public boolean performClick() {
        boolean handled = super.performClick();

        if (!handled) {
            handled = true;
            Context context = getContext();

            mPopup = new Dialog(context);
            mPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mPopup.setContentView(R.layout.spinner_listview);
            mListView = (ListView) mPopup.findViewById(R.id.listview);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(this);
            mPopup.setCancelable(true);
            mPopup.setCanceledOnTouchOutside(true);

            View anchor = this;
            final int anchorHeight = anchor.getHeight();
            int[] mDrawingLocation = new int[2];
            anchor.getLocationInWindow(mDrawingLocation);

            //            int[] location = new int[2];
            //            anchor.getLocationOnScreen(location);

            WindowManager.LayoutParams wmlp = mPopup.getWindow().getAttributes();
            //mPopup.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            
            mPopup.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.spinner_bg));

            wmlp.gravity = Gravity.TOP | Gravity.LEFT;
            wmlp.width = anchor.getWidth();
            //wmlp.height=100;
            wmlp.x = mDrawingLocation[0] + 0/*+xoff*/;
            wmlp.y = mDrawingLocation[1] + anchorHeight/*+yoff*/- getStatusBarHeight();

            mListView.measure(MeasureSpec.makeMeasureSpec(wmlp.width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(0xffff, MeasureSpec.AT_MOST));

            //检查最大高度,listview的最大高度不能超过屏幕的一半
            int measuredHeight = mListView.getMeasuredHeight();
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            int maxHeight = screenHeight / 2;
            if (measuredHeight > maxHeight) {
                LayoutParams layoutParams = mListView.getLayoutParams();
                layoutParams.height = maxHeight;
                mListView.setLayoutParams(layoutParams);
            }

            mPopup.getWindow().setAttributes(wmlp);
            mPopup.show();
        }

        return handled;
    }

    /**
     * 获取状态栏高度
     * 
     * @return 状态栏高度
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        } else {
            float dpValue = 25;
            result = (int) (dpValue * getResources().getDisplayMetrics().density + 0.5f);
        }
        return result;
    }

    //    @Override
    //    public void onClick(DialogInterface dialog, int which) {
    //        //setSelection(which);
    //        mSelectedItem=which;
    //        dialog.dismiss();
    //        mPopup = null;
    //    }

    public Object getSelectedItem() {
        int selection = getSelectedItemPosition();
        if (mAdapter != null && mAdapter.getCount() > 0 && selection >= 0) {
            return mAdapter.getItem(selection);
        } else {
            return null;
        }
    }

    public int getSelectedItemPosition() {
        return mSelectePosition;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
    }

    public final OnItemSelectedListener getOnItemSelectedListener() {
        return mOnItemSelectedListener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setSelection(position);

        if (mPopup != null && mPopup.isShowing()) {
            mPopup.dismiss();
            mPopup = null;
        }
    }

    public void setSelection(int i) {
        if(i>=mAdapter.getCount() || i<0){
            mSelectePosition=INVALID_POSITION;
            return;
        }
            
        if(mSelectePosition!=i){
            mSelectePosition=i;
                
            if(mSelectePosition>=0 && mAdapter.getCount()>0){
                Object object = mAdapter.getItem(mSelectePosition);
                setText(object.toString());
            }
            
            if (mSelectePosition >= 0 && mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemSelected(this,mSelectePosition);
            }
        }
    }

}
