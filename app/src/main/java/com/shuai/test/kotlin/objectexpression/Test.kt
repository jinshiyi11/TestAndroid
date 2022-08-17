package com.shuai.test.kotlin.objectexpression


class MyClass {
    companion object Factory {
        const val NAME ="NAME"
        @JvmField
        var age = 2
        @JvmStatic
        fun create(): MyClass = MyClass()
    }
}

val x = MyClass
