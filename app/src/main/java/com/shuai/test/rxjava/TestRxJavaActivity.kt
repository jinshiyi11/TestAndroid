package com.shuai.test.rxjava

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.shuai.test.R
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.math.min

class TestRxJavaActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private val TAG = TestRxJavaActivity::class.java.simpleName
    }

    private var mBtnTest: Button? = null
    private var mSubscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_rx_java)
        val b = null is TestRxJavaActivity
        mBtnTest = findViewById<View>(R.id.btn_test) as Button
        mBtnTest!!.setOnClickListener(this)
        findViewById<View>(R.id.btn_stop).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_test ->
                testTimeout()
            R.id.btn_stop ->
                stop()
        }
    }

    private fun stop() {
        mSubscription?.let {
            it.unsubscribe()
            mSubscription = null
        }
    }

    private fun test() {
        Observable.from(arrayOf("aa", "bb", "cc", "dd"))
                .flatMap<Any> { s ->
                    Observable.create<Void> { subscriber ->
                        Log.d(TAG, "call:" + s + Thread.currentThread().name)
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
                        val t = Thread {
                            subscriber.onNext(null)
                            subscriber.onCompleted()
                        }
                        t.start()
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Any?> {
                    override fun onCompleted() {
                        Log.d(TAG, "onCompleted")
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, "onError")
                    }

                    override fun onNext(o: Any?) {
                        Log.d(TAG, "onNext")
                    }
                })
    }

    private fun testTimeout() {
        var delay = 9
        var period = 2
        val timeout = 60
        var progressPeriod = 1
        mSubscription = Observable.merge(
                //轮询
                Observable.interval(delay.toLong(), period.toLong(), TimeUnit.SECONDS, AndroidSchedulers.mainThread()),
                //TODO:weican 测试超时
                Observable.never<Long>().timeout(timeout.toLong(), TimeUnit.SECONDS, AndroidSchedulers.mainThread()),
                //更新进度
                Observable.interval(progressPeriod.toLong(), progressPeriod.toLong(), TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                        .filter {
                            //压缩进度占比20%，这里拿不到压缩进度，做个假进度
                            val progress = min((80 + (it + 1) * progressPeriod * 20.0 / timeout).toInt(), 100)
                            Log.i(TAG, "轮询进度:${progress}")
                            //只更新进度，不发送事件
                            false
                        }
        ).subscribe({
            Log.i(TAG, "next :${it}")
            if(it==2L){
                mSubscription?.unsubscribe()
            }

        }, {
            Log.e(TAG, "error:${Log.getStackTraceString(it)}")
        })
    }
}