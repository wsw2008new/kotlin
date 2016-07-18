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

package org.jetbrains.kotlin.resolve.calls.smartcasts

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValue.Kind.OTHER
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValue.Kind.STABLE_VALUE
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue

interface IdentifierInfo {

    val id: Any? get() = null

    val kind: DataFlowValue.Kind get() = OTHER

    object NO : IdentifierInfo {
        override fun toString() = "NO_IDENTIFIER_INFO"
    }

    object NullId

    object NULL : IdentifierInfo {
        override val id = NullId
    }

    object ErrorId

    object ERROR : IdentifierInfo {
        override val id = ErrorId
    }

    data class Variable(override val id: VariableDescriptor, override val kind: DataFlowValue.Kind) : IdentifierInfo

    data class Receiver(override val id: ReceiverValue) : IdentifierInfo {
        override val kind = STABLE_VALUE
    }

    data class PackageOrClass(override val id: DeclarationDescriptor) : IdentifierInfo {
        override val kind = STABLE_VALUE
    }

    data class QualifiedId(val receiverInfo: IdentifierInfo, val selectorInfo: IdentifierInfo, val safe: Boolean) {
        val kind: DataFlowValue.Kind get() = if (receiverInfo.kind.isStable()) selectorInfo.kind else OTHER
    }

    data class Qualified(override val id: QualifiedId) : IdentifierInfo {
        override val kind: DataFlowValue.Kind get() = id.kind
    }

    companion object {

        fun qualified(receiverInfo: IdentifierInfo?, selectorInfo: IdentifierInfo, safe: Boolean): IdentifierInfo {
            val receiverId = receiverInfo?.id
            val selectorId = selectorInfo.id
            return if (selectorId == null || receiverInfo === NO) {
                NO
            }
            else if (receiverId == null || receiverInfo == null || receiverInfo is PackageOrClass) {
                selectorInfo
            }
            else {
                Qualified(QualifiedId(receiverInfo, selectorInfo, safe))
            }
        }
    }
}