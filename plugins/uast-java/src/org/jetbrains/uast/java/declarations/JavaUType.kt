/*
 * Copyright 2000-2016 JetBrains s.r.o.
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
package org.jetbrains.uast.java

import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiType
import com.intellij.psi.PsiWildcardType
import org.jetbrains.uast.*
import org.jetbrains.uast.kinds.UastVariance

class JavaUType(
        val psi: PsiType?,
        override val parent: UElement?
) : JavaAbstractUElement(), UType {
    override val name: String
        get() = when (psi) {
            is PsiClassType -> psi.className.substringAfterLast('.')
            else -> psi?.canonicalText?.substringAfterLast('.')
        }.orAnonymous("type")

    override val fqName: String?
        get() = when (psi) {
            is PsiClassType -> psi.resolve()?.qualifiedName
            else -> null
        }

    override val isInt: Boolean
        get() = check("int", "java.lang.Integer")

    override val isLong: Boolean
        get() = check("long", "java.lang.Long")

    override val isShort: Boolean
        get() = check("short", "java.lang.Short")

    override val isFloat: Boolean
        get() = check("float", "java.lang.Float")

    override val isDouble: Boolean
        get() = check("double", "java.lang.Double")

    override val isChar: Boolean
        get() = check("char", "java.lang.Character")

    override val isBoolean: Boolean
        get() = check("boolean", "java.lang.Boolean")

    override val isByte: Boolean
        get() = check("byte", "java.lang.Byte")

    override val isString: Boolean
        get() = (psi as? PsiClassType)?.resolve()?.qualifiedName == "java.lang.String"

    override val isObject: Boolean
        get() = (psi as? PsiClassType)?.resolve()?.qualifiedName == "java.lang.Object"

    override val isArray: Boolean
        get() = false

    override val parameters by lz {
        val classType = psi as? PsiClassType ?: return@lz emptyList<UTypeProjection>()
        if (!classType.hasParameters()) return@lz emptyList<UTypeProjection>()
        classType.parameters.map {
            val type = JavaConverter.convertType(it, null)
            val variance = when (it) {
                is PsiWildcardType -> {
                    if (it.isSuper)
                        UastVariance.CONTRAVARIANT
                    else if (it.isExtends)
                        UastVariance.CONTRAVARIANT
                    else
                        UastVariance.UNKNOWN
                }
                else -> UastVariance.INVARIANT
            }
            object : UTypeProjection {
                override val type = type
                override val variance = variance
            }
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun check(unboxedType: String, boxedType: String): Boolean =
            name == unboxedType || (psi as? PsiClassType)?.resolve()?.qualifiedName == boxedType

    override val annotations by lz { psi.getAnnotations(this) }

    override fun resolve(context: UastContext) = when (psi) {
        is PsiClassType -> psi.resolve()?.let { context.convert(it) as? UClass }
        else -> null
    }
}

class JavaUArrayType(val type: PsiArrayType, override val parent: UElement?) : UType {
    override val name: String
        get() = "Array"
    override val fqName: String
        get() = "Array"

    override val isInt: Boolean
        get() = false
    override val isShort: Boolean
        get() = false
    override val isLong: Boolean
        get() = false
    override val isFloat: Boolean
        get() = false
    override val isDouble: Boolean
        get() = false
    override val isChar: Boolean
        get() = false
    override val isBoolean: Boolean
        get() = false
    override val isByte: Boolean
        get() = false
    override val isString: Boolean
        get() = false
    override val isObject: Boolean
        get() = false

    override val isArray: Boolean
        get() = true

    override val parameters: List<UTypeProjection> by lz {
        val type = JavaConverter.convertType(type.componentType, this)
        val typeProjection = object : UTypeProjection {
            override val type = type
            override val variance: UastVariance
                get() = UastVariance.INVARIANT
        }
        listOf(typeProjection)
    }

    override fun resolve(context: UastContext) = null

    override val annotations: List<UAnnotation>
        get() = emptyList()
}