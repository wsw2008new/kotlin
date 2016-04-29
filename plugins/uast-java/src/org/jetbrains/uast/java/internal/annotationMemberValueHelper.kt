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
import org.jetbrains.uast.java.lz

internal fun PsiAnnotationMemberValue?.getUastValue(project: Project): UConstantValue<*> {
    if (this == null) return UErrorValue

    if (this is PsiLiteralExpression && this.type == PsiType.NULL) {
        return UNullValue
    }

    fun computeConstantExpression() = JavaPsiFacade.getInstance(project).constantEvaluationHelper.computeConstantExpression(this)

    val literalValue = (this as? PsiLiteralExpression)?.value ?: computeConstantExpression()

    if (literalValue != null) {
        when (literalValue) {
            is Boolean -> return JavaUBooleanValue(literalValue, this)
            is Double -> return JavaUDoubleValue(literalValue, this)
            is Float -> return JavaUFloatValue(literalValue, this)
            is String -> return JavaUStringValue(literalValue, this)
            is Char -> return JavaUCharValue(literalValue, this)
            is Byte -> return JavaUByteValue(literalValue, this)
            is Short -> return JavaUShortValue(literalValue, this)
            is Int -> return JavaUIntValue(literalValue, this)
            is Long -> return JavaULongValue(literalValue, this)
        }
    }

    return when (this) {
        is PsiReferenceExpression -> {
            val element = resolve()
            if (element is PsiEnumConstant) {
                UEnumValue(null, JavaConverter.convertType(element.type), element.name ?: "<error>")
            }
            else {
                UErrorValue
            }
        }
        is PsiArrayInitializerMemberValue -> UArrayValue(initializers.map { it.getUastValue(project) })
        is PsiAnnotation -> UAnnotationValue(JavaConverter.convertAnnotation(this, null))
        is PsiClassObjectAccessExpression -> UTypeValue(JavaConverter.convertType(type))
        else -> throw UnsupportedOperationException("Unsupported annotation this type: " + this)
    }
}

private interface WithOriginal {
    val psi: PsiAnnotationMemberValue
}

private fun WithOriginal.calc() = JavaConverter.convertWithoutParent(psi) as? UExpression

class JavaUDoubleValue(override val value: Double, override val psi: PsiAnnotationMemberValue) : URealValue<Double>, WithOriginal {
    override val original by lz { calc() }
}

class JavaUFloatValue(override val value: Float, override val psi: PsiAnnotationMemberValue) : URealValue<Float>, WithOriginal {
    override val original by lz { calc() }
}

class JavaUCharValue(override val value: Char, override val psi: PsiAnnotationMemberValue) : USimpleConstantValue<Char>, WithOriginal {
    override val original by lz { calc() }
}

class JavaUByteValue(override val value: Byte, override val psi: PsiAnnotationMemberValue) : UIntegralValue<Byte>, WithOriginal {
    override val original by lz { calc() }
}

class JavaUIntValue(override val value: Int, override val psi: PsiAnnotationMemberValue) : UIntegralValue<Int>, WithOriginal {
    override val original by lz { calc() }
}

class JavaULongValue(override val value: Long, override val psi: PsiAnnotationMemberValue) : UIntegralValue<Long>, WithOriginal {
    override val original by lz { calc() }
}

class JavaUShortValue(override val value: Short, override val psi: PsiAnnotationMemberValue) : UIntegralValue<Short>, WithOriginal {
    override val original by lz { calc() }
}

class JavaUBooleanValue(override val value: Boolean, override val psi: PsiAnnotationMemberValue) : USimpleConstantValue<Boolean>, WithOriginal {
    override val original by lz { calc() }
}

class JavaUStringValue(override val value: String, override val psi: PsiAnnotationMemberValue) : USimpleConstantValue<String>, WithOriginal {
    override val original by lz { calc() }
}