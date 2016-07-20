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

@file:JvmName("KTypesJvm")
package kotlin.reflect.jvm

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KotlinReflectionInternalError

/**
 * Returns the [KClass] instance representing the runtime class to which this type is erased to on JVM.
 */
val KType.jvmErasure: KClass<*>
    get() = classifier.let { classifier ->
        when (classifier) {
            is KClass<*> -> classifier
            is KTypeParameter -> TODO("Type parameter classifiers are not yet supported")
            else -> throw KotlinReflectionInternalError("Cannot calculate JVM erasure for type: $this")
        }
    }
