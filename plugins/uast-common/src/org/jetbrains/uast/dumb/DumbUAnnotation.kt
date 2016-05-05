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

package org.jetbrains.uast.dumb

import org.jetbrains.uast.*
import java.util.*

class DumbUAnnotation(
        override val fqName: String,
        values: Map<String, UConstantValue<*>>,
        override val valueArguments: List<UNamedExpression>,
        override val parent: UElement?
) : UAnnotation {
    private val values = Collections.unmodifiableMap(values)

    override fun getValue(name: String?) = if (name == null) {
        values.values.first()
    } else {
        values[name]
    }

    override fun getValues() = values

    override val name = fqName.substringAfterLast(".")

    override val nameElement: UElement?
        get() = null

    override fun resolve(context: UastContext) = null
}