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

@file:JvmMultifileClass
@file:JvmName("UastUtils")
package org.jetbrains.uast

fun UExpression.asQualifiedPath(): List<String>? {
    var error = false
    val list = mutableListOf<String>()
    fun addIdentifiers(expr: UQualifiedExpression) {
        val receiver = expr.receiver
        val selector = expr.selector as? USimpleReferenceExpression ?: run { error = true; return }
        when (receiver) {
            is UQualifiedExpression -> addIdentifiers(receiver)
            is USimpleReferenceExpression -> list += receiver.identifier
            else -> {
                error = true
                return
            }
        }
        list += selector.identifier
    }
    when (this) {
        is UQualifiedExpression -> addIdentifiers(this)
        is USimpleReferenceExpression -> listOf(identifier)
        else -> return null
    }
    return if (error || list.isEmpty()) null else list
}

fun UExpression.matchesQualified(fqName: String): Boolean {
    val identifiers = this.asQualifiedPath() ?: return false
    val passedIdentifiers = fqName.trim('.').split('.')
    return identifiers == passedIdentifiers
}

fun UExpression.startsWithQualified(fqName: String): Boolean {
    val identifiers = this.asQualifiedPath() ?: return false
    val passedIdentifiers = fqName.trim('.').split('.')
    identifiers.forEachIndexed { i, identifier ->
        if (identifier != passedIdentifiers[i]) return false
    }
    return true
}

fun UExpression.endsWithQualified(fqName: String): Boolean {
    val identifiers = this.asQualifiedPath() ?: return false
    val passedIdentifiers = fqName.trim('.').split('.')
    identifiers.forEachIndexed { i, identifier ->
        if (identifier != passedIdentifiers[i]) return false
    }
    return true
}

tailrec fun UQualifiedExpression.getCallElementFromQualified(): UCallExpression? {
    val selector = this.selector
    return when (selector) {
        is UQualifiedExpression -> selector.getCallElementFromQualified()
        is UCallExpression -> selector
        else -> null
    }
}

fun UCallExpression.getQualifiedCallElement(): UExpression {
    fun findParent(element: UExpression?): UExpression? = when (element) {
        is UQualifiedExpression -> findParent(element.parent as? UExpression) ?: element
        else -> null
    }

    return findParent(parent as? UExpression) ?: this
}