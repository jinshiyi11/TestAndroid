package com.shuai.test.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.interfaces.OnInvokeView;
import com.shuai.test.R;

public class TestDialogActivity extends Activity {
    private Context mContext;
    private Button mBtnShowDialog;
    private Button mBtnShowMyDialog;
    private MyDialog mMyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_dialog);

        mContext = this;
        mBtnShowDialog = (Button) findViewById(R.id.btn_show_dialog);
        mBtnShowDialog.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                EasyFloat.with(TestDialogActivity.this)
                        .setShowPattern(ShowPattern.ALL_TIME)
                        .setTag("fff")
                        .setDragEnable(false)
                        .setLayout(R.layout.float_view, new OnInvokeView() {
                            @Override
                            public void invoke(View view) {
                                View rootView = view.getRootView();
                                WindowManager.LayoutParams lp = (WindowManager.LayoutParams) rootView.getLayoutParams();
                                lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                                //rootView.setLayoutParams(lp);

                                WindowManager windowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                                windowManager.updateViewLayout(rootView, lp);
                                final EditText editText = view.findViewById(R.id.et_test);
                                editText.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editText.requestFocus();
                                        //InputMethodUtils.openInputMethod(editText, "fff");

                                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                // 打开软键盘

                                                ((InputMethodManager) mContext.getApplicationContext().getSystemService(INPUT_METHOD_SERVICE))
                                                        .showSoftInput(editText, 0);
                                            }
                                        }, 100);
                                    }


                                });
                            }
                        })
                                        .show();


//				AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
//				builder.setTitle("1111");
//				builder.setMessage("2222");
//				builder.show();

                                //Dialog dlg=new Dialog(mContext);
                                //dlg.setContentView(R.layout.dialog_test);
                                //dlg.setTitle("1111");
                                //dlg.show();

//				DisplayMetrics metrics = getResources().getDisplayMetrics();
//				int width = metrics.widthPixels;
//				dlg.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
                            }
                        });

                mBtnShowMyDialog = findViewById(R.id.btn_show_my_dialog);
                mBtnShowMyDialog.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mMyDialog == null) {
                            mMyDialog = new MyDialog(mContext);
                        }
                        mMyDialog.show();
                    }
                });

            }

        }
