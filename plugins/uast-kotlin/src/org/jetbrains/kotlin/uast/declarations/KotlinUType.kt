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

package org.jetbrains.kotlin.uast

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.name.FqNameUnsafe
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.uast.*
import org.jetbrains.uast.declarations.UClassType
import org.jetbrains.uast.declarations.UResolvedType
import org.jetbrains.uast.kinds.UastVariance
import org.jetbrains.uast.psi.PsiElementBacked


class KotlinUType(
        val type: KotlinType,
        val project: Project,
        override val psi: PsiElement? = null
) : KotlinAbstractUElement(), UType, PsiElementBacked {
    override val name: String
        get() = type.toString()

    override val fqName: String?
        get() = type.constructor.declarationDescriptor?.fqNameSafe?.asString()
    
    override val isBoolean: Boolean
        get() = checkType(KotlinBuiltIns.FQ_NAMES._boolean)

    override val isInt: Boolean
        get() = checkType(KotlinBuiltIns.FQ_NAMES._int)

    override val isShort: Boolean
        get() = checkType(KotlinBuiltIns.FQ_NAMES._short)

    override val isLong: Boolean
        get() = checkType(KotlinBuiltIns.FQ_NAMES._long)

    override val isFloat: Boolean
        get() = checkType(KotlinBuiltIns.FQ_NAMES._float)

    override val isDouble: Boolean
        get() = checkType(KotlinBuiltIns.FQ_NAMES._double)

    override val isChar: Boolean
        get() = checkType(KotlinBuiltIns.FQ_NAMES._char)

    override val isByte: Boolean
        get() = checkType(KotlinBuiltIns.FQ_NAMES._byte)

    override val isString: Boolean
        get() = checkType(KotlinBuiltIns.FQ_NAMES.string)

    override val isObject: Boolean
        get() = checkType(KotlinBuiltIns.FQ_NAMES.any)

    override val isArray: Boolean
        get() = checkType(KotlinBuiltIns.FQ_NAMES.array)

    private fun checkType(fqNameUnsafe: FqNameUnsafe): Boolean {
        val fqName = fqNameUnsafe.toSafe()
        val descriptor = type.constructor.declarationDescriptor
        return descriptor is ClassDescriptor
               && descriptor.getName() == fqName.shortName()
               && fqName == DescriptorUtils.getFqName(descriptor).toSafe()
    }

    override fun matchesFqName(fqName: String): Boolean {
        return when (fqName) {
            "java.lang.CharSequence" -> super.matchesFqName(fqName) ||
                                        super.matchesFqName(KotlinBuiltIns.FQ_NAMES.charSequence.asString())
            "java.lang.String" -> super.matchesFqName(fqName) ||
                                  super.matchesFqName(KotlinBuiltIns.FQ_NAMES.string.asString())
            else -> super.matchesFqName(fqName)
        }
    }

    //TODO
    override fun resolve(): UResolvedType {
        return UastErrorResolvedType
    }

    //TODO support descriptor annotations
    override val annotations = emptyList<UAnnotation>()
}

class KotlinUTypeProjection(val projection: TypeProjection, project: Project) : UTypeProjection {
    override val type by lz { KotlinConverter.convertType(projection.type, project, null) }
    override val variance: UastVariance
        get() {
            if (projection.isStarProjection) return UastVariance.UNKNOWN
            return when (projection.projectionKind) {
                Variance.INVARIANT -> UastVariance.INVARIANT
                Variance.IN_VARIANCE -> UastVariance.CONTRAVARIANT
                Variance.OUT_VARIANCE -> UastVariance.COVARIANT
            }
        }
}