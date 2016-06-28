@file:kotlin.jvm.JvmMultifileClass
@file:kotlin.jvm.JvmName("ExceptionsKt")
@file:kotlin.jvm.JvmVersion
@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "DeprecatedCallableAddReplaceWith")
package kotlin

public typealias Error = java.lang.Error
public typealias Exception = java.lang.Exception
public typealias RuntimeException = java.lang.RuntimeException
public typealias IllegalArgumentException = java.lang.IllegalArgumentException
public typealias IllegalStateException = java.lang.IllegalStateException
public typealias IndexOutOfBoundsException = java.lang.IndexOutOfBoundsException
public typealias UnsupportedOperationException = java.lang.UnsupportedOperationException

public typealias NumberFormatException = java.lang.NumberFormatException
public typealias NullPointerException = java.lang.NullPointerException
public typealias ClassCastException = java.lang.ClassCastException
public typealias AssertionError = java.lang.AssertionError

public typealias NoSuchElementException = java.util.NoSuchElementException

/**
 * Returns an array of stack trace elements representing the stack trace
 * pertaining to this throwable.
 */
@Deprecated("Provided for binary compatibility", level = DeprecationLevel.HIDDEN)
public val Throwable.stackTrace: Array<StackTraceElement>
    get() = stackTrace!!
