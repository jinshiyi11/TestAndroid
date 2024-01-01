package com.shuai.test.image

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.shuai.test.R

class TestGlideActivity : AppCompatActivity() {
    private lateinit var mIvTest: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_glide)
        mIvTest = findViewById(R.id.iv_test)
        findViewById<View>(R.id.tv_test).setOnClickListener {
            Glide.with(this).load("https://wx3.sinaimg.cn/mw690/6db418c5gy1hkptnot64sj20u20u0wlv.jpg").into(mIvTest)
        }

    }
}