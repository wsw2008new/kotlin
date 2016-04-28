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

package org.jetbrains.uast.java.internal

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import org.jetbrains.uast.*
import org.jetbrains.uast.java.JavaConverter

fun PsiAnnotationMemberValue?.getUastValue(project: Project): UConstantValue<*> {
    if (this == null) return UErrorValue

    val value = JavaPsiFacade.getInstance(project).constantEvaluationHelper.computeConstantExpression(this)

    if (this is PsiLiteralExpression && this.type == PsiType.NULL) {
        return UNullValue
    }

    val literalValue = value ?: (this as? PsiLiteralExpression)?.value
    if (literalValue != null) {
        when (literalValue) {
            is Double -> UDoubleValue(literalValue)
            is Float -> UFloatValue(literalValue)
            is String -> UStringValue(literalValue)
            is Char -> UCharValue(literalValue)
            is Byte -> UByteValue(literalValue)
            is Short -> UShortValue(literalValue)
            is Int -> UIntValue(literalValue)
            is Long -> ULongValue(literalValue)
        }
    }

    return when (this) {
        is PsiReferenceExpression -> {
            val element = resolve()
            if (element is PsiEnumConstant) {
                UEnumValue(null, JavaConverter.convertType(element.type), element.name ?: "<error>")
            } else {
                UErrorValue
            }
        }
        is PsiArrayInitializerMemberValue -> UArrayValue(initializers.map { it.getUastValue(project) })
        is PsiAnnotation -> UAnnotationValue(JavaConverter.convertAnnotation(this, null))
        is PsiClassObjectAccessExpression -> UTypeValue(JavaConverter.convertType(type))
        else -> throw UnsupportedOperationException("Unsupported annotation this type: " + this)
    }
}