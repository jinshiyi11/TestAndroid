package com.shuai.test.rxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.shuai.test.R;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class TestRxJavaActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG=TestRxJavaActivity.class.getSimpleName();
    private Button mBtnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_rx_java);

        boolean b=(null instanceof TestRxJavaActivity);
        mBtnTest = (Button) findViewById(R.id.btn_test);
        mBtnTest.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test:
                Observable.from(new String[]{"aa", "bb","cc","dd"})
                        .flatMap(new Func1<String, Observable<?>>() {
                            @Override
                            public Observable<?> call(final String s) {
                                return Observable.create(new Observable.OnSubscribe<Void>(){

                                    @Override
                                    public void call(final Subscriber<? super Void> subscriber) {
                                        Log.d(TAG,"call:"+s+Thread.currentThread().getName());
//                                        try {
//                                            Thread.sleep(8000);
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                        }
//                                        if(s.equals("bb")){
//                                            subscriber.onError(null);
//                                        }else {
//                                            subscriber.onNext(null);
//                                            subscriber.onCompleted();
//                                        }

                                        Thread t=new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                subscriber.onNext(null);
                                                subscriber.onCompleted();
                                            }
                                        });
                                        t.start();
                                    }
                                });
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Object>() {

                            @Override
                            public void onCompleted() {
                                Log.d(TAG,"onCompleted");

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG,"onError");
                            }

                            @Override
                            public void onNext(Object o) {

                                Log.d(TAG,"onNext");
                            }
                        });
                break;
        }

    }
}
