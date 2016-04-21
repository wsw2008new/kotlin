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

package org.jetbrains.kotlin.uast.internal

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.resolve.constants.*
import org.jetbrains.kotlin.resolve.source.getPsi
import org.jetbrains.kotlin.uast.KotlinConverter
import org.jetbrains.uast.baseElements.*

fun ConstantValue<*>.getUastValue(project: Project): UConstantValue<*> {
    return when (this) {
        is AnnotationValue -> {
            val source = value.source.getPsi() as? KtAnnotationEntry ?: return UErrorValue
            UAnnotationValue(KotlinConverter.convertAnnotation(source, null))
        }
        is ArrayValue -> UArrayValue(value.map { it.getUastValue(project) })
        is BooleanValue -> UBooleanValue(value)
        is DoubleValue -> UDoubleValue(value)
        is EnumValue -> {
            val enumValueType = KotlinConverter.convertType(type, project, null)
            UEnumValue(enumValueType, enumValueType, enumValueType.name)
        }
        is FloatValue -> UFloatValue(value)
        is ByteValue -> UByteValue(value)
        is CharValue -> UCharValue(value)
        is IntValue -> UIntValue(value)
        is LongValue -> ULongValue(value)
        is ShortValue -> UShortValue(value)
        is KClassValue -> UTypeValue(KotlinConverter.convertType(type, project, null))
        is NullValue -> UNullValue
        is StringValue -> UStringValue(value)
        else -> UErrorValue
    }
}