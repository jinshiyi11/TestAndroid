package com.shuai.test.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

/**
 *
 */
public class MyDialog extends Dialog implements DialogInterface.OnDismissListener {
    private Context mContext;
    private TextView mTvTest;

    public MyDialog(@NonNull Context context) {
        this(context, 0);
    }

    public MyDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        setOnDismissListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTvTest = new TextView(mContext);
        mTvTest.setText("111113333");
        setContentView(mTvTest);

//        setContentView(new FrameLayout(mContext));

        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width= ViewGroup.LayoutParams.MATCH_PARENT;
        attributes.height = 400;
        attributes.gravity= Gravity.BOTTOM;
        getWindow().setAttributes(attributes);
    }


    @Override
    public void onDismiss(DialogInterface dialog) {

    }
}
