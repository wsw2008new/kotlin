package foo

import lib.*

fun qqq() = 23

fun box(): String {
    assertEquals(24, baz {
        global += "before;"
        val result = qqq()
        global += "after;"
        result
    })

    return "OK"
}