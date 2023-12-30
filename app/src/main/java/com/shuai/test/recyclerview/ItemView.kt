package com.shuai.test.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.shuai.test.R

/**
 *
 */
class ItemView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mData: String? = null

    init {
        inflate(context, R.layout.row_layout, this)
    }

    fun setData(data: String?) {
        mData = data
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
    }
}