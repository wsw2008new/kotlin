// WITH_RUNTIME

abstract class S<T>(val klass: Class<T>) {
    val result = klass.simpleName
}

object OK : S<OK>(OK::class.java)

fun box(): String {
    return OK.result
}
