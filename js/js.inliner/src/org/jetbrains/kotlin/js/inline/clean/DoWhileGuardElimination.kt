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
    private val doWhileGuards = mutableSetOf<JsName>()
    private var hasChanges = false
    private var currentLoopLabel: JsName? = null
    private val loopGuardMap = mutableMapOf<JsDoWhile, Pair<JsName, JsStatement>>()
    private val labelGuardMap = mutableMapOf<JsName, JsName?>()
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
                    currentLoopLabel = x.name
                }

                super.visitLabel(x)
            }

            override fun visitDoWhile(x: JsDoWhile) {
                val body = x.body
                val (candidate, guardedBody) = when (body) {
                    is JsBlock -> {
                        val firstStatement = body.statements.first()
                        if (firstStatement is JsLabel && body.statements.size == 1) {
                            Pair(firstStatement.name, firstStatement.statement)
                        }
                        else {
                            Pair(null, null)
                        }
                    }
                    is JsLabel -> Pair(body.name, body.statement)
                    else -> Pair(null, null)
                }

                if (candidate != null) {
                    doWhileGuards += candidate
                    loopGuardMap[x] = Pair(candidate, guardedBody!!)
                    labelGuardMap[candidate] = currentLoopLabel
                }

                withCurrentGuard(candidate) { super.visitDoWhile(x) }
            }

            override fun visitBreak(x: JsBreak) {
                val guardName = x.label?.name ?: currentGuard
                if (guardName != null) {
                    val loopLabel = labelGuardMap[guardName]
                    if (loopLabel == null && currentGuard != guardName) {
                        doWhileGuards -= guardName
                    }
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
                loopGuardMap[x]?.let {
                    val (guardName, guardedBody) = it
                    if (guardName in doWhileGuards) {
                        x.body = accept(guardedBody)
                        hasChanges = true
                        return false
                    }
                }
                return super.visit(x, ctx)
            }

            override fun visit(x: JsBreak, ctx: JsContext<JsNode>): Boolean {
                val name = x.label?.name
                if (name in doWhileGuards) {
                    val target = labelGuardMap[name]
                    ctx.replaceMe(JsContinue(target?.makeRef()))
                    hasChanges = true
                }
                return false
            }
        }.accept(root)
    }
}
