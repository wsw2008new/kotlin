// WITH_REFLECT

import kotlin.test.assertEquals

class F {
    fun <A> foo() {}
    val <B> B.bar: B get() = this
}

class C<D> {
    fun baz() {}
    fun <E, G> quux() {}
}

fun box(): String {
    assertEquals(0, F::class.typeParameters.size)
    assertEquals(1, F::class.members.single { it.name == "foo" }.typeParameters.size)
    assertEquals(1, F::class.members.single { it.name == "bar" }.typeParameters.size)

    assertEquals(1, C::class.typeParameters.size)
    assertEquals(0, C::class.members.single { it.name == "baz" }.typeParameters.size)
    assertEquals(2, C::class.members.single { it.name == "quux" }.typeParameters.size)

    return "OK"
}
