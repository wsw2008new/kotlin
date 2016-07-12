// WITH_REFLECT

import kotlin.reflect.KTypeParameter
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class A<U> {
    fun <T> foo(): T = null!!
    fun bar(): Array<U>? = null!!
}

fun box(): String {
    val t = A::class.members.single { it.name == "foo" }.returnType
    assertFalse(t.isMarkedNullable)
    assertTrue(t.classifier is KTypeParameter)

    val u = A::class.members.single { it.name == "bar" }.returnType
    assertTrue(u.isMarkedNullable)
    assertEquals(Array<Any>::class, u.classifier)

    return "OK"
}
