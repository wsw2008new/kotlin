// KT-12044 Assertion "Rewrite at slice LEXICAL_SCOPE" for 'if' with property references

fun test() {
    data class Pair<F, S>(val first: F, val second: S)
    val (x, y) =
            Pair(1,
                 if (1 == 1)
                     Pair<String, String>::first
                 else
                     Pair<String, String>::second)
}
