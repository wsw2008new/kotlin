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

class DumbUQualifiedExpression() : UQualifiedExpression {
    override lateinit var receiver: UExpression
    override lateinit var selector: UExpression
    override var parent: UElement? = null

    constructor(receiver: UExpression, selector: UExpression, parent: UElement?) : this() {
        this.receiver = receiver
        this.selector = selector
        this.parent = parent
    }

    override fun resolve(context: UastContext) = null
    override val accessType: UastQualifiedExpressionAccessType
        get() = UastQualifiedExpressionAccessType.SIMPLE

    companion object {
        @JvmStatic
        fun make(fqName: String, parent: UElement?): UExpression {
            assert(fqName.isNotBlank())

            val parts = fqName.split('.')
            if (parts.size == 1) {
                return DumbUSimpleReferenceExpression(parts.first(), parent)
            } else {
                fun make(parts: List<String>, last: String, parent: UElement?): UExpression {
                    return DumbUQualifiedExpression().apply {
                        receiver = if (parts.size == 1)
                            make(parts.first(), this)
                        else
                            make(parts.dropLast(1), parts.last(), parent)

                        selector = make(last, this)
                        this.parent = parent
                    }
                }

                return make(parts.dropLast(1), parts.last(), parent)
            }
        }
    }
}