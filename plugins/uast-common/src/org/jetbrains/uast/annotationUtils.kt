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

/**
 * Find an annotation with the required qualified name.
 *
 * @param fqName the qualified name to search
 * @return [UAnnotation] element if the annotation with the specified [fqName] was found, null otherwise.
 */
fun UAnnotated.findAnnotation(fqName: String) = annotations.firstOrNull { it.fqName == fqName }

fun UFunction.getAllAnnotations(context: UastContext): List<UAnnotation> {
    val annotations = this.annotations.toMutableList()
    for (superFunction in this.getSuperFunctions(context)) {
        annotations += superFunction.annotations
    }
    return annotations
}

fun UClass.getAllAnnotations(context: UastContext): List<UAnnotation> {
    val annotations = this.annotations.toMutableList()
    for (superFunction in this.getSuperClasses(context)) {
        annotations += superFunction.annotations
    }
    return annotations
}