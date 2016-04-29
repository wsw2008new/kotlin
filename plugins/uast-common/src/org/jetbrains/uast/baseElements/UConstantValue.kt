/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.uast

interface UConstantValue<T> {
    val value: T

    val original: UExpression?
        get() = null
}

interface USimpleConstantValue<T> : UConstantValue<T>

open class UAnnotationValue(override val value: UAnnotation) : UConstantValue<UAnnotation>

open class UArrayValue(override val value: List<UConstantValue<*>>) : UConstantValue<List<UConstantValue<*>>>

open class UEnumValue(override val value: UType?, val enumType: UType, val valueName: String) : UConstantValue<UType?>

object UErrorValue : UConstantValue<Unit> {
    override val value = Unit
}

interface UIntegralValue<T> : USimpleConstantValue<T>

interface URealValue<T> : USimpleConstantValue<T>

open class UDoubleValue(override val value: Double) : URealValue<Double>

open class UFloatValue(override val value: Float) : URealValue<Float>

open class UCharValue(override val value: Char) : USimpleConstantValue<Char>

open class UByteValue(override val value: Byte) : UIntegralValue<Byte>

open class UIntValue(override val value: Int) : UIntegralValue<Int>

open class ULongValue(override val value: Long) : UIntegralValue<Long>

open class UShortValue(override val value: Short) : UIntegralValue<Short>

open class UBooleanValue(override val value: Boolean) : USimpleConstantValue<Boolean>

open class UStringValue(override val value: String) : USimpleConstantValue<String>

open class UTypeValue(override val value: UType) : UConstantValue<UType>

open class UExpressionValue(override val value: UExpression) : UConstantValue<UExpression>

object UNullValue : USimpleConstantValue<Any?> {
    override val value = null
}