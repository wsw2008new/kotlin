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

open class IdentifierInfo(open val id: Any?, val kind: DataFlowValue.Kind) {
    object NO : IdentifierInfo(null, OTHER) {
        override fun toString() = "NO_IDENTIFIER_INFO"
    }

    object NullId

    object NULL : IdentifierInfo(NullId, OTHER)

    object ErrorId

    object ERROR : IdentifierInfo(ErrorId, OTHER)

    class Variable(override val id: VariableDescriptor, kind: DataFlowValue.Kind) : IdentifierInfo(id, kind)

    class Receiver(override val id: ReceiverValue) : IdentifierInfo(id, STABLE_VALUE)

    class PackageOrClass(override val id: DeclarationDescriptor) : IdentifierInfo(id, STABLE_VALUE)

    data class QualifiedId(val receiverId: Any, val selectorId: Any, val safe: Boolean)

    class Qualified(override val id: QualifiedId, kind: DataFlowValue.Kind) : IdentifierInfo(id, kind)

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
                Qualified(QualifiedId(receiverId, selectorId, safe),
                          if (receiverInfo.kind.isStable()) selectorInfo.kind else OTHER)
            }
        }
    }
}