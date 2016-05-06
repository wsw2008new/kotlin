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

package org.jetbrains.uast.java.declarations

import com.intellij.psi.PsiPrimitiveType
import org.jetbrains.uast.*
import org.jetbrains.uast.java.JavaAbstractUElement
import org.jetbrains.uast.java.getAnnotations

class JavaPrimitiveUType(val psi: PsiPrimitiveType): JavaAbstractUElement(), UType {
    override val name: String
        get() = psi.getCanonicalText(false)

    override val isInt: Boolean
        get() = psi === PsiPrimitiveType.INT

    override val isShort: Boolean
        get() = psi === PsiPrimitiveType.SHORT

    override val isLong: Boolean
        get() = psi === PsiPrimitiveType.LONG

    override val isFloat: Boolean
        get() = psi === PsiPrimitiveType.FLOAT

    override val isDouble: Boolean
        get() = psi === PsiPrimitiveType.DOUBLE

    override val isChar: Boolean
        get() = psi === PsiPrimitiveType.CHAR

    override val isBoolean: Boolean
        get() = psi === PsiPrimitiveType.BOOLEAN

    override val isByte: Boolean
        get() = psi === PsiPrimitiveType.BYTE

    override val isVoid: Boolean
        get() = psi === PsiPrimitiveType.VOID

    override val isString: Boolean
        get() = throw UnsupportedOperationException()

    override val isObject: Boolean
        get() = false

    override val isPrimitiveType: Boolean
        get() = true

    override val arguments: List<UTypeProjection>
        get() = emptyList()

    override fun resolve(context: UastContext) = null

    override val fqName: String?
        get() = null

    override val annotations by org.jetbrains.uast.java.lz { psi.getAnnotations(this) }
}