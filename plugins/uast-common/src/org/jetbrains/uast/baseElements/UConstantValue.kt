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

package org.jetbrains.uast.baseElements

import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UType

interface UConstantValue<T> {
    val value: T
}

interface USimpleConstantValue<T> : UConstantValue<T>

class UAnnotationValue(override val value: UAnnotation) : UConstantValue<UAnnotation>

class UArrayValue(override val value: List<UConstantValue<*>>) : UConstantValue<List<UConstantValue<*>>>

class UEnumValue(override val value: UType?, val enumType: UType, val valueName: String) : UConstantValue<UType?>

object UErrorValue : UConstantValue<Unit> {
    override val value = Unit
}

interface UIntegralValue<T> : USimpleConstantValue<T>

interface URealValue<T> : USimpleConstantValue<T>

class UDoubleValue(override val value: Double) : URealValue<Double>

class UFloatValue(override val value: Float) : URealValue<Float>

class UCharValue(override val value: Char) : USimpleConstantValue<Char>

class UByteValue(override val value: Byte) : UIntegralValue<Byte>

class UIntValue(override val value: Int) : UIntegralValue<Int>

class ULongValue(override val value: Long) : UIntegralValue<Long>

class UShortValue(override val value: Short) : UIntegralValue<Short>

class UTypeValue(override val value: UType) : UConstantValue<UType>

object UNullValue : USimpleConstantValue<Any?> {
    override val value = null
}

class UBooleanValue(override val value: Boolean) : USimpleConstantValue<Boolean>

class UStringValue(override val value: String) : USimpleConstantValue<String>