package com.shuai.test.kotlin.delegate

interface Base {
    fun print()
    fun hello()
}

class BaseImpl(val x: Int) : Base {
    override fun print() { print(x) }
    override fun hello() {
        println("hello")
    }
}

class Derived(val b: Base) : Base by b{
    override fun hello() {
        b.hello()
        println("byebye")
    }
}

fun main() {
    val b = BaseImpl(10)
    Derived(b).hello()
}