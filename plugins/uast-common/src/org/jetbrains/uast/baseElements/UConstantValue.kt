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

class UAnnotationValue(override val value: UAnnotation) : UConstantValue<UAnnotation>

class UArrayValue(override val value: List<UConstantValue<*>>) : UConstantValue<List<UConstantValue<*>>>

class UBooleanValue(override val value: Boolean) : UConstantValue<Boolean>

class UDoubleValue(override val value: Double) : UConstantValue<Double>

class UEnumValue(override val value: UType?, val enumType: UType, val valueName: String) : UConstantValue<UType?>

object UErrorValue : UConstantValue<Unit> {
    override val value = Unit
}

class UFloatValue(override val value: Float) : UConstantValue<Float>

interface UIntegralValue<T> : UConstantValue<T>

class UCharValue(override val value: Char) : UConstantValue<Char>

class UByteValue(override val value: Byte) : UConstantValue<Byte>

class UIntValue(override val value: Int) : UConstantValue<Int>

class ULongValue(override val value: Long) : UConstantValue<Long>

class UShortValue(override val value: Short) : UConstantValue<Short>

class UTypeValue(override val value: UType) : UConstantValue<UType>

object UNullValue : UConstantValue<Any?> {
    override val value = null
}

class UStringValue(override val value: String) : UConstantValue<String>