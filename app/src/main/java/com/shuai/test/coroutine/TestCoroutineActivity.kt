package com.shuai.test.coroutine

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.shuai.test.databinding.ActivityTestCoroutineBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 协程测试
 */
class TestCoroutineActivity : AppCompatActivity() {
    companion object {
        const val TAG = "TestCoroutineActivity"
    }

    private lateinit var mBinding: ActivityTestCoroutineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityTestCoroutineBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.btnTest.setOnClickListener {
            test()
        }
    }

    private fun test() {
        GlobalScope.launch {
            Log.i(TAG, "test")
        }
    }
}