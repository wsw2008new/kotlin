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

import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.uast.internal.getUastValue
import org.jetbrains.uast.*
import org.jetbrains.uast.baseElements.UConstantValue
import org.jetbrains.uast.psi.PsiElementBacked

class KotlinUAnnotation(
        override val psi: KtAnnotationEntry,
        override val parent: UElement?
) : KotlinAbstractUElement(), UAnnotation, PsiElementBacked {
    private val bindingContext by lz { psi.analyze(BodyResolveMode.PARTIAL) }

    private val declarationDescriptor by lz {
        val resolvedCall = psi.calleeExpression?.getResolvedCall(bindingContext) ?: return@lz null
        (resolvedCall.resultingDescriptor as? ConstructorDescriptor)?.containingDeclaration
    }

    private val annotationDescriptor by lz {
        bindingContext[BindingContext.ANNOTATION, psi]
    }

    override val fqName: String?
        get() = declarationDescriptor?.fqNameSafe?.asString()

    override val name: String
        get() = (psi.typeReference?.typeElement as? KtUserType)?.referencedName.orAnonymous()

    override val valueArguments by lz {
        psi.valueArguments.map {
            val name = it.getArgumentName()?.asName?.identifier.orAnonymous()
            UNamedExpression(name, this).apply {
                expression = KotlinConverter.convertOrEmpty(it.getArgumentExpression(), this)
            }
        }
    }

    override fun getValue(name: String): UConstantValue<*>? {
        val descriptor = annotationDescriptor ?: return null
        for ((key, value) in descriptor.allValueArguments) {
            if (key.name.asString() == name) {
                return value.getUastValue(psi.project)
            }
        }

        return null
    }

    override fun getValues(): Map<String, UConstantValue<*>> {
        val descriptor = annotationDescriptor ?: return emptyMap()
        val values = mutableMapOf<String, UConstantValue<*>>()
        val project = psi.project
        for ((key, value) in descriptor.allValueArguments) {
            values.put(key.name.asString(), value.getUastValue(project))
        }
        return values
    }

    override fun resolve(context: UastContext): UClass? {
        val classDescriptor = declarationDescriptor ?: return null
        val source = classDescriptor.toSource() ?: return null
        return context.convert(source) as? UClass
    }
}