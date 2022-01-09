package com.shuai.test.kotlin.objectexpression


class MyClass {
    companion object Factory {
        const val NAME ="NAME"
        @JvmStatic
        var age = 2
        fun create(): MyClass = MyClass()
    }
}
