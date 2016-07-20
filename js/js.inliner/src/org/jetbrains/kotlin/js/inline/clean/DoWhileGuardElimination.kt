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

package org.jetbrains.kotlin.js.inline.clean

import com.google.dart.compiler.backend.js.ast.*

internal class DoWhileGuardElimination(private val root: JsStatement) {
    private val guardLabels = mutableSetOf<JsName>()
    private var hasChanges = false
    private val loopGuardMap = mutableMapOf<JsDoWhile, JsLabel>()
    private val guardToLoopLabel = mutableMapOf<JsName, JsName?>()
    private var currentGuard: JsName? = null

    fun apply(): Boolean {
        analyze()
        perform()
        return hasChanges
    }

    private fun analyze() {
        object : RecursiveJsVisitor() {
            override fun visitLabel(x: JsLabel) {
                val statement = x.statement
                if (statement is JsDoWhile) {
                    processDoWhile(statement, x.name)
                }
                else {
                    super.visitLabel(x)
                }
            }

            override fun visitDoWhile(x: JsDoWhile) = processDoWhile(x, null)

            private fun processDoWhile(x: JsDoWhile, label: JsName?) {
                val body = x.body
                val guard = when (body) {
                    is JsBlock -> {
                        val firstStatement = body.statements.first()
                        if (firstStatement is JsLabel && body.statements.size == 1) {
                            firstStatement
                        }
                        else {
                            null
                        }
                    }
                    is JsLabel -> body
                    else -> null
                }

                if (guard != null) {
                    guardLabels += guard.name
                    loopGuardMap[x] = guard
                    guardToLoopLabel[guard.name] = label
                }

                withCurrentGuard(guard?.name) { super.visitDoWhile(x) }
            }

            override fun visitBreak(x: JsBreak) {
                val guardLabel = x.label?.name ?: return

                val loopLabel = guardToLoopLabel[guardLabel]
                if (loopLabel == null && currentGuard != guardLabel) {
                    guardLabels -= guardLabel
                }
            }

            override fun visitWhile(x: JsWhile) {
                withCurrentGuard(null) { super.visitWhile(x) }
            }

            override fun visitFor(x: JsFor) {
                withCurrentGuard(null) { super.visitFor(x) }
            }

            override fun visitForIn(x: JsForIn) {
                withCurrentGuard(null) { super.visitForIn(x) }
            }

            private inline fun withCurrentGuard(guard: JsName?, action: () -> Unit) {
                val oldGuard = currentGuard
                currentGuard = guard
                action()
                currentGuard = oldGuard
            }

            override fun visitFunction(x: JsFunction) { }
        }.accept(root)
    }

    private fun perform() {
        object : JsVisitorWithContextImpl() {
            override fun visit(x: JsDoWhile, ctx: JsContext<JsNode>): Boolean {
                loopGuardMap[x]?.let { guard ->
                    if (guard.name in guardLabels) {
                        x.body = accept(guard.statement)
                        hasChanges = true
                        return false
                    }
                }
                return super.visit(x, ctx)
            }

            override fun visit(x: JsBreak, ctx: JsContext<JsNode>): Boolean {
                val name = x.label?.name
                if (name in guardLabels) {
                    val target = guardToLoopLabel[name]
                    ctx.replaceMe(JsContinue(target?.makeRef()))
                    hasChanges = true
                }
                return false
            }
        }.accept(root)
    }
}
